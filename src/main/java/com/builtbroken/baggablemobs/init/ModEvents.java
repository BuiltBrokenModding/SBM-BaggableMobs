package com.builtbroken.baggablemobs.init;

import com.builtbroken.baggablemobs.BaggableMobs;
import com.builtbroken.baggablemobs.lib.BaggableMobsUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

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
                if (BaggableMobsConfig.CONFIG.DISABLE_CAPTURING_HOSTILE_MOBS.get() && target instanceof EntityMob || !target.isNonBoss())
                {
                    return;
                }
                BaggableMobsUtil.storeMobInBag(player, (EntityLivingBase) target, !player.isCreative());
                event.setCanceled(true);
            }
        }
    }
}
