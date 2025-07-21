/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.events.meteor;

@SuppressWarnings("InstantiationOfUtilityClass")
public class CustomFontChangedEvent {
    private static final CustomFontChangedEvent INSTANCE = new CustomFontChangedEvent();

    public static CustomFontChangedEvent get() {
        return INSTANCE;
    }
}
