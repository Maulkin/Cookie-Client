/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.events.packets;

import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;

public class InventoryEvent {
    private static final InventoryEvent INSTANCE = new InventoryEvent();

    public InventoryS2CPacket packet;

    public static InventoryEvent get(InventoryS2CPacket packet) {
        INSTANCE.packet = packet;
        return INSTANCE;
    }
}
