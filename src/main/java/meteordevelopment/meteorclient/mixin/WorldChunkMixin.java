/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.CookieClient;
import meteordevelopment.meteorclient.events.world.BlockUpdateEvent;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldChunk.class)
public abstract class WorldChunkMixin {
    @Shadow @Final World world;

    @Inject(method = "setBlockState", at = @At("TAIL"))
    private void onSetBlockState(BlockPos pos, BlockState state, int flags, CallbackInfoReturnable<BlockState> info) {
        if (world.isClient) CookieClient.EVENT_BUS.post(BlockUpdateEvent.get(pos, info.getReturnValue(), state));
    }
}
