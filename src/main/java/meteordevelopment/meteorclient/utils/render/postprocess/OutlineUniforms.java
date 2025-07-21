/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.utils.render.postprocess;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import net.minecraft.client.gl.DynamicUniformStorage;

import java.nio.ByteBuffer;

public class OutlineUniforms {
    private static final int UNIFORM_SIZE = new Std140SizeCalculator()
        .putInt()
        .putFloat()
        .putInt()
        .putFloat()
        .get();

    private static final DynamicUniformStorage<Data> STORAGE = new DynamicUniformStorage<>("Cookie - Outline UBO", UNIFORM_SIZE, 16);

    public static void flipFrame() {
        STORAGE.clear();
    }

    public static GpuBufferSlice write(int width, float fillOpacity, int shapeMode, float glowMultiplier) {
        return STORAGE.write(new Data(width, fillOpacity, shapeMode, glowMultiplier));
    }

    private record Data(int width, float fillOpacity, int shapeMode, float glowMultiplier) implements DynamicUniformStorage.Uploadable {
        @Override
        public void write(ByteBuffer buffer) {
            Std140Builder.intoBuffer(buffer)
                .putInt(width)
                .putFloat(fillOpacity)
                .putInt(shapeMode)
                .putFloat(glowMultiplier);
        }
    }
}
