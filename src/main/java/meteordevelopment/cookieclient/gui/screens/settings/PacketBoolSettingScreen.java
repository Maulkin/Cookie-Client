/*
 * This file is part of the Cookie Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.cookieclient.gui.screens.settings;

import meteordevelopment.cookieclient.gui.GuiTheme;
import meteordevelopment.cookieclient.gui.widgets.WWidget;
import meteordevelopment.cookieclient.settings.PacketListSetting;
import meteordevelopment.cookieclient.settings.Setting;
import meteordevelopment.cookieclient.utils.network.PacketUtils;
import net.minecraft.network.packet.Packet;

import java.util.Set;
import java.util.function.Predicate;

public class PacketBoolSettingScreen extends CollectionListSettingScreen<Class<? extends Packet<?>>> {
    public PacketBoolSettingScreen(GuiTheme theme, Setting<Set<Class<? extends Packet<?>>>> setting) {
        super(theme, "Select Packets", setting, setting.get(), PacketUtils.PACKETS);
    }

    @Override
    protected boolean includeValue(Class<? extends Packet<?>> value) {
        Predicate<Class<? extends Packet<?>>> filter = ((PacketListSetting) setting).filter;

        if (filter == null) return true;
        return filter.test(value);
    }

    @Override
    protected WWidget getValueWidget(Class<? extends Packet<?>> value) {
        return theme.label(getValueName(value));
    }

    @Override
    protected String getValueName(Class<? extends Packet<?>> value) {
        return PacketUtils.getName(value);
    }
}
