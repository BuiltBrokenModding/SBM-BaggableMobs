package com.builtbroken.baggablemobs;

import com.builtbroken.baggablemobs.config.ConfigMain;
import com.builtbroken.baggablemobs.content.ItemMobBag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = BaggableMobs.DOMAIN)
@Mod(modid = BaggableMobs.DOMAIN, name = BaggableMobs.NAME, version = BaggableMobs.VERSION)
public class BaggableMobs
{
    public static final String VERSION = "1.0.0";
    public static final String NAME = "Baggable Mobs";
    public static final String DOMAIN = "baggablemobs";

    @Instance(DOMAIN)
    public static BaggableMobs INSTANCE;

    public static ItemMobBag itemMobBag;

    public static Logger LOGGER;

    public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(DOMAIN)
    {
        @Override
        public ItemStack createIcon()
        {
            return new ItemStack(itemMobBag);
        }
    };

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        LOGGER = event.getModLog();
    }

    @SubscribeEvent
    public static void onItemRegistryReady(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(itemMobBag = new ItemMobBag());
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        ConfigMain.generateCache();
        if (FMLCommonHandler.instance().getSide().isClient())
        {
            event.registerServerCommand(new CommandGenerateEntityList());
        }
    }
}
