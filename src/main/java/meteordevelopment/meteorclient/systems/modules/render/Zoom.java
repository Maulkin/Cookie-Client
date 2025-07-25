/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.systems.modules.render;

import meteordevelopment.meteorclient.CookieClient;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.meteor.MouseScrollEvent;
import meteordevelopment.meteorclient.events.render.GetFovEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class Zoom extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> zoom = sgGeneral.add(new DoubleSetting.Builder()
        .name("zoom")
        .description("How much to zoom.")
        .defaultValue(6)
        .min(1)
        .build()
    );

    private final Setting<Double> scrollSensitivity = sgGeneral.add(new DoubleSetting.Builder()
        .name("scroll-sensitivity")
        .description("Allows you to change zoom value using scroll wheel. 0 to disable.")
        .defaultValue(1)
        .min(0)
        .build()
    );

    private final Setting<Boolean> smooth = sgGeneral.add(new BoolSetting.Builder()
        .name("smooth")
        .description("Smooth transition.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> cinematic = sgGeneral.add(new BoolSetting.Builder()
        .name("cinematic")
        .description("Enables cinematic camera.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> hideHud = sgGeneral.add(new BoolSetting.Builder()
        .name("hide-HUD")
        .description("Whether or not to hide the Minecraft HUD.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> renderHands = sgGeneral.add(new BoolSetting.Builder()
        .name("show-hands")
        .description("Whether or not to render your hands.")
        .defaultValue(false)
        .visible(() -> !hideHud.get())
        .build()
    );

    private boolean enabled;
    private boolean preCinematic;
    private double preMouseSensitivity;
    private double value;
    private double lastFov;
    private double time;

    private boolean hudManualToggled;

    public Zoom() {
        super(Categories.Render, "zoom", "Zooms your view.");
        autoSubscribe = false;
    }

    @Override
    public void onActivate() {
        if (!enabled) {
            preCinematic = mc.options.smoothCameraEnabled;
            preMouseSensitivity = mc.options.getMouseSensitivity().getValue();
            value = zoom.get();
            lastFov = mc.options.getFov().getValue();
            time = 0.001;

            CookieClient.EVENT_BUS.subscribe(this);
            enabled = true;
        }

        if (hideHud.get() && !mc.options.hudHidden) {
            hudManualToggled = false;
            mc.options.hudHidden = true;
        }
    }

    @Override
    public void onDeactivate() {
        if (hideHud.get() && !hudManualToggled) {
            mc.options.hudHidden = false;
        }
    }

    @EventHandler
    public void onKeyPressed(KeyEvent event) {
        if (event.key != GLFW.GLFW_KEY_F1) return;
        hudManualToggled = true;
    }

    public void onStop() {
        mc.options.smoothCameraEnabled = preCinematic;
        mc.options.getMouseSensitivity().setValue(preMouseSensitivity);

        mc.worldRenderer.scheduleTerrainUpdate();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        mc.options.smoothCameraEnabled = cinematic.get();

        if (!cinematic.get()) {
            mc.options.getMouseSensitivity().setValue(preMouseSensitivity / Math.max(getScaling() * 0.5, 1));
        }

        if (time == 0) {
            CookieClient.EVENT_BUS.unsubscribe(this);
            enabled = false;

            onStop();
        }
    }

    @EventHandler
    private void onMouseScroll(MouseScrollEvent event) {
        if (scrollSensitivity.get() > 0 && isActive()) {
            value += event.value * 0.25 * (scrollSensitivity.get() * value);
            if (value < 1) value = 1;

            event.cancel();
        }
    }

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (!smooth.get()) {
            time = isActive() ? 1 : 0;
            return;
        }

        if (isActive()) time += event.frameTime * 5;
        else time -= event.frameTime * 5;

        time = MathHelper.clamp(time, 0, 1);
    }

    @EventHandler
    private void onGetFov(GetFovEvent event) {
        event.fov /= (float) getScaling();

        if (lastFov != event.fov) mc.worldRenderer.scheduleTerrainUpdate();
        lastFov = event.fov;
    }

    public double getScaling() {
        double delta = time < 0.5 ? 4 * time * time * time : 1 - Math.pow(-2 * time + 2, 3) / 2; // Ease in out cubic
        return MathHelper.lerp(delta, 1, value);
    }

    public boolean renderHands() {
        return !isActive() || renderHands.get();
    }
}
