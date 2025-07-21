/*
 * This file is part of the Cookie Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.cookieclient;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import meteordevelopment.cookieclient.gui.GuiThemes;
import meteordevelopment.cookieclient.gui.screens.ModulesScreen;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> new ModulesScreen(GuiThemes.get());
    }
}
