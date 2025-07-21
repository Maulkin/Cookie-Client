/*
 * This file is part of the Cookie Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.cookieclient.gui.screens.settings;

import meteordevelopment.cookieclient.gui.GuiTheme;
import meteordevelopment.cookieclient.gui.widgets.WWidget;
import meteordevelopment.cookieclient.settings.Setting;
import meteordevelopment.cookieclient.utils.misc.Names;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import java.util.Set;

public class EnchantmentListSettingScreen extends DynamicRegistryListSettingScreen<Enchantment> {
    public EnchantmentListSettingScreen(GuiTheme theme, Setting<Set<RegistryKey<Enchantment>>> setting) {
        super(theme, "Select Enchantments", setting, setting.get(), RegistryKeys.ENCHANTMENT);
    }

    @Override
    protected WWidget getValueWidget(RegistryKey<Enchantment> value) {
        return theme.label(getValueName(value));
    }

    @Override
    protected String getValueName(RegistryKey<Enchantment> value) {
        return Names.get(value);
    }
}
