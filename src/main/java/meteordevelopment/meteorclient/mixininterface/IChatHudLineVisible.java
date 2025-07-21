/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.mixininterface;

public interface IChatHudLineVisible extends IChatHudLine {
    boolean meteor$isStartOfEntry();
    void meteor$setStartOfEntry(boolean start);
}
