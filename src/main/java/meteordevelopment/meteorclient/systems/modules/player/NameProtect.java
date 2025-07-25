/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.systems.modules.player;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class NameProtect extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> nameProtect = sgGeneral.add(new BoolSetting.Builder()
        .name("name-protect")
        .description("Hides your name client-side.")
        .defaultValue(true)
        .build()
    );

    private final Setting<String> name = sgGeneral.add(new StringSetting.Builder()
        .name("name")
        .description("Name to be replaced with.")
        .defaultValue("player")
        .visible(nameProtect::get)
        .build()
    );

    private final Setting<Boolean> skinProtect = sgGeneral.add(new BoolSetting.Builder()
        .name("skin-protect")
        .description("Make players become Steves.")
        .defaultValue(true)
        .build()
    );

    private String username = "If you see this, something is wrong.";

    public NameProtect() {
        super(Categories.Player, "name-protect", "Hide player names and skins.");
    }

    @Override
    public void onActivate() {
        username = mc.getSession().getUsername();
    }

    public String replaceName(String string) {
        if (string != null && isActive()) {
            return string.replace(username, name.get());
        }

        return string;
    }

    public String getName(String original) {
        if (!name.get().isEmpty() && isActive()) {
            return name.get();
        }

        return original;
    }

    public boolean skinProtect() {
        return isActive() && skinProtect.get();
    }
}
