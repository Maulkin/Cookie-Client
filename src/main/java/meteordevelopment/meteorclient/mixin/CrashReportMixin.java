/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.mixin;

import meteordevelopment.meteorclient.CookieClient;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.elements.TextHud;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(CrashReport.class)
public abstract class CrashReportMixin {
    @Inject(method = "addDetails", at = @At("TAIL"))
    private void onAddDetails(StringBuilder sb, CallbackInfo info) {
        sb.append("\n\n-- Cookie Client --\n\n");
        sb.append("Version: ").append(CookieClient.VERSION).append("\n");
        if (!CookieClient.BUILD_NUMBER.isEmpty()) {
            sb.append("Build: ").append(CookieClient.BUILD_NUMBER).append("\n");
        }

        if (Modules.get() != null) {
            boolean modulesActive = false;
            for (Category category : Modules.loopCategories()) {
                List<Module> modules = Modules.get().getGroup(category);
                boolean categoryActive = false;

                for (Module module : modules) {
                    if (module == null || !module.isActive()) continue;

                    if (!modulesActive) {
                        modulesActive = true;
                        sb.append("\n[[ Active Modules ]]\n");
                    }

                    if (!categoryActive) {
                        categoryActive = true;
                        sb.append("\n[")
                          .append(category)
                          .append("]:\n");
                    }

                    sb.append(module.name).append("\n");
                }

            }

        }

        if (Hud.get() != null && Hud.get().active) {
            boolean hudActive = false;
            for (HudElement element : Hud.get()) {
                if (element == null || !element.isActive()) continue;

                if (!hudActive) {
                    hudActive = true;
                    sb.append("\n[[ Active Hud Elements ]]\n");
                }

                if (!(element instanceof TextHud textHud)) sb.append(element.info.name).append("\n");
                else {
                    sb.append("Text\n{")
                      .append(textHud.text.get())
                      .append("}\n");
                    if (textHud.shown.get() != TextHud.Shown.Always) {
                        sb.append("(")
                          .append(textHud.shown.get())
                          .append(textHud.condition.get())
                          .append(")\n");
                    }
                }
            }
        }
    }
}
