/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class Sneak extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Which method to sneak.")
        .defaultValue(Mode.Vanilla)
        .build()
    );

    public Sneak() {
        super (Categories.Movement, "sneak", "Sneaks for you");
    }

    public boolean doPacket() {
        return isActive() && !mc.player.getAbilities().flying && mode.get() == Mode.Packet;
    }

    public boolean doVanilla() {
        return isActive() && !mc.player.getAbilities().flying && mode.get() == Mode.Vanilla;
    }

    public enum Mode {
        Packet,
        Vanilla
    }
}
