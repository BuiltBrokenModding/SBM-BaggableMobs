package com.builtbroken.baggablemobs.init;

import com.builtbroken.baggablemobs.BaggableMobs;
import com.builtbroken.baggablemobs.lib.BaggableMobsUtil;
import com.builtbroken.baggablemobs.init.ModConfig.Options;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

/**
 * @author p455w0rd
 */
@EventBusSubscriber(modid = BaggableMobs.MODID)
public class ModEvents
{
    @SubscribeEvent
    public static void onEntityInteract(EntityInteract event)
    {
        if (event.getEntityPlayer() != null && event.getEntityPlayer().getEntityWorld() != null)
        {
            EntityPlayer player = event.getEntityPlayer();
            Entity target = event.getTarget();
            ItemStack heldItem = player.getHeldItemMainhand();
            if (!heldItem.isEmpty() && heldItem.getItem() == BaggableMobs.itemMobBag && target instanceof EntityCreature)
            {
                if (Options.DISABLE_CAPTURING_HOSTILE_MOBS && target instanceof EntityMob || !target.isNonBoss())
                {
                    return;
                }
                BaggableMobsUtil.storeMobInBag(player, (EntityLivingBase) target, !player.capabilities.isCreativeMode);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent e)
    {
        if (e.getModID().equals(BaggableMobs.MODID))
        {
            ModConfig.init();
            BaggableMobs.PROXY.refreshResources();
        }
    }

    @SubscribeEvent
    public static void playerConnected(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (!event.player.world.isRemote)
        {
            ModConfig.sendToPlayer((EntityPlayerMP) event.player);
        }
    }
}
