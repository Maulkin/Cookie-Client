/*
 * This file is part of the Cookie Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.cookieclient.gui.screens.settings;

import meteordevelopment.cookieclient.gui.GuiTheme;
import meteordevelopment.cookieclient.gui.widgets.WWidget;
import meteordevelopment.cookieclient.settings.Setting;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandlerType;

import java.util.List;

public class ScreenHandlerSettingScreen extends CollectionListSettingScreen<ScreenHandlerType<?>> {
    public ScreenHandlerSettingScreen(GuiTheme theme, Setting<List<ScreenHandlerType<?>>> setting) {
        super(theme, "Select Screen Handlers", setting, setting.get(), Registries.SCREEN_HANDLER);
    }

    @Override
    protected WWidget getValueWidget(ScreenHandlerType<?> value) {
        return theme.label(getValueName(value));
    }

    @Override
    protected String getValueName(ScreenHandlerType<?> type) {
        return Registries.SCREEN_HANDLER.getId(type).toString();
    }
}
