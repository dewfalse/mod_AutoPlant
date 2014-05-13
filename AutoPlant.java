package autoplant;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

import java.util.logging.Logger;

@Mod(modid = AutoPlant.modid, name = AutoPlant.name, version = "1.0")
public class AutoPlant {
    public static final String modid = "AutoPlant";
    public static final String name = "AutoPlant";
	@SidedProxy(clientSide = "autoplant.ClientProxy", serverSide = "autoplant.CommonProxy")
	public static CommonProxy proxy;

	@Instance("AutoPlant")
	public static AutoPlant instance;

    public static final PacketPipeline packetPipeline = new PacketPipeline();
	public static Logger logger = Logger.getLogger("Minecraft");

	public static Config config = new Config();

    @Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();
        packetPipeline.init(AutoPlant.modid);
        packetPipeline.registerPacket(PacketHandler.class);
	}

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        config.load(event.getSuggestedConfigurationFile());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        packetPipeline.postInit();
    }
}
