package com.builtbroken.baggablemobs;

import com.builtbroken.baggablemobs.content.BaggableMobsUtil;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by Dark(DarkGuardsman, Robert) on 1/20/2019.
 */
@Mod.EventBusSubscriber(modid = BaggableMobs.DOMAIN, value = Side.CLIENT)
public class ClientReg
{
    @SubscribeEvent
    public static void registerAllModels(ModelRegistryEvent event)
    {
        //Register model
        ModelLoader.setCustomModelResourceLocation(BaggableMobs.itemMobBag, 0, new ModelResourceLocation(BaggableMobs.itemMobBag.getRegistryName(), "inventory"));
    }

    @SubscribeEvent
    public static void registerItemColors(ColorHandlerEvent.Item event)
    {
        //Register colors
        event.getItemColors().registerItemColorHandler((stack, tintIndex) -> {
            return tintIndex != 2 ? BaggableMobsUtil.getMobID(stack) == null ? -1 : BaggableMobsUtil.getMobEggColor(BaggableMobsUtil.getMobID(stack), tintIndex) : -1;
        }, BaggableMobs.itemMobBag);
    }
}
