package autoplant;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		if(packet.channel != Config.channel) {
			return;
		}

		DataInputStream stream = new DataInputStream(new ByteArrayInputStream(packet.data));
		try {
			String command = stream.readUTF();

			if(command.equalsIgnoreCase(EnumCommand.PLANT.toString())) {
				int index = stream.readInt();
				int x = stream.readInt();
				int y = stream.readInt();
				int z = stream.readInt();

				setBlock(player, index, x, y, z);
			}
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

	private void setBlock(Player player, int index, int x, int y, int z) {
		EntityPlayerMP thePlayer = (EntityPlayerMP) player;
		World theWorld = thePlayer.worldObj;
		ItemStack itemStack = thePlayer.inventory.getStackInSlot(index);
		if(theWorld.setBlockAndMetadataWithNotify(x, y, z, itemStack.itemID, itemStack.getItemDamage(), 3)) {
			itemStack.stackSize--;
			thePlayer.inventory.setInventorySlotContents(index, itemStack);
			thePlayer.inventory.onInventoryChanged();
			thePlayer.inventoryContainer.detectAndSendChanges();
		}

	}

}
