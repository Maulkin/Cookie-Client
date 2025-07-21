/*
 * This file is part of the Cookie Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.cookieclient.events.render;

public class GetFovEvent {
    private static final GetFovEvent INSTANCE = new GetFovEvent();

    public float fov;

    public static GetFovEvent get(float fov) {
        INSTANCE.fov = fov;
        return INSTANCE;
    }
}
