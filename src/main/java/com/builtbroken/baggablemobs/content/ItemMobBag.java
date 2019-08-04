package com.builtbroken.baggablemobs.content;

import java.util.List;

import javax.annotation.Nullable;

import com.builtbroken.baggablemobs.BaggableMobs;
import com.builtbroken.baggablemobs.init.BaggableMobsConfig;
import com.builtbroken.baggablemobs.lib.BaggableMobsUtil;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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
    public ActionResultType onItemUse(ItemUseContext ctx)
    {
        PlayerEntity player = ctx.getPlayer();
        Hand hand = player.getActiveHand();
        World world = ctx.getWorld();
        Direction facing = ctx.getFace();
        BlockPos pos = ctx.getPos();

        if (hand == Hand.MAIN_HAND && player != null)
        {
            ItemStack bag = player.getHeldItemMainhand();
            if (world.isRemote)
            {
                player.swingArm(hand);
            }
            if (!world.isRemote)
            {
                BaggableMobsUtil.spawnMobFromBag(bag, world, facing == Direction.UP ? pos.up(1) : pos.offset(facing), player.isCreative());
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        ResourceLocation mob = BaggableMobsUtil.getCapturedMobInBag(stack);
        if (mob == null)
        {
            String mobType = BaggableMobsConfig.CONFIG.DISABLE_CAPTURING_HOSTILE_MOBS.get() ? I18n.format("tooltip.a_friendly") : I18n.format("tooltip.any");
            tooltip.add(new StringTextComponent(
                    new TranslationTextComponent("tooltip.right_click").getFormattedText()
                    + " " +
                    new TranslationTextComponent(mobType).getFormattedText()
                    + " " +
                    new TranslationTextComponent("tooltip.mob_to_capture").getFormattedText()));
        }
        else
        {
            if (BaggableMobsUtil.isVillager(BaggableMobsUtil.getCapturedMobInBag(stack)))
            {
                String profession = BaggableMobsUtil.getCapturedVillagerProfession(stack);
                if (!profession.isEmpty())
                {
                    tooltip.add(new StringTextComponent(profession));
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
            return new StringTextComponent(super.getDisplayName(stack).getFormattedText() + " (" + mobName + ")");
        }
        return super.getDisplayName(stack);
    }

}
