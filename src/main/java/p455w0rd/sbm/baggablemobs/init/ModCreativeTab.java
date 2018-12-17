package p455w0rd.sbm.baggablemobs.init;

import static p455w0rd.sbm.baggablemobs.util.BaggableMobsUtil.*;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import net.minecraftforge.registries.GameData;
import p455w0rd.sbm.baggablemobs.init.ModConfig.Options;
import p455w0rd.sbm.baggablemobs.util.BaggableMobsUtil;

/**
 * @author p455w0rd
 *
 */
public class ModCreativeTab extends CreativeTabs {

	public static CreativeTabs TAB;
	public static List<ItemStack> BAG_LIST = Lists.<ItemStack>newArrayList();

	public ModCreativeTab() {
		super(ModGlobals.MODID);
	}

	public static void init() {
		TAB = new ModCreativeTab();
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(ModItems.MOB_BAG);
	}

	@Override
	public void displayAllRelevantItems(NonNullList<ItemStack> items) {
		items.add(new ItemStack(ModItems.MOB_BAG));
		items.addAll(getWandList());
	}

	public static List<ItemStack> getWandList() {
		if (BAG_LIST.isEmpty()) {
			Collection<EntityEntry> mobList = BaggableMobsUtil.getCapurableMobs().values();
			for (EntityEntry entry : mobList) {//ForgeRegistries.ENTITIES.getValuesCollection()) {
				Class<? extends Entity> tempEntity = entry.getEntityClass();
				if (EntityCreature.class.isAssignableFrom(tempEntity) || EntityDragon.class.isAssignableFrom(tempEntity)) {
					if (Options.DISABLE_CAPTURING_HOSTILE_MOBS && EntityMob.class.isAssignableFrom(tempEntity)) {
						continue;
					}

					if (BaggableMobsUtil.isVillager(entry.getRegistryName())) {
						for (VillagerProfession profession : ForgeRegistries.VILLAGER_PROFESSIONS.getValuesCollection()) {
							ItemStack mobBag = new ItemStack(ModItems.MOB_BAG);
							if (!storeVillagerInBag(mobBag, profession)) {
								break;
							}
							BAG_LIST.add(mobBag);
						}
						continue;
					}
					ItemStack mobBag = new ItemStack(ModItems.MOB_BAG);
					storeMobInBag(mobBag, tempEntity);
					BAG_LIST.add(mobBag);
				}
			}
		}
		return BAG_LIST;
	}

	private static void storeMobInBag(ItemStack mobBag, Class<? extends Entity> clazz) {
		if (mobBag.getItem() == ModItems.MOB_BAG && !doesBagHaveMobStored(mobBag)) {
			for (EntityEntry entry : BaggableMobsUtil.getCapurableMobs().values()) {
				if (entry.getEntityClass().equals(clazz)) {
					String mobLoc = entry.getRegistryName().toString();
					if (!mobBag.hasTagCompound()) {
						mobBag.setTagCompound(new NBTTagCompound());
					}
					mobBag.getTagCompound().setString(CAPTURED_MOB_TAG, mobLoc);
				}
			}
		}
	}

	private static boolean storeVillagerInBag(ItemStack mobBag, VillagerProfession profession) {
		if (mobBag.getItem() == ModItems.MOB_BAG && !doesBagHaveMobStored(mobBag)) {
			for (EntityEntry entry : BaggableMobsUtil.getCapurableMobs().values()) {
				if (entry.getEntityClass().equals(EntityVillager.class)) {
					String mobLoc = entry.getRegistryName().toString();
					if (!mobBag.hasTagCompound()) {
						mobBag.setTagCompound(new NBTTagCompound());
					}
					mobBag.getTagCompound().setString(CAPTURED_MOB_TAG, mobLoc);
					NBTTagCompound professionData = new NBTTagCompound();
					professionData.setInteger("Profession", GameData.getWrapper(VillagerProfession.class).getIDForObject(profession));
					professionData.setString("ProfessionName", profession.getRegistryName().toString());
					professionData.setInteger("Career", 0);
					professionData.setInteger("CareerLevel", 0);
					professionData.setInteger("Riches", 0);
					professionData.setByte("Willing", (byte) 0);
					mobBag.getTagCompound().setTag(CAPTURED_MOB_DATA_TAG, professionData);
					return true;
				}
			}
		}
		return false;
	}

}
