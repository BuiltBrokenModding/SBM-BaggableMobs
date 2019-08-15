package com.builtbroken.baggablemobs.init;

import com.builtbroken.baggablemobs.BaggableMobs;
import com.builtbroken.baggablemobs.lib.BaggableMobsUtil;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
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
        if (event.getPlayer() != null && event.getPlayer().getEntityWorld() != null)
        {
            PlayerEntity player = event.getPlayer();
            Entity target = event.getTarget();
            ItemStack heldItem = player.getHeldItemMainhand();
            if (!heldItem.isEmpty() && heldItem.getItem() == BaggableMobs.itemMobBag && target instanceof CreatureEntity)
            {
                if (BaggableMobsConfig.CONFIG.DISABLE_CAPTURING_HOSTILE_MOBS.get() && target instanceof MonsterEntity || !target.isNonBoss())
                {
                    return;
                }
                BaggableMobsUtil.storeMobInBag(player, (LivingEntity) target, !player.isCreative());
                event.setCanceled(true);
            }
        }
    }
}
