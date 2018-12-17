package p455w0rd.sbm.baggablemobs;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import p455w0rd.sbm.baggablemobs.init.ModGlobals;
import p455w0rd.sbm.baggablemobs.proxy.CommonProxy;

@Mod(modid = ModGlobals.MODID, name = ModGlobals.NAME, version = ModGlobals.VERSION, dependencies = ModGlobals.DEPENDANCIES, guiFactory = ModGlobals.GUI_FACTORY, acceptedMinecraftVersions = "1.12")
public class BaggableMobs {

	@SidedProxy(clientSide = ModGlobals.CLIENT_PROXY, serverSide = ModGlobals.SERVER_PROXY)
	public static CommonProxy PROXY;

	@Instance(ModGlobals.MODID)
	public static BaggableMobs INSTANCE;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		PROXY.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		PROXY.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		PROXY.postInit(event);
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		PROXY.serverStarting(event);
	}

	@EventHandler
	public void playerConnected(PlayerLoggedInEvent event) {
		PROXY.playerConnected(event);
	}

}
