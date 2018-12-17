package p455w0rd.sbm.baggablemobs.init;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.sbm.baggablemobs.client.command.CommandGenerateEntityList;

/**
 * @author p455w0rd
 *
 */
public class ModCommands {

	// Command for generating a text file filled with all living entities availble in the current instance
	private static final CommandGenerateEntityList GEN_ENTITY_LIST = new CommandGenerateEntityList();

	@SideOnly(Side.CLIENT)
	public static class Client {
		public static void register(FMLServerStartingEvent event) {
			if (FMLCommonHandler.instance().getSide().isClient()) {
				event.registerServerCommand(GEN_ENTITY_LIST);
			}
		}
	}

}
