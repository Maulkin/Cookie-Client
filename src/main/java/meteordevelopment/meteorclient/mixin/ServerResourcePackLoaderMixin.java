/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.ServerSpoof;
import net.minecraft.client.resource.server.ServerResourcePackLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerResourcePackLoader.class)
public abstract class ServerResourcePackLoaderMixin {
    @Inject(method = "onReloadSuccess", at = @At("TAIL"))
    private void removeInactivePacksTail(CallbackInfo ci) {
        Modules.get().get(ServerSpoof.class).silentAcceptResourcePack = false;
    }
}
