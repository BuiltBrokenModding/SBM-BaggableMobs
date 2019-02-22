package com.builtbroken.baggablemobs.content;

import static com.builtbroken.baggablemobs.lib.BaggableMobsUtil.CAPTURED_MOB_DATA_TAG;
import static com.builtbroken.baggablemobs.lib.BaggableMobsUtil.CAPTURED_MOB_TAG;
import static com.builtbroken.baggablemobs.lib.BaggableMobsUtil.doesBagHaveMobStored;

import java.util.Collection;
import java.util.List;

import com.builtbroken.baggablemobs.BaggableMobs;
import com.builtbroken.baggablemobs.init.BaggableMobsConfig;
import com.builtbroken.baggablemobs.lib.BaggableMobsUtil;
import com.google.common.collect.Lists;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.GameData;

/**
 * @author p455w0rd
 */
public class ModItemGroup extends ItemGroup
{

    public static ItemGroup GROUP;
    public static List<ItemStack> BAG_LIST = Lists.<ItemStack>newArrayList();

    public ModItemGroup()
    {
        super(BaggableMobs.MODID);
    }

    public static void init()
    {
        GROUP = new ModItemGroup();
    }

    @Override
    public ItemStack createIcon()
    {
        return new ItemStack(BaggableMobs.itemMobBag);
    }

    @Override
    public void fill(NonNullList<ItemStack> items)
    {
        items.add(new ItemStack(BaggableMobs.itemMobBag));
        items.addAll(getBagList());
    }

    public static List<ItemStack> getBagList()
    {
        if (BAG_LIST.isEmpty())
        {
            Collection<EntityType<?>> mobList = BaggableMobsUtil.getCapturableMobs().values();
            for (EntityType<?> type : mobList)
            {//ForgeRegistries.ENTITIES.getValuesCollection()) {
                Class<? extends Entity> tempEntity = type.getEntityClass();
                if (EntityCreature.class.isAssignableFrom(tempEntity) || EntityDragon.class.isAssignableFrom(tempEntity))
                {
                    if (BaggableMobsConfig.CONFIG.DISABLE_CAPTURING_HOSTILE_MOBS.get() && EntityMob.class.isAssignableFrom(tempEntity))
                    {
                        continue;
                    }

                    if (BaggableMobsUtil.isVillager(type.getRegistryName()))
                    {
                        for (VillagerProfession profession : ForgeRegistries.VILLAGER_PROFESSIONS.getValues())
                        {
                            ItemStack mobBag = new ItemStack(BaggableMobs.itemMobBag);
                            if (!storeVillagerInBag(mobBag, profession))
                            {
                                break;
                            }
                            BAG_LIST.add(mobBag);
                        }
                        continue;
                    }
                    ItemStack mobBag = new ItemStack(BaggableMobs.itemMobBag);
                    storeMobInBag(mobBag, tempEntity);
                    BAG_LIST.add(mobBag);
                }
            }
        }
        return BAG_LIST;
    }

    private static void storeMobInBag(ItemStack mobBag, Class<? extends Entity> clazz)
    {
        if (mobBag.getItem() == BaggableMobs.itemMobBag && !doesBagHaveMobStored(mobBag))
        {
            for (EntityType<?> type : BaggableMobsUtil.getCapturableMobs().values())
            {
                if (type.getEntityClass().equals(clazz))
                {
                    String mobLoc = type.getRegistryName().toString();
                    if (!mobBag.hasTag())
                    {
                        mobBag.setTag(new NBTTagCompound());
                    }
                    mobBag.getTag().putString(CAPTURED_MOB_TAG, mobLoc);
                }
            }
        }
    }

    private static boolean storeVillagerInBag(ItemStack mobBag, VillagerProfession profession)
    {
        if (mobBag.getItem() == BaggableMobs.itemMobBag && !doesBagHaveMobStored(mobBag))
        {
            for (EntityType<?> type : BaggableMobsUtil.getCapturableMobs().values())
            {
                if (type.getEntityClass().equals(EntityVillager.class))
                {
                    String mobLoc = type.getRegistryName().toString();
                    if (!mobBag.hasTag())
                    {
                        mobBag.setTag(new NBTTagCompound());
                    }
                    mobBag.getTag().putString(CAPTURED_MOB_TAG, mobLoc);
                    NBTTagCompound professionData = new NBTTagCompound();
                    professionData.putInt("Profession", GameData.getWrapper(VillagerProfession.class).getId(profession));
                    professionData.putString("ProfessionName", profession.getRegistryName().toString());
                    professionData.putInt("Career", 0);
                    professionData.putInt("CareerLevel", 0);
                    professionData.putInt("Riches", 0);
                    professionData.putByte("Willing", (byte) 0);
                    mobBag.getTag().put(CAPTURED_MOB_DATA_TAG, professionData);
                    return true;
                }
            }
        }
        return false;
    }

}