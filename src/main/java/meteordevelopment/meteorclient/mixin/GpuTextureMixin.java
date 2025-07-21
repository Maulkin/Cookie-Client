/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.mixin;

import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.GpuTexture;
import meteordevelopment.meteorclient.mixininterface.IGpuTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GpuTexture.class)
public abstract class GpuTextureMixin implements IGpuTexture {
    @Shadow(remap = false)
    protected AddressMode addressModeU;

    @Shadow(remap = false)
    protected AddressMode addressModeV;

    @Override
    public AddressMode meteor$getAddressModeU() {
        return this.addressModeU;
    }

    @Override
    public AddressMode meteor$getAddressModeV() {
        return this.addressModeV;
    }
}
