/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.systems.hud.elements;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.TickRate;

public class LagNotifierHud extends HudElement {
    public static final HudElementInfo<LagNotifierHud> INFO = new HudElementInfo<>(Hud.GROUP, "lag-notifier", "Displays if the server is lagging in ticks.", LagNotifierHud::new);

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgScale = settings.createGroup("Scale");
    private final SettingGroup sgBackground = settings.createGroup("Background");

    // General

    private final Setting<Boolean> shadow = sgGeneral.add(new BoolSetting.Builder()
        .name("shadow")
        .description("Text shadow.")
        .defaultValue(true)
        .build()
    );

    private final Setting<SettingColor> textColor = sgGeneral.add(new ColorSetting.Builder()
        .name("text-color")
        .description("A.")
        .defaultValue(new SettingColor())
        .build()
    );

    private final Setting<SettingColor> color1 = sgGeneral.add(new ColorSetting.Builder()
        .name("color-1")
        .description("First color.")
        .defaultValue(new SettingColor(255, 255, 5))
        .build()
    );

    private final Setting<SettingColor> color2 = sgGeneral.add(new ColorSetting.Builder()
        .name("color-2")
        .description("Second color.")
        .defaultValue(new SettingColor(235, 158, 52))
        .build()
    );

    private final Setting<SettingColor> color3 = sgGeneral.add(new ColorSetting.Builder()
        .name("color-3")
        .description("Third color.")
        .defaultValue(new SettingColor(225, 45, 45))
        .build()
    );

    // Scale

    private final Setting<Boolean> customScale = sgScale.add(new BoolSetting.Builder()
        .name("custom-scale")
        .description("Applies a custom scale to this hud element.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Double> scale = sgScale.add(new DoubleSetting.Builder()
        .name("scale")
        .description("Custom scale.")
        .visible(customScale::get)
        .defaultValue(1)
        .min(0.5)
        .sliderRange(0.5, 3)
        .build()
    );

    // Background

    private final Setting<Boolean> background = sgBackground.add(new BoolSetting.Builder()
        .name("background")
        .description("Displays background.")
        .defaultValue(false)
        .build()
    );

    private final Setting<SettingColor> backgroundColor = sgBackground.add(new ColorSetting.Builder()
        .name("background-color")
        .description("Color used for the background.")
        .visible(background::get)
        .defaultValue(new SettingColor(25, 25, 25, 50))
        .build()
    );

    public LagNotifierHud() {
        super(INFO);
    }

    @Override
    public void render(HudRenderer renderer) {
        float timeSinceLastTick = TickRate.INSTANCE.getTimeSinceLastTick();
        if (timeSinceLastTick < 1.1f && !isInEditor()) return;

        Color color = isInEditor() || timeSinceLastTick > 10f ? color3.get()
            : timeSinceLastTick > 3f ? color2.get()
            : color1.get();

        String info = isInEditor() ? "10.2" : String.format("%.1f", timeSinceLastTick);

        render(renderer, info, color);
        if (background.get()) {
            renderer.quad(this.x, this.y, getWidth(), getHeight(), backgroundColor.get());
        }
    }

    private void render(HudRenderer renderer, String right, Color rightColor) {
        double x2 = renderer.text("Time since last tick ", x, y, textColor.get(), shadow.get(), getScale());
        x2 = renderer.text(right, x2, y, rightColor, shadow.get(), getScale());

        setSize(x2 - x, renderer.textHeight(shadow.get(), getScale()));
    }

    private double getScale() {
        return customScale.get() ? scale.get() : Hud.get().getTextScale();
    }
}
