/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.renderer.text;

import meteordevelopment.meteorclient.renderer.MeshBuilder;
import meteordevelopment.meteorclient.renderer.MeshRenderer;
import meteordevelopment.meteorclient.renderer.CookieRenderPipelines;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

public class CustomTextRenderer implements TextRenderer {
    public static final Color SHADOW_COLOR = new Color(60, 60, 60, 180);

    private final MeshBuilder mesh = new MeshBuilder(CookieRenderPipelines.UI_TEXT);

    public final FontFace fontFace;

    private final Font[] fonts;
    private Font font;

    private boolean building;
    private boolean scaleOnly;
    private double fontScale = 1;
    private double scale = 1;

    public CustomTextRenderer(FontFace fontFace) {
        this.fontFace = fontFace;

        byte[] bytes = Utils.readBytes(fontFace.toStream());
        ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length).put(bytes).flip();

        fonts = new Font[5];
        for (int i = 0; i < fonts.length; i++) {
            fonts[i] = new Font(buffer, (int) Math.round(27 * ((i * 0.5) + 1)));
        }
    }

    @Override
    public void setAlpha(double a) {
        mesh.alpha = a;
    }

    @Override
    public void begin(double scale, boolean scaleOnly, boolean big) {
        if (building) throw new RuntimeException("CustomTextRenderer.begin() called twice");

        if (!scaleOnly) mesh.begin();

        if (big) {
            this.font = fonts[fonts.length - 1];
        }
        else {
            double scaleA = Math.floor(scale * 10) / 10;

            int scaleI;
            if (scaleA >= 3) scaleI = 5;
            else if (scaleA >= 2.5) scaleI = 4;
            else if (scaleA >= 2) scaleI = 3;
            else if (scaleA >= 1.5) scaleI = 2;
            else scaleI = 1;

            font = fonts[scaleI - 1];
        }

        this.building = true;
        this.scaleOnly = scaleOnly;

        this.fontScale = font.getHeight() / 27.0;
        this.scale = 1 + (scale - fontScale) / fontScale;
    }

    @Override
    public double getWidth(String text, int length, boolean shadow) {
        if (text.isEmpty()) return 0;

        Font font = building ? this.font : fonts[0];
        return (font.getWidth(text, length) + (shadow ? 1 : 0)) * scale / 1.5;
    }

    @Override
    public double getHeight(boolean shadow) {
        Font font = building ? this.font : fonts[0];
        return (font.getHeight() + 1 + (shadow ? 1 : 0)) * scale / 1.5;
    }

    @Override
    public double render(String text, double x, double y, Color color, boolean shadow) {
        boolean wasBuilding = building;
        if (!wasBuilding) begin();

        double width;
        if (shadow) {
            int preShadowA = SHADOW_COLOR.a;
            SHADOW_COLOR.a = (int) (color.a / 255.0 * preShadowA);

            width = font.render(mesh, text, x + fontScale * scale / 1.5, y + fontScale * scale / 1.5, SHADOW_COLOR, scale / 1.5);
            font.render(mesh, text, x, y, color, scale / 1.5);

            SHADOW_COLOR.a = preShadowA;
        }
        else {
            width = font.render(mesh, text, x, y, color, scale / 1.5);
        }

        if (!wasBuilding) end();
        return width;
    }

    @Override
    public boolean isBuilding() {
        return building;
    }

    @Override
    public void end() {
        if (!building) throw new RuntimeException("CustomTextRenderer.end() called without calling begin()");

        if (!scaleOnly) {
            mesh.end();

            MeshRenderer.begin()
                .attachments(MinecraftClient.getInstance().getFramebuffer())
                .pipeline(CookieRenderPipelines.UI_TEXT)
                .mesh(mesh)
                .sampler("u_Texture", font.texture.getGlTextureView())
                .end();
        }

        building = false;
        scale = 1;
    }

    public void destroy() {
        for (Font font : this.fonts) {
            font.texture.close();
        }
    }
}
