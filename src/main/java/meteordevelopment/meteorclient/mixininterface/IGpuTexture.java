/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.mixininterface;

import com.mojang.blaze3d.textures.AddressMode;

public interface IGpuTexture {
    AddressMode  meteor$getAddressModeU();
    AddressMode  meteor$getAddressModeV();
}
