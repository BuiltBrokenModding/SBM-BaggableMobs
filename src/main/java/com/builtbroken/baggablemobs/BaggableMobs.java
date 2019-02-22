package com.builtbroken.baggablemobs;

import com.builtbroken.baggablemobs.content.CommandGenerateEntityList;
import com.builtbroken.baggablemobs.content.ItemMobBag;
import com.builtbroken.baggablemobs.content.ModItemGroup;
import com.builtbroken.baggablemobs.init.BaggableMobsConfig;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod.EventBusSubscriber(bus=Bus.MOD)
@Mod(BaggableMobs.MODID)
public class BaggableMobs
{
    public static final String VERSION = "1.0.0";
    public static final String NAME = "Baggable Mobs";
    public static final String MODID = "baggablemobs";

    public static ItemMobBag itemMobBag;

    public static final String PROTOCOL_VERSION = "1.0";
    public static SimpleChannel channel = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, MODID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public BaggableMobs()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, BaggableMobsConfig.CONFIG_SPEC);
        MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
        ModItemGroup.init();
    }

    @SubscribeEvent
    public static void onItemRegistryReady(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(itemMobBag = new ItemMobBag());
    }

    public void serverStarting(FMLServerStartingEvent event)
    {
        CommandGenerateEntityList.register(event.getCommandDispatcher());
    }
}
