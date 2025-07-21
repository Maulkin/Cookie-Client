/*
 * This file is part of the Cookie Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.mixininterface;

import com.mojang.authlib.GameProfile;

public interface IChatHudLine {
    String meteor$getText();

    int meteor$getId();

    void meteor$setId(int id);

    GameProfile meteor$getSender();

    void meteor$setSender(GameProfile profile);
}
