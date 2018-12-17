package p455w0rd.sbm.baggablemobs.proxy;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import p455w0rd.sbm.baggablemobs.init.ModConfig;
import p455w0rd.sbm.baggablemobs.init.ModNetworking;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		ModConfig.init();
		ModNetworking.init();
	}

	public void init(FMLInitializationEvent event) {
	}

	public void postInit(FMLPostInitializationEvent event) {
	}

	public void serverStarting(FMLServerStartingEvent event) {
	}

	public void playerConnected(PlayerLoggedInEvent event) {
		ModConfig.sendToPlayer((EntityPlayerMP) event.player);
	}

	public void refreshResources() {
	}

}
