/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.events.world;

import net.minecraft.world.chunk.WorldChunk;

/**
 * @implNote Shouldn't be put in a {@link meteordevelopment.meteorclient.utils.misc.Pool} to avoid a race-condition, or in a {@link ThreadLocal} as it is shared between threads.
 * @author Crosby
 */
public record ChunkDataEvent(WorldChunk chunk) {}
