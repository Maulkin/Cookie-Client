/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.mixin;

import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.screen.HorseScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HorseScreenHandler.class)
public interface HorseScreenHandlerAccessor {
    @Accessor("entity")
    AbstractHorseEntity getEntity();
}
