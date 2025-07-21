/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.events.entity.player;

public class JumpVelocityMultiplierEvent {
    private static final JumpVelocityMultiplierEvent INSTANCE = new JumpVelocityMultiplierEvent();

    public float multiplier = 1;

    public static JumpVelocityMultiplierEvent get() {
        INSTANCE.multiplier = 1;
        return INSTANCE;
    }
}
