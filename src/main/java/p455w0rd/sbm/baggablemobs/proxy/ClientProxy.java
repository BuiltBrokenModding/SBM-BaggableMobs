package p455w0rd.sbm.baggablemobs.proxy;

import java.util.function.Predicate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.fml.common.event.*;
import p455w0rd.sbm.baggablemobs.init.*;
import p455w0rd.sbm.baggablemobs.init.ModConfig.Options;
import p455w0rd.sbm.baggablemobs.util.BaggableMobsUtil;

public class ClientProxy extends CommonProxy implements ISelectiveResourceReloadListener {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		ModCreativeTab.init();
	}

	@Override
	public void init(FMLInitializationEvent event) {
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
			return tintIndex != 2 ? BaggableMobsUtil.getCapturedMobInBag(stack) == null ? -1 : BaggableMobsUtil.getMobEggColor(BaggableMobsUtil.getCapturedMobInBag(stack), tintIndex) : -1;
		}, ModItems.MOB_BAG);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(this);
	}

	@Override
	public void serverStarting(FMLServerStartingEvent event) {
		ModCommands.Client.register(event);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void refreshResources() {
		Minecraft.getMinecraft().refreshResources();
	}

	// A reload listener for purposes of refreshing the generated bag list in both Creative Tab and JEI from the Mog Config GUI
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
		BaggableMobsUtil.CAPTURABLE_MOBS.clear();
		BaggableMobsUtil.getCapurableMobs();
		ModCreativeTab.BAG_LIST.clear();
		ModCreativeTab.getWandList();
		ModItems.MOB_BAG.setMaxStackSize(Options.MOB_BAG_MAX_STACKSIZE);
	}

}
