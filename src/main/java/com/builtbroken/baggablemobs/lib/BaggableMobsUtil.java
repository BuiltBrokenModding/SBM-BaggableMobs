package com.builtbroken.baggablemobs.lib;

import com.builtbroken.baggablemobs.BaggableMobs;
import com.builtbroken.baggablemobs.init.ModConfig.MobListMode;
import com.builtbroken.baggablemobs.init.ModConfig.Options;
import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

/**
 * @author p455w0rd
 */
@SuppressWarnings("deprecation")
public class BaggableMobsUtil
{

    public static Map<ResourceLocation, EntityEntry> CAPTURABLE_MOBS = Maps.<ResourceLocation, EntityEntry>newHashMap();
    public static final String CAPTURED_MOB_TAG = "CapturedMob";
    public static final String CAPTURED_MOB_DATA_TAG = "CapturedMobData";
    private static final BaggableMobsUtil INSTANCE = new BaggableMobsUtil();

    public static BaggableMobsUtil getInstance()
    {
        return INSTANCE;
    }

    // Extracts the Resourcelocation of the mob stored in the bag
    public static ResourceLocation getCapturedMobInBag(ItemStack mobBag)
    {
        if (mobBag.getItem() == BaggableMobs.itemMobBag && mobBag.hasTagCompound())
        {
            NBTTagCompound wandNBT = mobBag.getTagCompound();
            if (wandNBT.hasKey(CAPTURED_MOB_TAG) && !wandNBT.getString(CAPTURED_MOB_TAG).isEmpty())
            {
                ResourceLocation mob = new ResourceLocation(wandNBT.getString(CAPTURED_MOB_TAG));
                if (ForgeRegistries.ENTITIES.containsKey(mob))
                {
                    return mob;
                }
            }
        }
        return null;
    }

    // Gets the localized mob name
    public static String getMobName(ItemStack mobBag)
    {
        if (mobBag.getItem() == BaggableMobs.itemMobBag && mobBag.hasTagCompound() && mobBag.getTagCompound().hasKey(CAPTURED_MOB_TAG))
        {
            for (EntityEntry entry : getCapurableMobs().values())
            {
                ResourceLocation resLoc = new ResourceLocation(mobBag.getTagCompound().getString(CAPTURED_MOB_TAG));
                if (entry.getRegistryName().equals(resLoc))
                {
                    return I18n.translateToLocal("entity." + entry.getName() + ".name");
                }
            }
        }
        return "";
    }

    // Checks if the ResourceLocation is a villager
    public static boolean isVillager(ResourceLocation resLoc)
    {
        return resLoc.toString().equals("minecraft:villager");
    }

    // Gets a villager profession
    public static String getCapturedVillagerProfession(ItemStack mobBag)
    {
        if (mobBag.getItem() == BaggableMobs.itemMobBag && mobBag.hasTagCompound() && mobBag.getTagCompound().hasKey(CAPTURED_MOB_TAG))
        {
            for (EntityEntry entry : getCapurableMobs().values())
            {
                ResourceLocation resLoc = new ResourceLocation(mobBag.getTagCompound().getString(CAPTURED_MOB_TAG));
                if (entry.getRegistryName().equals(resLoc))
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
                }
            }
        }
        return "";
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

    // Gets/Generates the list of current valid capturable mobs
    public static Map<ResourceLocation, EntityEntry> getCapurableMobs()
    {
        if (CAPTURABLE_MOBS.isEmpty())
        {
            if (getConfigMobListMode() == MobListMode.BLACKLIST)
            {
                for (Entry<ResourceLocation, EntityEntry> entry : ForgeRegistries.ENTITIES.getEntries())
                {
                    if (Options.DISABLE_CAPTURING_HOSTILE_MOBS && EntityMob.class.isAssignableFrom(entry.getValue().getEntityClass()))
                    {
                        continue;
                    }
                    if (EntityLiving.class.isAssignableFrom(entry.getValue().getEntityClass()) && !getConfigMobList().contains(entry.getValue().getEntityClass()))
                    {
                        CAPTURABLE_MOBS.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            else
            {
                for (Class<? extends EntityLiving> clazz : getConfigMobList())
                {
                    if (EntityLiving.class.isAssignableFrom(clazz))
                    {
                        CAPTURABLE_MOBS.put(EntityList.getKey(clazz), ForgeRegistries.ENTITIES.getValue(EntityList.getKey(clazz)));
                    }
                }
            }
        }
        return CAPTURABLE_MOBS;
    }

    // Gets the mob list mode from the config file
    private static MobListMode getConfigMobListMode()
    {
        return Options.MOB_LIST_MODE;
    }

    // Gets the mob list array from the config file
    @SuppressWarnings("unchecked")
    private static List<Class<? extends EntityLiving>> getConfigMobList() throws InvalidEntityException
    {
        List<Class<? extends EntityLiving>> mobList = new ArrayList<>();
        if (Options.MOB_LIST.length == 0)
        {
            return mobList;
        }
        for (String registryName : Options.MOB_LIST)
        {
            Class<? extends Entity> clazz = EntityList.getClass(new ResourceLocation(registryName));
            if (clazz != null && EntityLiving.class.isAssignableFrom(clazz))
            {
                if (!mobList.contains(clazz))
                {
                    mobList.add((Class<? extends EntityLiving>) clazz);
                }
            }
            else
            {
                throw new InvalidEntityException(registryName);
            }
        }
        return mobList;
    }

    // Generates the text file containing a full list of living entities available in the current instance
    public static void generateEntityList(EntityPlayer player)
    {
        final String filename = "EntityList.txt";
        File entityListFile = new File(filename);
        if (entityListFile.exists())
        {
            entityListFile.delete();
        }
        List<String> fileContents = new ArrayList<>();
        fileContents.add("Registry Name - Class Name - Entity Name");
        fileContents.add("========================================");
        for (EntityEntry entity : ForgeRegistries.ENTITIES.getValuesCollection())
        {
            if (EntityLiving.class.isAssignableFrom(entity.getEntityClass()))
            {
                fileContents.add(entity.getRegistryName().toString() + " - " + entity.getEntityClass().getSimpleName().toString() + ".class - " + I18n.translateToLocal("entity." + entity.getName() + ".name"));
            }
        }
        try
        {
            FileUtils.writeLines(entityListFile, "UTF-8", fileContents, false);
            player.sendMessage(new TextComponentString("Generated entity list file at " + TextFormatting.ITALIC + "" + entityListFile.getAbsolutePath()));
        }
        catch (IOException e)
        {
        }
    }

    // Spawns the mob stored in a bag
    public static boolean spawnMobFromBag(ItemStack mobBag, World world, BlockPos pos, boolean isPlayerCreative)
    {
        if (!mobBag.isEmpty() && mobBag.getItem() == BaggableMobs.itemMobBag && doesBagHaveMobStored(mobBag))
        {
            for (EntityEntry entry : getCapurableMobs().values())
            {
                if (entry.getRegistryName().equals(getCapturedMobInBag(mobBag)))
                {
                    Entity entity = entry.newInstance(world);

                    if (mobBag.getTagCompound().hasKey(CAPTURED_MOB_DATA_TAG))
                    {
                        entity.readFromNBT(mobBag.getTagCompound().getCompoundTag(CAPTURED_MOB_DATA_TAG));
                        if (getCapturedMobInBag(mobBag).toString().equals("minecraft:villager"))
                        {

                        }
                    }
                    entity.setUniqueId(UUID.randomUUID());
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

    // Stores the mob in the bag and optionally kills it (in Creative mode, mobs aren't killed)
    public static boolean storeMobInBag(EntityPlayer player, EntityLivingBase mob, boolean killMob)
    {
        ItemStack mobBag = player.getHeldItemMainhand();
        World world = player.getEntityWorld();
        if (world != null && mobBag.getItem() == BaggableMobs.itemMobBag && (!doesBagHaveMobStored(mobBag) || !killMob))
        {
            ItemStack leftOverBags = ItemStack.EMPTY;
            if (mobBag.getCount() > 1)
            {
                leftOverBags = new ItemStack(BaggableMobs.itemMobBag);
                leftOverBags.setCount(mobBag.getCount() - 1);
                mobBag.setCount(1);
            }
            for (EntityEntry entry : getCapurableMobs().values())
            {
                if (entry.getEntityClass().equals(mob.getClass()))
                {
                    String mobLoc = entry.getRegistryName().toString();
                    if (!mobBag.hasTagCompound())
                    {
                        mobBag.setTagCompound(new NBTTagCompound());
                    }
                    mobBag.getTagCompound().setString(CAPTURED_MOB_TAG, mobLoc);
                    mobBag.getTagCompound().setTag(CAPTURED_MOB_DATA_TAG, mob.writeToNBT(new NBTTagCompound()));
                    if (killMob)
                    {
                        mob.setDead();
                    }
                    if (!leftOverBags.isEmpty())
                    {
                        if (!player.addItemStackToInventory(leftOverBags))
                        {
                            BlockPos pos = player.getPosition();
                            EntityItem leftOverEntity = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), leftOverBags);
                            leftOverEntity.setDefaultPickupDelay();
                            if (!world.isRemote)
                            {
                                world.spawnEntity(leftOverEntity);
                            }
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    // Check whether or not mob is stored in the bag
    public static boolean doesBagHaveMobStored(ItemStack mobBag)
    {
        return getCapturedMobInBag(mobBag) != null;
    }

    // Custom exception for invalid entity strings in config file
    private static class InvalidEntityException extends RuntimeException
    {

        private static final long serialVersionUID = -1530249344404652991L;

        public InvalidEntityException(String registryNameString)
        {
            super("\"" + registryNameString + "\" is not a valid entity. Entity registry name must be used. Example: minecraft:villager");
        }

    }

}
