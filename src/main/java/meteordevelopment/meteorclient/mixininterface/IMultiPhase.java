/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.mixininterface;

import net.minecraft.client.render.RenderLayer;

public interface IMultiPhase {
    RenderLayer.MultiPhaseParameters meteor$getParameters();
}
