package com.builtbroken.baggablemobs.lib;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.builtbroken.baggablemobs.BaggableMobs;
import com.builtbroken.baggablemobs.init.BaggableMobsConfig;
import com.builtbroken.baggablemobs.init.BaggableMobsConfig.MobListMode;
import com.google.common.collect.Maps;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author p455w0rd
 */
public class BaggableMobsUtil
{

    public static Map<ResourceLocation, EntityType<?>> CAPTURABLE_MOBS = Maps.<ResourceLocation, EntityType<?>>newHashMap();
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
        if (mobBag.getItem() == BaggableMobs.itemMobBag && mobBag.hasTag())
        {
            CompoundNBT bagNBT = mobBag.getTag();
            if (bagNBT.contains(CAPTURED_MOB_TAG) && !bagNBT.getString(CAPTURED_MOB_TAG).isEmpty())
            {
                ResourceLocation mob = new ResourceLocation(bagNBT.getString(CAPTURED_MOB_TAG));
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
        if (mobBag.getItem() == BaggableMobs.itemMobBag && mobBag.hasTag() && mobBag.getTag().contains(CAPTURED_MOB_TAG))
        {
            for (EntityType<?> type : getCapturableMobs().values())
            {
                ResourceLocation resLoc = new ResourceLocation(mobBag.getTag().getString(CAPTURED_MOB_TAG));
                if (type.getRegistryName().equals(resLoc))
                {
                    return I18n.format(type.getTranslationKey());
                }
            }
        }
        return "";
    }

    // Checks if the ResourceLocation is a villager
    public static boolean isVillager(ResourceLocation resLoc)
    {
        return resLoc.toString().equals(EntityType.VILLAGER.getRegistryName().toString());
    }

    // Gets a villager profession
    public static String getCapturedVillagerProfession(ItemStack mobBag)
    {
        if (mobBag.getItem() == BaggableMobs.itemMobBag && mobBag.hasTag() && mobBag.getTag().contains(CAPTURED_MOB_TAG))
        {
            for (EntityType<?> type : getCapturableMobs().values())
            {
                ResourceLocation resLoc = new ResourceLocation(mobBag.getTag().getString(CAPTURED_MOB_TAG));
                if (type.getRegistryName().equals(resLoc))
                {
                    if (isVillager(resLoc))
                    {
                        if (mobBag.getTag().contains(CAPTURED_MOB_DATA_TAG))
                        {
                            CompoundNBT villagerData = mobBag.getTag().getCompound(CAPTURED_MOB_DATA_TAG);
                            VillagerProfession profession = ForgeRegistries.PROFESSIONS.getValue(new ResourceLocation(villagerData.getString("ProfessionName")));
                            return "- " + I18n.format("tooltip.profession") + ": " + I18n.format("entity.minecraft.villager." + profession.toString());
                        }
                        return "- " + I18n.format("tooltip.profession") + ": " + I18n.format("entity.minecraft.villager");
                    }
                }
            }
        }
        return "";
    }

    // Gets the mob egg color for purposes of coloring a filled bag
    public static int getMobEggColor(ResourceLocation resLoc, int index)
    {
        for (EntityType<?> type : ForgeRegistries.ENTITIES.getValues())
        {
            if (type.getRegistryName().equals(resLoc))
            {
                SpawnEggItem egg = SpawnEggItem.getEgg(type);

                if(egg != null)
                    return egg.getColor(index);
            }
        }
        return -1;
    }

    // Gets/Generates the list of current valid capturable mobs
    public static Map<ResourceLocation, EntityType<?>> getCapturableMobs()
    {
        if (CAPTURABLE_MOBS.isEmpty())
        {
            if (getConfigMobListMode() == MobListMode.BLACKLIST)
            {
                for (Entry<ResourceLocation, EntityType<?>> entry : ForgeRegistries.ENTITIES.getEntries())
                {
                    EntityClassification classification = entry.getValue().getClassification();
                    if (BaggableMobsConfig.CONFIG.DISABLE_CAPTURING_HOSTILE_MOBS.get() && classification == EntityClassification.MONSTER)
                    {
                        continue;
                    }
                    if ((classification == EntityClassification.AMBIENT || classification == EntityClassification.CREATURE || classification == EntityClassification.WATER_CREATURE) && !getConfigMobList().contains(entry.getValue()))
                    {
                        CAPTURABLE_MOBS.put(entry.getKey(), entry.getValue());
                    }
                }

                CAPTURABLE_MOBS.put(EntityType.VILLAGER.getRegistryName(), EntityType.VILLAGER); //villagers are classified as MISC
                CAPTURABLE_MOBS.put(EntityType.IRON_GOLEM.getRegistryName(), EntityType.IRON_GOLEM); //iron golems are classified as MISC
            }
            else
            {
                for (EntityType<?> type : getConfigMobList())
                {
                    CAPTURABLE_MOBS.put(type.getRegistryName(), type);
                }
            }
        }
        return CAPTURABLE_MOBS;
    }

    // Gets the mob list mode from the config file
    private static MobListMode getConfigMobListMode()
    {
        return MobListMode.values()[BaggableMobsConfig.CONFIG.MOB_LIST_MODE.get()];
    }

    // Gets the mob list array from the config file
    private static List<EntityType<?>> getConfigMobList() throws InvalidEntityException
    {
        List<EntityType<?>> mobList = new ArrayList<>();
        if (BaggableMobsConfig.CONFIG.MOB_LIST.get().size() == 0)
        {
            return mobList;
        }
        for (String registryName : BaggableMobsConfig.CONFIG.MOB_LIST.get())
        {
            ResourceLocation loc = new ResourceLocation(registryName);

            if(ForgeRegistries.ENTITIES.containsKey(loc))
            {
                EntityType<?> type = ForgeRegistries.ENTITIES.getValue(loc);

                if (!mobList.contains(type))
                {
                    mobList.add(type);
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
    public static void generateEntityList(PlayerEntity player)
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
        for (EntityType<?> entity : ForgeRegistries.ENTITIES.getValues())
        {
            if (entity.getClassification() != EntityClassification.MISC)
            {
                fileContents.add(entity.getRegistryName().toString() + " - " + entity.getClass().getSimpleName().toString() + ".class - " + I18n.format(entity.getTranslationKey()));
            }
        }
        try
        {
            FileUtils.writeLines(entityListFile, "UTF-8", fileContents, false);
            player.sendMessage(new StringTextComponent("Generated entity list file at " + TextFormatting.ITALIC + "" + entityListFile.getAbsolutePath()));
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
            for (EntityType<?> entry : getCapturableMobs().values())
            {
                if (entry.getRegistryName().equals(getCapturedMobInBag(mobBag)))
                {
                    Entity entity = entry.create(world);

                    if (mobBag.getTag().contains(CAPTURED_MOB_DATA_TAG))
                    {
                        entity.read(mobBag.getTag().getCompound(CAPTURED_MOB_DATA_TAG));
                        if (getCapturedMobInBag(mobBag).toString().equals("minecraft:villager"))
                        {

                        }
                    }
                    entity.setUniqueId(UUID.randomUUID());
                    entity.setLocationAndAngles(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, 0.0F, 0.0F);
                    world.addEntity(entity);
                    if (!isPlayerCreative)
                    {
                        mobBag.setTag(null);
                    }
                    if (entity instanceof MobEntity)
                    {
                        ((MobEntity) entity).playAmbientSound();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    // Stores the mob in the bag and optionally kills it (in Creative mode, mobs aren't killed)
    public static boolean storeMobInBag(PlayerEntity player, LivingEntity mob, boolean killMob)
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
            for (EntityType<?> type : getCapturableMobs().values())
            {
                if (type.equals(mob.getType()))
                {
                    String mobLoc = type.getRegistryName().toString();
                    if (!mobBag.hasTag())
                    {
                        mobBag.setTag(new CompoundNBT());
                    }
                    mobBag.getTag().putString(CAPTURED_MOB_TAG, mobLoc);
                    mobBag.getTag().put(CAPTURED_MOB_DATA_TAG, mob.serializeNBT());
                    if (killMob)
                    {
                        mob.remove();
                    }
                    if (!leftOverBags.isEmpty())
                    {
                        if (!player.addItemStackToInventory(leftOverBags))
                        {
                            BlockPos pos = player.getPosition();
                            ItemEntity leftOverEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), leftOverBags);
                            leftOverEntity.setDefaultPickupDelay();
                            if (!world.isRemote)
                            {
                                world.addEntity(leftOverEntity);
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
