/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.mixininterface;

import net.minecraft.util.math.BlockPos;

public interface IBox {
    void meteor$expand(double v);

    void meteor$set(double x1, double y1, double z1, double x2, double y2, double z2);

    default void meteor$set(BlockPos pos) {
        meteor$set(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
    }
}
