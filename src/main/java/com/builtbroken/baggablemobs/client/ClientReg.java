package com.builtbroken.baggablemobs.client;

import com.builtbroken.baggablemobs.BaggableMobs;
import com.builtbroken.baggablemobs.lib.BaggableMobsUtil;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Created by Dark(DarkGuardsman, Robert) on 1/20/2019.
 */
@Mod.EventBusSubscriber(modid = BaggableMobs.MODID, value = Dist.CLIENT)
public class ClientReg
{
    @SubscribeEvent
    public static void registerItemColors(ColorHandlerEvent.Item event)
    {
        //Register colors
        event.getItemColors().register((stack, tintIndex) -> {
            return tintIndex != 2 ? BaggableMobsUtil.getCapturedMobInBag(stack) == null ? -1 : BaggableMobsUtil.getMobEggColor(BaggableMobsUtil.getCapturedMobInBag(stack), tintIndex) : -1;
        }, BaggableMobs.itemMobBag);
    }
}
