package com.builtbroken.baggablemobs;

import com.builtbroken.baggablemobs.content.CommandGenerateEntityList;
import com.builtbroken.baggablemobs.content.ItemMobBag;
import com.builtbroken.baggablemobs.content.ModCreativeTab;
import com.builtbroken.baggablemobs.init.ModConfig;
import com.builtbroken.baggablemobs.lib.network.PacketHandler;
import com.builtbroken.baggablemobs.proxy.CommonProxy;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = BaggableMobs.MODID)
@Mod(modid = BaggableMobs.MODID, name = BaggableMobs.NAME, version = BaggableMobs.VERSION, guiFactory = BaggableMobs.GUI_FACTORY, acceptedMinecraftVersions = "1.12")
public class BaggableMobs
{
    public static final String VERSION = "1.0.0";
    public static final String NAME = "Baggable Mobs";
    public static final String MODID = "baggablemobs";
    public static final String GUI_FACTORY = "com.builtbroken.baggablemobs.init.ModGuiFactory";
    public static final String CONFIG_FILE = "config/BaggableMobs.cfg";

    @SidedProxy(clientSide = "com.builtbroken.baggablemobs.proxy.ClientProxy", serverSide = "com.builtbroken.baggablemobs.proxy.CommonProxy")
    public static CommonProxy PROXY;

    @Instance(MODID)
    public static BaggableMobs INSTANCE;

    public static ItemMobBag itemMobBag;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        ModConfig.init();
        PacketHandler.init();
        ModCreativeTab.init();
    }

    @SubscribeEvent
    public static void onItemRegistryReady(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(itemMobBag = new ItemMobBag());
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        PROXY.init(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        PROXY.postInit(event);
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        if (FMLCommonHandler.instance().getSide().isClient()) {
            event.registerServerCommand(new CommandGenerateEntityList());
        }
    }
}
