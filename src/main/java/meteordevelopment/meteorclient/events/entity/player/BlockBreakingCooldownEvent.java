/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.events.entity.player;

public class BlockBreakingCooldownEvent {
    private static final BlockBreakingCooldownEvent INSTANCE = new BlockBreakingCooldownEvent();

    public int cooldown;

    public static BlockBreakingCooldownEvent get(int cooldown) {
        INSTANCE.cooldown = cooldown;
        return INSTANCE;
    }
}
