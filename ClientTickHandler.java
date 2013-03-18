package autoplant;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ClientTickHandler implements ITickHandler {

	int prev_blockHitWait = 0;
	int targetBlockId = 0;
	int targetBlockMetadata = 0;
	Coord blockCoord = new Coord();
	int sideHit = 0;

	Queue<Coord> nextTarget = new LinkedList<Coord>();
	Set<Coord> vectors = new LinkedHashSet();

	int count = 0;

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if (type.equals(EnumSet.of(TickType.CLIENT))) {
			GuiScreen guiscreen = Minecraft.getMinecraft().currentScreen;
			if (guiscreen == null) {
				onTickInGame();
			}
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

	@Override
	public String getLabel() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return null;
	}

	private int getTorchIndex() {
		Minecraft mc = Minecraft.getMinecraft();
		int max = AutoPlant.config.use_inventory ? mc.thePlayer.inventory.mainInventory.length : 9;
		for(int iInventory = 0; iInventory < max; iInventory++) {
			ItemStack itemStack = mc.thePlayer.inventory.mainInventory[iInventory];
			if(itemStack == null) {
				continue;
			}
			Block block = Block.blocksList[itemStack.itemID];
			if(block == null) {
				continue;
			}
			if(block instanceof BlockSapling) {
				return iInventory;
			}
		}
		return -1;
	}

	public void onTickInGame() {
		if(AutoPlant.config.getMode() == EnumMode.OFF) {
			return;
		}
		Minecraft mc = Minecraft.getMinecraft();

		int index = getTorchIndex();
		if(index == -1) {
			return;
		}

		int distance = AutoPlant.config.distance;
		int width = distance / 2 + 1;
		int posX = (int)Math.round(mc.thePlayer.posX);
		int posY = (int)Math.round(mc.thePlayer.posY);
		int posZ = (int)Math.round(mc.thePlayer.posZ);
		for(int x = posX - width; x <= posX + width; ++x) {
			for(int z = posZ - width; z <= posZ + width; ++z) {
				boolean setTorch = false;
				for(int y = posY - 3; y <= posY + 1; ++y) {
					int x_mod = x % distance;
					if(x_mod < 0) {
						x_mod += distance;
					}
					int z_mod = z % distance;
					if(z_mod < 0) {
						z_mod += distance;
					}
					if( (x_mod != AutoPlant.config.x_plus) || (z_mod != AutoPlant.config.z_plus) ) {
						continue;
					}
					int blockID = mc.theWorld.getBlockId(x, y, z);
					int underBlockID = mc.theWorld.getBlockId(x, y-1, z);
					if(blockID != 0) continue;
					if(underBlockID != Block.grass.blockID && underBlockID != Block.dirt.blockID) continue;
					ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(index);
					if(mc.theWorld.setBlockAndMetadataWithNotify(x, y, z, itemStack.itemID, itemStack.getItemDamage(), 3)) {
						sendPacket(EnumCommand.PLANT, index, new Coord(x,y,z));
						itemStack.stackSize--;
						mc.thePlayer.inventory.setInventorySlotContents(index, itemStack);
						return;
					}
				}
			}
		}
	}

	private void sendPacket(EnumCommand command, int index, Coord pos) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(bytes);
		try {
			stream.writeUTF(command.toString());
			stream.writeInt(index);
			stream.writeInt(pos.x);
			stream.writeInt(pos.y);
			stream.writeInt(pos.z);

			Packet250CustomPayload packet = new Packet250CustomPayload();
			packet.channel = Config.channel;
			packet.data = bytes.toByteArray();
			packet.length = packet.data.length;
			Minecraft mc = Minecraft.getMinecraft();
			mc.thePlayer.sendQueue.addToSendQueue(packet);
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

}
