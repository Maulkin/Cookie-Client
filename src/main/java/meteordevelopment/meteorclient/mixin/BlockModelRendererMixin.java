/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.render.Xray;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

@Mixin(BlockModelRenderer.class)
public abstract class BlockModelRendererMixin {
    @Unique
    private final ThreadLocal<Integer> alphas = new ThreadLocal<>();

    @Inject(method = {"renderSmooth", "renderFlat"}, at = @At("HEAD"), cancellable = true)
    private void onRenderSmooth(BlockRenderView world, List<BlockModelPart> parts, BlockState state, BlockPos pos, MatrixStack matrices, VertexConsumer vertexConsumer, boolean cull, int overlay, CallbackInfo ci) {
        int alpha = Xray.getAlpha(state, pos);

        if (alpha == 0) ci.cancel();
        else alphas.set(alpha);
    }

    @ModifyArgs(method = "renderQuad", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;quad(Lnet/minecraft/client/util/math/MatrixStack$Entry;Lnet/minecraft/client/render/model/BakedQuad;[FFFFF[IIZ)V"))
    private void modifyXrayAlpha(final Args args) {
        final int alpha = alphas.get();
        args.set(6, alpha == -1 ? args.get(6) : alpha / 255f);
    }
}
