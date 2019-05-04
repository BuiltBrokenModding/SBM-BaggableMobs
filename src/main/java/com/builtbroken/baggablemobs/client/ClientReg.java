package com.builtbroken.baggablemobs.client;

import com.builtbroken.baggablemobs.BaggableMobs;
import com.builtbroken.baggablemobs.content.ModCreativeTab;
import com.builtbroken.baggablemobs.init.ModConfig;
import com.builtbroken.baggablemobs.lib.BaggableMobsUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.function.Predicate;

/**
 * Created by Dark(DarkGuardsman, Robert) on 1/20/2019.
 */
@Mod.EventBusSubscriber(modid = BaggableMobs.MODID, value = Side.CLIENT)
public class ClientReg implements ISelectiveResourceReloadListener
{
    @SubscribeEvent
    public static void registerAllModels(ModelRegistryEvent event)
    {
        //Register model
        ModelLoader.setCustomModelResourceLocation(BaggableMobs.itemMobBag, 0, new ModelResourceLocation(BaggableMobs.itemMobBag.getRegistryName(), "inventory"));

        //Register reload listener
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new ClientReg());
    }

    @SubscribeEvent
    public static void registerItemColors(ColorHandlerEvent.Item event)
    {
        //Register colors
        event.getItemColors().registerItemColorHandler((stack, tintIndex) -> {
            return tintIndex != 2 ? BaggableMobsUtil.getCapturedMobInBag(stack) == null ? -1 : BaggableMobsUtil.getMobEggColor(BaggableMobsUtil.getCapturedMobInBag(stack), tintIndex) : -1;
        }, BaggableMobs.itemMobBag);
    }

    // A reload listener for purposes of refreshing the generated bag list in both Creative Tab and JEI from the Mog Config GUI
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        BaggableMobsUtil.CAPTURABLE_MOBS.clear();
        BaggableMobsUtil.getCapurableMobs();
        ModCreativeTab.BAG_LIST.clear();
        ModCreativeTab.getWandList();
        BaggableMobs.itemMobBag.setMaxStackSize(ModConfig.Options.MOB_BAG_MAX_STACKSIZE);
    }
}
