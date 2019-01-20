package com.builtbroken.baggablemobs.client;

import com.builtbroken.baggablemobs.BaggableMobs;
import com.builtbroken.baggablemobs.init.ModConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author p455w0rd
 *
 */
public class GuiModConfig extends GuiConfig {

	public GuiModConfig(GuiScreen parent) {
		super(getParent(parent), getConfigElements(), BaggableMobs.MODID, false, false, getTitle(parent));
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
		return I18n.format(BaggableMobs.NAME + " Config");
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
	}
}