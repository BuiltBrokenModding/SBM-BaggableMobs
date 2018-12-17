package p455w0rd.sbm.baggablemobs.client.command;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import p455w0rd.sbm.baggablemobs.util.BaggableMobsUtil;

/**
 * @author p455w0rd
 *
 */
public class CommandGenerateEntityList implements ICommand {

	@Override
	public int compareTo(ICommand o) {
		return 0;
	}

	@Override
	public String getName() {
		return "genentitylist";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/" + getName();
	}

	@Override
	public List<String> getAliases() {
		return Lists.newArrayList("genentitylist", "gel");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		BaggableMobsUtil.generateEntityList((EntityPlayer) sender);
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		if (sender instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) sender;
			return !FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer() || player.isCreative() || FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getOppedPlayers().getEntry(player.getGameProfile()) != null;
		}
		return false;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return false;
	}

}
