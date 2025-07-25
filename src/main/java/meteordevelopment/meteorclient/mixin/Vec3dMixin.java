/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.mixininterface.IVec3d;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Vec3d.class)
public abstract class Vec3dMixin implements IVec3d {
    @Shadow @Final @Mutable public double x;
    @Shadow @Final @Mutable public double y;
    @Shadow @Final @Mutable public double z;

    @Override
    public void meteor$set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void meteor$setXZ(double x, double z) {
        this.x = x;
        this.z = z;
    }

    @Override
    public void meteor$setY(double y) {
        this.y = y;
    }
}
