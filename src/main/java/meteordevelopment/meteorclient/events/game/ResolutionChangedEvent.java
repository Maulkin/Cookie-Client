/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.events.game;

public class ResolutionChangedEvent {
    private static final ResolutionChangedEvent INSTANCE = new ResolutionChangedEvent();

    public static ResolutionChangedEvent get() {
        return INSTANCE;
    }
}
