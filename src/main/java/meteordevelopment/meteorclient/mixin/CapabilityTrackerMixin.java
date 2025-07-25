/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.mixin;

import com.mojang.blaze3d.opengl.GlStateManager;
import meteordevelopment.meteorclient.mixininterface.ICapabilityTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GlStateManager.CapabilityTracker.class)
public abstract class CapabilityTrackerMixin implements ICapabilityTracker {
    @Shadow
    private boolean state;

    @Shadow
    public abstract void setState(boolean state);

    @Override
    public boolean meteor$get() {
        return state;
    }

    @Override
    public void meteor$set(boolean state) {
        setState(state);
    }
}
