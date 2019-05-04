package com.builtbroken.baggablemobs.config;

import com.builtbroken.baggablemobs.BaggableMobs;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;

@Config(modid = BaggableMobs.DOMAIN, name = "sbm/BaggableMobs")
@Config.LangKey("config." + BaggableMobs.DOMAIN + ":config.title")
@Mod.EventBusSubscriber(modid = BaggableMobs.DOMAIN)
public class ConfigMain
{
    @Config.Name("disllow_hostile_mobs")
    @Config.LangKey("config." + BaggableMobs.DOMAIN + ":config.disable.hostile.mobs.title")
    public static boolean DISABLE_CAPTURING_HOSTILE_MOBS = true;

    @Config.Name("entity_list")
    @Config.LangKey("config." + BaggableMobs.DOMAIN + ":config.entity.list.title")
    public static String[] ENTITY_LIST = new String[0];

    @Config.Name("is_ban_list")
    @Config.LangKey("config." + BaggableMobs.DOMAIN + ":config.entity.list.ban.title")
    public static boolean IS_BAN_LIST = true;

    @Config.Name("stacksize")
    @Config.RangeInt(max = 64, min = 1)
    @Config.LangKey("config." + BaggableMobs.DOMAIN + ":config.stacksize.title")
    public static int STACK_SIZE = 64;

    ///=================================================================

    private static final Set<ResourceLocation> _entityRegNameCache = new HashSet();


    public static void generateCache()
    {
        //Clear cache
        _entityRegNameCache.clear();

        //Convert entity string array into list of registry names
        for (String string : ENTITY_LIST)
        {
            string = string.trim();
            if (!string.isEmpty())
            {
                final ResourceLocation location = new ResourceLocation(string);
                final EntityEntry entityEntry = ForgeRegistries.ENTITIES.getValue(location);
                if (entityEntry != null)
                {
					_entityRegNameCache.add(location);
                }
                else if (IS_BAN_LIST)
                {
                    throw new RuntimeException("BaggableMobs: Failed to locate entity by name " + location);
                }
                else
                {
                    BaggableMobs.LOGGER.error("Failed to locate entity by name " + location);
                }
            }
        }
    }

    public static Set<ResourceLocation> getSupportedEntities()
    {
        return _entityRegNameCache;
    }

    @SubscribeEvent
    public void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent e)
    {
        if (e.getModID().equals(BaggableMobs.DOMAIN))
        {
            ConfigManager.sync(BaggableMobs.DOMAIN, Config.Type.INSTANCE);
            generateCache();
        }
    }
}
