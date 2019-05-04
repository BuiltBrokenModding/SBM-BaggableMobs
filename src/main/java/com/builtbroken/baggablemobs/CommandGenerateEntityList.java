package com.builtbroken.baggablemobs;

import com.google.common.collect.Lists;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
		generateEntityList((EntityPlayer) sender);
	}

	public static void generateEntityList(EntityPlayer player)
	{
		final String filename = "EntityList.txt";
		File entityListFile = new File(filename);
		if (entityListFile.exists())
		{
			entityListFile.delete();
		}
		List<String> fileContents = new ArrayList<>();
		fileContents.add("Registry Name - Class Name - Entity Name");
		fileContents.add("========================================");
		for (EntityEntry entity : ForgeRegistries.ENTITIES.getValuesCollection())
		{
			if (EntityLiving.class.isAssignableFrom(entity.getEntityClass()))
			{
				fileContents.add(entity.getRegistryName().toString() + " - " + entity.getEntityClass().getSimpleName().toString() + ".class - " + I18n.translateToLocal("entity." + entity.getName() + ".name"));
			}
		}
		try
		{
			FileUtils.writeLines(entityListFile, "UTF-8", fileContents, false);
			player.sendMessage(new TextComponentString("Generated entity list file at " + TextFormatting.ITALIC + "" + entityListFile.getAbsolutePath()));
		}
		catch (IOException e)
		{
		}
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
