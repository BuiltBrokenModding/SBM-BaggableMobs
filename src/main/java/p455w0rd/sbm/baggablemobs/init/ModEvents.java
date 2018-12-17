package p455w0rd.sbm.baggablemobs.init;

import net.minecraft.entity.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import p455w0rd.sbm.baggablemobs.BaggableMobs;
import p455w0rd.sbm.baggablemobs.init.ModConfig.Options;
import p455w0rd.sbm.baggablemobs.util.BaggableMobsUtil;

/**
 * @author p455w0rd
 *
 */
@EventBusSubscriber(modid = ModGlobals.MODID)
public class ModEvents {

	@SubscribeEvent
	public static void onItemRegistryReady(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(ModItems.MOB_BAG);
	}

	@SubscribeEvent
	public static void onModelRegistryReady(ModelRegistryEvent event) {
		ModItems.registerModel();
	}

	@SubscribeEvent
	public static void onEntityInteract(EntityInteract event) {
		if (event.getEntityPlayer() != null && event.getEntityPlayer().getEntityWorld() != null) {
			EntityPlayer player = event.getEntityPlayer();
			Entity target = event.getTarget();
			ItemStack heldItem = player.getHeldItemMainhand();
			if (!heldItem.isEmpty() && heldItem.getItem() == ModItems.MOB_BAG && target instanceof EntityCreature) {
				if (Options.DISABLE_CAPTURING_HOSTILE_MOBS && target instanceof EntityMob || !target.isNonBoss()) {
					return;
				}
				BaggableMobsUtil.storeMobInBag(player, (EntityLivingBase) target, !player.capabilities.isCreativeMode);
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent e) {
		if (e.getModID().equals(ModGlobals.MODID)) {
			ModConfig.init();
			if (FMLCommonHandler.instance().getSide().isClient()) {
				BaggableMobs.PROXY.refreshResources();
			}
		}
	}

}
