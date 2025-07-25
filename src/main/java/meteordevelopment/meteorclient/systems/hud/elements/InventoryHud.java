/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.systems.hud.elements;

import meteordevelopment.meteorclient.CookieClient;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import static meteordevelopment.meteorclient.CookieClient.mc;

public class InventoryHud extends HudElement {
    public static final HudElementInfo<InventoryHud> INFO = new HudElementInfo<>(Hud.GROUP, "inventory", "Displays your inventory.", InventoryHud::new);

    private static final Identifier TEXTURE = CookieClient.identifier("textures/container.png");
    private static final Identifier TEXTURE_TRANSPARENT = CookieClient.identifier("textures/container-transparent.png");

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgScale = settings.createGroup("Scale");
    private final SettingGroup sgBackground = settings.createGroup("Background");

    private final Setting<Boolean> containers = sgGeneral.add(new BoolSetting.Builder()
        .name("containers")
        .description("Shows the contents of a container when holding them.")
        .defaultValue(false)
        .build()
    );

    // Scale

    public final Setting<Boolean> customScale = sgScale.add(new BoolSetting.Builder()
        .name("custom-scale")
        .description("Applies a custom scale to this hud element.")
        .defaultValue(false)
        .onChanged(aBoolean -> calculateSize())
        .build()
    );

    public final Setting<Double> scale = sgScale.add(new DoubleSetting.Builder()
        .name("scale")
        .description("Custom scale.")
        .visible(customScale::get)
        .defaultValue(2)
        .onChanged(aDouble -> calculateSize())
        .min(0.5)
        .sliderRange(0.5, 3)
        .build()
    );

    // Background

    private final Setting<Background> background = sgBackground.add(new EnumSetting.Builder<Background>()
        .name("background")
        .description("Background of inventory viewer.")
        .defaultValue(Background.Texture)
        .onChanged(bg -> calculateSize())
        .build()
    );

    public final Setting<SettingColor> backgroundColor = sgBackground.add(new ColorSetting.Builder()
        .name("background-color")
        .description("Color used for the background.")
        .visible(() -> background.get() == Background.Flat)
        .defaultValue(new SettingColor(25, 25, 25, 50))
        .build()
    );

    private final ItemStack[] containerItems = new ItemStack[9 * 3];
    private final Color WHITE = new Color(255, 255, 255);

    private InventoryHud() {
        super(INFO);

        calculateSize();
    }

    @Override
    public void render(HudRenderer renderer) {
        double x = this.x, y = this.y;

        ItemStack container = getContainer();
        boolean hasContainer = containers.get() && container != null;
        if (hasContainer) Utils.getItemsInContainerItem(container, containerItems);
        Color drawColor = hasContainer ? Utils.getShulkerColor(container) :
            background.get() == Background.Flat ? backgroundColor.get() : WHITE;

        if (background.get() != Background.None) {
            drawBackground(renderer, (int) x, (int) y, drawColor);
        }

        if (mc.player == null) return;

        renderer.post(() -> {
            for (int row = 0; row < 3; row++) {
                for (int i = 0; i < 9; i++) {
                    int index = row * 9 + i;
                    ItemStack stack = hasContainer ? containerItems[index] : mc.player.getInventory().getStack(index + 9);
                    if (stack == null) continue;

                    int itemX = background.get() == Background.Texture ? (int) (x + (8 + i * 18) * getScale()) : (int) (x + (1 + i * 18) * getScale());
                    int itemY = background.get() == Background.Texture ? (int) (y + (7 + row * 18) * getScale()) : (int) (y + (1 + row * 18) * getScale());

                    renderer.item(stack, itemX, itemY, (float) getScale(), true);
                }
            }
        });
    }

    private void calculateSize() {
        setSize(background.get().width * getScale(), background.get().height * getScale());
    }

    private void drawBackground(HudRenderer renderer, int x, int y, Color color) {
        int w = getWidth();
        int h = getHeight();

        switch (background.get()) {
            case Texture, Outline -> renderer.texture(background.get() == Background.Texture ? TEXTURE : TEXTURE_TRANSPARENT, x, y, w, h, color);
            case Flat -> renderer.quad(x, y, w, h, color);
        }
    }

    private ItemStack getContainer() {
        if (isInEditor() || mc.player == null) return null;

        ItemStack stack = mc.player.getOffHandStack();
        if (Utils.hasItems(stack) || stack.getItem() == Items.ENDER_CHEST) return stack;

        stack = mc.player.getMainHandStack();
        if (Utils.hasItems(stack) || stack.getItem() == Items.ENDER_CHEST) return stack;

        return null;
    }

    private double getScale() {
        return customScale.get() ? scale.get() : scale.getDefaultValue();
    }

    public enum Background {
        None(162, 54),
        Texture(176, 67),
        Outline(162, 54),
        Flat(162, 54);

        private final int width, height;

        Background(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }
}
