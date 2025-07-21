/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.mixininterface.IBakedQuad;
import net.minecraft.client.render.model.BakedQuad;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BakedQuad.class)
public abstract class BakedQuadMixin implements IBakedQuad {
    @Shadow
    @Final
    private int[] vertexData;

    @Override
    public float meteor$getX(int vertexI) {
        return Float.intBitsToFloat(vertexData[vertexI * 8]);
    }

    @Override
    public float meteor$getY(int vertexI) {
        return Float.intBitsToFloat(vertexData[vertexI * 8 + 1]);
    }

    @Override
    public float meteor$getZ(int vertexI) {
        return Float.intBitsToFloat(vertexData[vertexI * 8 + 2]);
    }
}
