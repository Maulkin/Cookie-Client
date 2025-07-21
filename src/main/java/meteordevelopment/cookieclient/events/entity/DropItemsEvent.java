/*
 * This file is part of the Cookie Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.cookieclient.events.entity;

import meteordevelopment.cookieclient.events.Cancellable;
import net.minecraft.item.ItemStack;

public class DropItemsEvent extends Cancellable {
    private static final DropItemsEvent INSTANCE = new DropItemsEvent();

    public ItemStack itemStack;

    public static DropItemsEvent get(ItemStack itemStack) {
        INSTANCE.setCancelled(false);
        INSTANCE.itemStack = itemStack;
        return INSTANCE;
    }
}
