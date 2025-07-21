/*
 * This file is part of the Cookie Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.cookieclient.settings.BoolSetting;
import meteordevelopment.cookieclient.settings.DoubleSetting;
import meteordevelopment.cookieclient.settings.Setting;
import meteordevelopment.cookieclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class TridentBoost extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> multiplier = sgGeneral.add(new DoubleSetting.Builder()
        .name("boost")
        .description("How much your velocity is multiplied by when using riptide.")
        .defaultValue(2)
        .min(0.1)
        .sliderMin(1)
        .build()
    );

    private final Setting<Boolean> allowOutOfWater = sgGeneral.add(new BoolSetting.Builder()
        .name("out-of-water")
        .description("Whether riptide should work out of water")
        .defaultValue(true)
        .build()
    );

    public TridentBoost() {
        super(Categories.Movement, "trident-boost", "Boosts you when using riptide with a trident.");
    }

    public double getMultiplier() {
        return isActive() ? multiplier.get() : 1;
    }

    public boolean allowOutOfWater() {
        return isActive() ? allowOutOfWater.get() : false;
    }
}
