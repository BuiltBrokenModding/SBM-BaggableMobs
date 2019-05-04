package com.builtbroken.baggablemobs.content;

import com.builtbroken.baggablemobs.BaggableMobs;
import com.builtbroken.baggablemobs.config.ConfigMain;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

import java.util.UUID;

/**
 * @author p455w0rd
 */
@SuppressWarnings("deprecation")
public class BaggableMobsUtil
{
    public static final String CAPTURED_MOB_TAG = "CapturedMob";
    public static final String CAPTURED_MOB_DATA_TAG = "CapturedMobData";
    public static final String VILLAGER_ID = "minecraft:villager";

    /**
     * Gets the mob's registry name from the bag's save
     *
     * @param mobBag
     * @return
     */
    public static ResourceLocation getMobID(ItemStack mobBag)
    {
        if (mobBag.getItem() == BaggableMobs.itemMobBag && mobBag.hasTagCompound())
        {
            final NBTTagCompound nbtTagCompound = mobBag.getTagCompound();
            if (nbtTagCompound.hasKey(CAPTURED_MOB_TAG))
            {
                final String mobTag = nbtTagCompound.getString(CAPTURED_MOB_TAG);
                if (!mobTag.isEmpty())
                {
                    return new ResourceLocation(mobTag);
                }
            }
        }
        return null;
    }


    /**
     * Gets the display name of the entity
     *
     * @param mobBag
     * @return
     */
    public static String getMobName(ItemStack mobBag)
    {
        final ResourceLocation regName = getMobID(mobBag);
        if (regName != null)
        {
            final EntityEntry entry = ForgeRegistries.ENTITIES.getValue(regName);
            if (entry != null)
            {
                return I18n.translateToLocal("entity." + entry.getName() + ".name");
            }
        }
        return "";
    }

    /**
     * Checks if the mob is a villager
     *
     * @param resLoc
     * @return
     */
    public static boolean isVillager(ResourceLocation resLoc)
    {
        return resLoc.toString().equals(VILLAGER_ID);
    }

    /**
     * Gets the string name of the villager's profession
     *
     * @param mobBag
     * @return
     */
    public static String getCapturedVillagerProfession(ItemStack mobBag)
    {
        if (!ForgeRegistries.VILLAGER_PROFESSIONS.getKeys().isEmpty())
        {
            final ResourceLocation resLoc = getMobID(mobBag);
            if (resLoc != null)
            {
                if (isVillager(resLoc))
                {
                    if (mobBag.getTagCompound().hasKey(CAPTURED_MOB_DATA_TAG))
                    {
                        NBTTagCompound villagerData = mobBag.getTagCompound().getCompoundTag(CAPTURED_MOB_DATA_TAG);
                        VillagerProfession profession = ForgeRegistries.VILLAGER_PROFESSIONS.getValue(new ResourceLocation(villagerData.getString("ProfessionName")));
                        VillagerCareer career = profession.getCareer(villagerData.getInteger("Career"));
                        return "- " + I18n.translateToLocal("tooltip.profession") + ": " + I18n.translateToLocal("entity.Villager." + career.getName());
                    }
                    return "- " + I18n.translateToLocal("tooltip.profession") + ": " + I18n.translateToLocal("entity.Villager.farmer");
                }
                return "err_vil";
            }
            return "err_id";
        }
        return "err_prof";
    }

    // Gets the mob egg color for purposes of coloring a filled bag
    public static int getMobEggColor(ResourceLocation resLoc, int index)
    {
        for (EntityEntry entry : ForgeRegistries.ENTITIES.getValuesCollection())
        {
            if (entry.getRegistryName().equals(resLoc) && entry.getEgg() != null)
            {
                return index == 0 ? entry.getEgg().primaryColor : entry.getEgg().secondaryColor;
            }
        }
        return -1;
    }

    // Spawns the mob stored in a bag
    public static boolean spawnMobFromBag(ItemStack mobBag, World world, BlockPos pos, boolean isPlayerCreative)
    {
        final ResourceLocation regName = getMobID(mobBag);
        if (regName != null)
        {
            final EntityEntry entry = ForgeRegistries.ENTITIES.getValue(regName);
            if (entry != null)
            {
                final Entity entity = entry.newInstance(world);
                if (entity != null)
                {
                    if (mobBag.getTagCompound().hasKey(CAPTURED_MOB_DATA_TAG))
                    {
                        entity.readFromNBT(mobBag.getTagCompound().getCompoundTag(CAPTURED_MOB_DATA_TAG));
                        if (getMobID(mobBag).toString().equals("minecraft:villager"))
                        {
                            //TODO why?
                        }
                    }
                    entity.setUniqueId(UUID.randomUUID()); //TODO might not want to generate random
                    entity.setLocationAndAngles(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, 0.0F, 0.0F);
                    world.spawnEntity(entity);
                    if (!isPlayerCreative)
                    {
                        mobBag.setTagCompound(null);
                    }
                    if (entity instanceof EntityLiving)
                    {
                        ((EntityLiving) entity).playLivingSound();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean canCaptureEntity(Entity entity)
    {
        if (entity instanceof EntityLiving)
        {
            if (!entity.isNonBoss() || entity.isDead)
            {
                return false;
            }
            else if (ConfigMain.IS_BAN_LIST)
            {
                return !ConfigMain.getSupportedEntities().contains(getRegName(entity));
            }
            else
            {
                return ConfigMain.getSupportedEntities().contains(getRegName(entity));
            }
        }
        return false;
    }

    public static ResourceLocation getRegName(Entity entity)
    {
        return entity != null ? EntityList.getKey(entity.getClass()) : null;
    }

    /**
     * Tries to store the current target mob inside of the bag
     *
     * @param player - player interacting with mob
     * @param hand   - hand used
     * @param entity - entity to capture
     * @return true if mob is capture into the bag
     */
    public static boolean storeMobInBag(EntityPlayer player, ItemStack heldItem, EnumHand hand, EntityLivingBase entity)
    {
        //Make sure we are not double storing mobs
        if (getMobID(heldItem) == null && canCaptureEntity(entity))
        {
            //Clone bag
            final ItemStack bagItemStack = heldItem.copy();
            bagItemStack.setCount(1);

            //Consume item
            if (!player.isCreative())
            {
                heldItem.shrink(1);
            }

            //Get entity
            final EntityEntry entry = ForgeRegistries.ENTITIES.getValue(getRegName(entity));
            if (entry != null)
            {
                //Save data
                final String mobRegName = entry.getRegistryName().toString();
                if (!bagItemStack.hasTagCompound())
                {
                    bagItemStack.setTagCompound(new NBTTagCompound());
                }
                bagItemStack.getTagCompound().setString(CAPTURED_MOB_TAG, mobRegName);
                bagItemStack.getTagCompound().setTag(CAPTURED_MOB_DATA_TAG, entity.writeToNBT(new NBTTagCompound()));

                //Kill mob if not in creative
                if (!player.isCreative())
                {
                    entity.setDead();
                }

                //Insert into hand if main hand is empty now
                if (heldItem.isEmpty())
                {
                    player.setHeldItem(hand, bagItemStack);
                }
                //If not try to insert into inventory
                else if (!player.addItemStackToInventory(bagItemStack))
                {
                    //If that fails drop on ground
                    player.entityDropItem(bagItemStack, 0f);
                }

                return true;
            }
        }
        return false;
    }
}
