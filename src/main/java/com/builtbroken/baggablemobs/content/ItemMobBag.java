package com.builtbroken.baggablemobs.content;

import java.util.List;

import javax.annotation.Nullable;

import com.builtbroken.baggablemobs.BaggableMobs;
import com.builtbroken.baggablemobs.init.BaggableMobsConfig;
import com.builtbroken.baggablemobs.lib.BaggableMobsUtil;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

/**
 * @author p455w0rd
 */
public class ItemMobBag extends Item
{
    public ItemMobBag()
    {
        super(new Item.Properties());
        setRegistryName(BaggableMobs.MODID + ":mob_bag");
        addPropertyOverride(new ResourceLocation("hasmob"), (stack, world, entity) -> BaggableMobsUtil.doesBagHaveMobStored(stack) ? 1 : 0);
    }

    @Override
    public int getItemStackLimit(ItemStack stack)
    {
        return BaggableMobsConfig.CONFIG.MOB_BAG_MAX_STACKSIZE.get();
    }

    @Override
    public EnumActionResult onItemUse(ItemUseContext ctx)
    {
        EntityPlayer player = ctx.getPlayer();
        EnumHand hand = player.getActiveHand();
        World world = ctx.getWorld();
        EnumFacing facing = ctx.getFace();
        BlockPos pos = ctx.getPos();

        if (hand == EnumHand.MAIN_HAND && player != null)
        {
            ItemStack bag = player.getHeldItemMainhand();
            if (world.isRemote)
            {
                player.swingArm(hand);
            }
            if (!world.isRemote)
            {
                BaggableMobsUtil.spawnMobFromBag(bag, world, facing == EnumFacing.UP ? pos.up(1) : pos.offset(facing), player.isCreative());
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.PASS;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        ResourceLocation mob = BaggableMobsUtil.getCapturedMobInBag(stack);
        if (mob == null)
        {
            String mobType = BaggableMobsConfig.CONFIG.DISABLE_CAPTURING_HOSTILE_MOBS.get() ? I18n.format("tooltip.a_friendly") : I18n.format("tooltip.any");
            tooltip.add(new TextComponentString(
                    new TextComponentTranslation("tooltip.right_click").getFormattedText()
                    + " " +
                    new TextComponentTranslation(mobType).getFormattedText()
                    + " " +
                    new TextComponentTranslation("tooltip.mob_to_capture").getFormattedText()));
        }
        else
        {
            if (BaggableMobsUtil.isVillager(BaggableMobsUtil.getCapturedMobInBag(stack)))
            {
                String profession = BaggableMobsUtil.getCapturedVillagerProfession(stack);
                if (!profession.isEmpty())
                {
                    tooltip.add(new TextComponentString(profession));
                }
            }
        }
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack)
    {
        if (BaggableMobsUtil.doesBagHaveMobStored(stack))
        {
            String mobName = BaggableMobsUtil.getMobName(stack);
            if (BaggableMobsUtil.isVillager(BaggableMobsUtil.getCapturedMobInBag(stack)))
            {
            }
            return new TextComponentString(super.getDisplayName(stack).getFormattedText() + " (" + mobName + ")");
        }
        return super.getDisplayName(stack);
    }

}
