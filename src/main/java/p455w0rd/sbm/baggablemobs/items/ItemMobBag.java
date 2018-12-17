package p455w0rd.sbm.baggablemobs.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.sbm.baggablemobs.init.ModConfig.Options;
import p455w0rd.sbm.baggablemobs.init.ModCreativeTab;
import p455w0rd.sbm.baggablemobs.init.ModGlobals;
import p455w0rd.sbm.baggablemobs.util.BaggableMobsUtil;

/**
 * @author p455w0rd
 *
 */
@SuppressWarnings("deprecation")
public class ItemMobBag extends Item {

	private static final String NAME = "mob_bag";

	public ItemMobBag() {
		setRegistryName(ModGlobals.MODID + ":" + NAME);
		setUnlocalizedName(NAME);
		addPropertyOverride(new ResourceLocation("hasmob"), (stack, world, entity) -> BaggableMobsUtil.doesBagHaveMobStored(stack) ? 1 : 0);
		setMaxStackSize(Options.MOB_BAG_MAX_STACKSIZE);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (tab == ModCreativeTab.TAB || tab == CreativeTabs.SEARCH) {
			items.add(new ItemStack(this));
			items.addAll(ModCreativeTab.getWandList());
		}
	}

	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(new ResourceLocation(ModGlobals.MODID, NAME), "inventory"));
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (hand == EnumHand.MAIN_HAND && player != null) {
			ItemStack wand = player.getHeldItemMainhand();
			if (world.isRemote) {
				player.swingArm(hand);
			}
			if (!world.isRemote) {
				BaggableMobsUtil.spawnMobFromBag(wand, world, facing == EnumFacing.UP ? pos.up(1) : pos.offset(facing), player.capabilities.isCreativeMode);
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.PASS;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		ResourceLocation mob = BaggableMobsUtil.getCapturedMobInBag(stack);
		if (mob == null) {
			String mobType = Options.DISABLE_CAPTURING_HOSTILE_MOBS ? I18n.translateToLocal("tooltip.a_friendly") : I18n.translateToLocal("tooltip.any");
			tooltip.add(I18n.translateToLocal("tooltip.right_click") + " " + mobType + " " + I18n.translateToLocal("tooltip.mob_to_capture"));
		}
		else {
			if (BaggableMobsUtil.isVillager(BaggableMobsUtil.getCapturedMobInBag(stack))) {
				String profession = BaggableMobsUtil.getCapturedVillagerProfession(stack);
				if (!profession.isEmpty()) {
					tooltip.add(profession);
				}
			}
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		if (BaggableMobsUtil.doesBagHaveMobStored(stack)) {
			String mobName = BaggableMobsUtil.getMobName(stack);
			if (BaggableMobsUtil.isVillager(BaggableMobsUtil.getCapturedMobInBag(stack))) {
			}
			return super.getItemStackDisplayName(stack) + " (" + mobName + ")";
		}
		return super.getItemStackDisplayName(stack);
	}

}
