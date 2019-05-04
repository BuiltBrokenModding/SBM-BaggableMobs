package com.builtbroken.baggablemobs.content;

import com.builtbroken.baggablemobs.BaggableMobs;
import com.builtbroken.baggablemobs.config.ConfigMain;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.GameData;

import javax.annotation.Nullable;
import java.util.List;

import static com.builtbroken.baggablemobs.content.BaggableMobsUtil.CAPTURED_MOB_DATA_TAG;

/**
 * @author p455w0rd
 */
@Mod.EventBusSubscriber(modid = BaggableMobs.DOMAIN)
public class ItemMobBag extends Item
{
    public ItemMobBag()
    {
        setCreativeTab(BaggableMobs.CREATIVE_TAB);
        setRegistryName(BaggableMobs.DOMAIN + ":mob_bag");
        setTranslationKey(getRegistryName().toString());
        addPropertyOverride(new ResourceLocation("hasmob"), (stack, world, entity) -> BaggableMobsUtil.getMobID(stack) != null ? 1 : 0);
        setMaxStackSize(ConfigMain.STACK_SIZE);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand)
    {
        return false;
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event)
    {
        if (event.getEntityPlayer() != null && event.getEntityPlayer().getEntityWorld() != null)
        {
            final EntityPlayer player = event.getEntityPlayer();
            final Entity target = event.getTarget();
            final ItemStack heldItem = player.getHeldItem(event.getHand());

            //Check to make sure its an item and we are acting on a living creature
            if (heldItem.getItem() == BaggableMobs.itemMobBag && BaggableMobsUtil.canCaptureEntity(target))
            {
                //Do action
                if(!player.world.isRemote)
                {
                    BaggableMobsUtil.storeMobInBag(player, heldItem, event.getHand(), (EntityLivingBase) target);
                }

                event.setCanceled(true);
                event.setCancellationResult(EnumActionResult.SUCCESS);
            }
        }
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (tab == getCreativeTab() || tab == CreativeTabs.SEARCH)
        {
            //Empty item
            items.add(new ItemStack(this));

            //Per entity
            for (EntityEntry entry : ForgeRegistries.ENTITIES)
            {
                Class<? extends Entity> tempEntity = entry.getEntityClass();
                if (EntityLiving.class.isAssignableFrom(tempEntity))
                {
                    final ItemStack mobBag = new ItemStack(BaggableMobs.itemMobBag);
                    mobBag.setTagCompound(new NBTTagCompound());
                    mobBag.getTagCompound().setString(BaggableMobsUtil.CAPTURED_MOB_TAG, entry.getRegistryName().toString());

                    //Generating villager options
                    if (BaggableMobsUtil.isVillager(entry.getRegistryName()))
                    {
                        for (VillagerRegistry.VillagerProfession profession : ForgeRegistries.VILLAGER_PROFESSIONS.getValuesCollection())
                        {
                            NBTTagCompound professionData = new NBTTagCompound();
                            professionData.setInteger("Profession", GameData.getWrapper(VillagerRegistry.VillagerProfession.class).getIDForObject(profession));
                            professionData.setString("ProfessionName", profession.getRegistryName().toString());
                            professionData.setInteger("Career", 0);
                            professionData.setInteger("CareerLevel", 0);
                            professionData.setInteger("Riches", 0);
                            professionData.setByte("Willing", (byte) 0);

                            final ItemStack villagerBag = mobBag.copy();
                            villagerBag.getTagCompound().setTag(CAPTURED_MOB_DATA_TAG, professionData);
                            items.add(villagerBag);
                        }
                    }
                    else
                    {
                        items.add(mobBag);
                    }
                }
            }
        }
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (hand == EnumHand.MAIN_HAND && player != null)
        {
            ItemStack wand = player.getHeldItemMainhand();
            if (world.isRemote)
            {
                player.swingArm(hand);
            }
            if (!world.isRemote)
            {
                BaggableMobsUtil.spawnMobFromBag(wand, world, facing == EnumFacing.UP ? pos.up(1) : pos.offset(facing), player.capabilities.isCreativeMode);
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.PASS;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        try
        {
            ResourceLocation mob = BaggableMobsUtil.getMobID(stack);
            if (mob == null)
            {
                String mobType = ConfigMain.DISABLE_CAPTURING_HOSTILE_MOBS ? I18n.translateToLocal("tooltip.a_friendly") : I18n.translateToLocal("tooltip.any");
                tooltip.add(I18n.translateToLocal("tooltip.right_click") + " " + mobType + " " + I18n.translateToLocal("tooltip.mob_to_capture"));
            }
            else
            {
                if (BaggableMobsUtil.isVillager(BaggableMobsUtil.getMobID(stack)))
                {
                    String profession = BaggableMobsUtil.getCapturedVillagerProfession(stack);
                    if (!profession.isEmpty())
                    {
                        tooltip.add(profession);
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            tooltip.add("-Error getting tooltip:");
            tooltip.add("--" + e.getMessage());
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        if (BaggableMobsUtil.getMobID(stack) != null)
        {
            String mobName = BaggableMobsUtil.getMobName(stack);
            if (BaggableMobsUtil.isVillager(BaggableMobsUtil.getMobID(stack)))
            {
            }
            return super.getItemStackDisplayName(stack) + " (" + mobName + ")";
        }
        return super.getItemStackDisplayName(stack);
    }

}
