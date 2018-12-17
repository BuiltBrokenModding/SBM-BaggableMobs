package p455w0rd.sbm.baggablemobs.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.*;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import p455w0rd.sbm.baggablemobs.init.ModConfig;
import p455w0rd.sbm.baggablemobs.init.ModGlobals;

/**
 * @author p455w0rd
 *
 */
public class GuiModConfig extends GuiConfig {

	public GuiModConfig(GuiScreen parent) {
		super(getParent(parent), getConfigElements(), ModGlobals.MODID, false, false, getTitle(parent));
	}

	private static GuiScreen getParent(GuiScreen parent) {
		return parent;
	}

	private static List<IConfigElement> getConfigElements() {
		List<IConfigElement> configElements = new ArrayList<IConfigElement>();
		Configuration config = ModConfig.CONFIG;
		if (config != null) {
			ConfigCategory categoryClient = config.getCategory(ModConfig.CAT);
			configElements.addAll(new ConfigElement(categoryClient).getChildElements());
		}
		return configElements;
	}

	private static String getTitle(GuiScreen parent) {
		return I18n.format(ModGlobals.NAME + " Config");
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
	}
}