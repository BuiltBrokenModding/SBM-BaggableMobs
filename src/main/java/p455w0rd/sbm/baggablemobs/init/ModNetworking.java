package p455w0rd.sbm.baggablemobs.init;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import p455w0rd.sbm.baggablemobs.network.PacketConfigSync;

/**
 * @author p455w0rd
 *
 */
public class ModNetworking {

	private static SimpleNetworkWrapper INSTANCE = null;

	public static SimpleNetworkWrapper getInstance() {
		return INSTANCE;
	}

	public static void init() {
		if (INSTANCE == null) {
			INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ModGlobals.MODID);
		}
		getInstance().registerMessage(PacketConfigSync.Handler.class, PacketConfigSync.class, 0, Side.CLIENT);
	}

}
