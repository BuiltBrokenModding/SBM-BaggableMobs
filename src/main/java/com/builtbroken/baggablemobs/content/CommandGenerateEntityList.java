package com.builtbroken.baggablemobs.content;

import com.builtbroken.baggablemobs.lib.BaggableMobsUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

/**
 * @author p455w0rd
 *
 */
public class CommandGenerateEntityList
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal("genentitylist").requires(CommandGenerateEntityList::requirement).executes(CommandGenerateEntityList::executor));
        dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal("gel").requires(CommandGenerateEntityList::requirement).executes(CommandGenerateEntityList::executor));
    }

    private static boolean requirement(CommandSource src)
    {
        try
        {
            return ServerLifecycleHooks.getCurrentServer().isDedicatedServer() || src.asPlayer().isCreative() || ServerLifecycleHooks.getCurrentServer().getPlayerList().getOppedPlayers().getEntry(src.asPlayer().getGameProfile()) != null;
        }
        catch(CommandSyntaxException e)
        {
            return false;
        }
    }

    private static int executor(CommandContext<CommandSource> ctx)
    {
        try
        {
            BaggableMobsUtil.generateEntityList(ctx.getSource().asPlayer());
        }
        catch(CommandSyntaxException e) {}

        return 0;
    }
}
