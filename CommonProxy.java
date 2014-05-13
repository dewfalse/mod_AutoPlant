package autoplant;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class CommonProxy  {

	void init() {
        FMLCommonHandler.instance().bus().register(new ConnectionHandler());
	}

}
