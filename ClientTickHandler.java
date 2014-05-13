package autoplant;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class ClientTickHandler  {

	int prev_blockHitWait = 0;
	int targetBlockId = 0;
	int targetBlockMetadata = 0;
	Coord blockCoord = new Coord();
	int sideHit = 0;

	Queue<Coord> nextTarget = new LinkedList<Coord>();
	Set<Coord> vectors = new LinkedHashSet();

	int count = 0;

    @SubscribeEvent
	public void tickEnd(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !FMLClientHandler.instance().isGUIOpen(GuiChat.class)) {
			onTickInGame();
		}
	}

	private int getPlantIndex() {
		Minecraft mc = Minecraft.getMinecraft();
        if(mc == null) return -1;
        if(mc.thePlayer == null) return -1;
        if(mc.thePlayer.inventory == null) return -1;
		int max = AutoPlant.config.use_inventory ? mc.thePlayer.inventory.mainInventory.length : 9;
		for(int iInventory = 0; iInventory < max; iInventory++) {
			ItemStack itemStack = mc.thePlayer.inventory.mainInventory[iInventory];
			if(itemStack == null) {
				continue;
			}
            Block block = Block.getBlockFromItem(itemStack.getItem());
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

		int index = getPlantIndex();
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
                    Block block = mc.theWorld.getBlock(x, y, z);
                    Block underBlock = mc.theWorld.getBlock(x, y-1, z);
                    boolean b = block.isAir(mc.theWorld, x, y, z);
					if(b == false) continue;


					if(underBlock != Block.getBlockFromName("grass") && underBlock != Block.getBlockFromName("dirt")) continue;
					ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(index);
					if(mc.theWorld.setBlock(x, y, z, Block.getBlockFromItem(itemStack.getItem()), itemStack.getItemDamage(), 3)) {
						sendPacket(EnumCommand.PLANT, index, new Coord(x,y,z));
						return;
					}
				}
			}
		}
	}

	private void sendPacket(EnumCommand command, int index, Coord pos) {
        AutoPlant.packetPipeline.sendPacketToServer(new PacketHandler(command, index, pos));
	}

}
