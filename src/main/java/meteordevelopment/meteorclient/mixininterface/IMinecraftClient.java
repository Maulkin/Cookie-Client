/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.mixininterface;

import net.minecraft.client.gl.Framebuffer;

public interface IMinecraftClient {
    void meteor$rightClick();

    void meteor$setFramebuffer(Framebuffer framebuffer);
}
