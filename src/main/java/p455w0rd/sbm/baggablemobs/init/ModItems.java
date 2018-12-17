package p455w0rd.sbm.baggablemobs.init;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import p455w0rd.sbm.baggablemobs.items.ItemMobBag;

/**
 * @author p455w0rd
 *
 */
public class ModItems {

	public static final ItemMobBag MOB_BAG = new ItemMobBag();

	@SideOnly(Side.CLIENT)
	public static void registerModel() {
		MOB_BAG.initModel();
	}

}
