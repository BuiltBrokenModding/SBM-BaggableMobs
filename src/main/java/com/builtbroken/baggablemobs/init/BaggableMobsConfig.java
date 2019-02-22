package com.builtbroken.baggablemobs.init;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

/**
 * @author p455w0rd
 *
 */
public class BaggableMobsConfig {

    public static final ForgeConfigSpec CONFIG_SPEC;
    public static final BaggableMobsConfig CONFIG;

    static
    {
        Pair<BaggableMobsConfig,ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(BaggableMobsConfig::new);

        CONFIG_SPEC = specPair.getRight();
        CONFIG = specPair.getLeft();
    }

    public BooleanValue DISABLE_CAPTURING_HOSTILE_MOBS;
    public ConfigValue<List<? extends String>> MOB_LIST;
    public IntValue MOB_LIST_MODE;
    public IntValue MOB_BAG_MAX_STACKSIZE;

    public BaggableMobsConfig(ForgeConfigSpec.Builder builder)
    {
        DISABLE_CAPTURING_HOSTILE_MOBS = builder
                .comment("If true, the Mob Bag will only capture neutral and friendly mobs")
                .define("DisableHostileMobCapture", true);
        MOB_LIST_MODE = builder
                .comment("The mob list mode",
                        "0=list will be used as a whitelist [only mobs in the list will be baggable]",
                        "1=list will be used as a blacklist [no mob in the list will be baggable]")
                .defineInRange("MobListMode", 1, 0 ,1);
        MOB_LIST = builder.
                comment("A list of mobs for either whitelisting or blacklisting")
                .define("MobList", Arrays.asList("minecraft:ender_dragon", "minecraft:wither"));
        MOB_BAG_MAX_STACKSIZE = builder
                .comment("Max stack size for empty Mob Bag")
                .defineInRange("MobBagMaxStackSize", 64, 1, 64);
    }

    public static enum MobListMode {
        WHITELIST, BLACKLIST;
    }

}
