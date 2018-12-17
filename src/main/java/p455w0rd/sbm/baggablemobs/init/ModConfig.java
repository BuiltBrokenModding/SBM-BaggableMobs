package p455w0rd.sbm.baggablemobs.init;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Configuration;
import p455w0rd.sbm.baggablemobs.network.PacketConfigSync;

/**
 * @author p455w0rd
 *
 */
public class ModConfig {

	public static Configuration CONFIG;

	public static final String CAT = "General";

	public static void init() {
		if (CONFIG == null) {
			CONFIG = new Configuration(new File(ModGlobals.CONFIG_FILE));
			CONFIG.load();
		}
		Options.DISABLE_CAPTURING_HOSTILE_MOBS = CONFIG.getBoolean("DisableHostileMobCapture", CAT, true, "If true, the Capture Wand will only capture neutral and friendly mobs");
		Options.MOB_LIST_MODE = CONFIG.getBoolean("MobListMode", CAT, true, "The mob list mode\ntrue=list will be used as a blacklist [no mob in the list will be baggable]\nfalse=list will be used as a whitelist [only mobs in the list will be baggable]") ? MobListMode.BLACKLIST : MobListMode.WHITELIST;
		Options.MOB_LIST = CONFIG.getStringList("MobList", CAT, new String[] {
				"minecraft:ender_dragon", "minecraft:wither"
		}, "A list of mobs for either whitelisting or blacklisting");
		Options.MOB_BAG_MAX_STACKSIZE = CONFIG.getInt("MobBagMaxStackSize", CAT, 64, 1, 64, "Max stack size for empty Mob Bag");
		if (CONFIG.hasChanged()) {
			CONFIG.save();
		}
	}

	public static void sendToPlayer(EntityPlayerMP player) {
		Map<String, Object> configsToSend = new HashMap<>();
		configsToSend.put("DisableHostileMobCapture", Options.DISABLE_CAPTURING_HOSTILE_MOBS);
		configsToSend.put("MobList", Options.MOB_LIST);
		configsToSend.put("MobListMode", Options.MOB_LIST_MODE);
		configsToSend.put("MobBagMaxStackSize", Options.MOB_BAG_MAX_STACKSIZE);
		ModNetworking.getInstance().sendTo(new PacketConfigSync(configsToSend), player);
	}

	public static class Options {

		public static boolean DISABLE_CAPTURING_HOSTILE_MOBS = true;
		public static String[] MOB_LIST = new String[0];
		public static MobListMode MOB_LIST_MODE = MobListMode.BLACKLIST;
		public static int MOB_BAG_MAX_STACKSIZE = 64;

	}

	public static enum MobListMode {
			WHITELIST, BLACKLIST;
	}

}
