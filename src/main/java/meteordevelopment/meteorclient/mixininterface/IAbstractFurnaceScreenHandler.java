/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.mixininterface;

import net.minecraft.item.ItemStack;

public interface IAbstractFurnaceScreenHandler {
    boolean meteor$isItemSmeltable(ItemStack itemStack);
}
