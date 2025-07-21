/*
 * This file is part of the Cookie Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.systems.modules.world;

import meteordevelopment.cookieclient.events.world.TickEvent;
import meteordevelopment.cookieclient.settings.BoolSetting;
import meteordevelopment.cookieclient.settings.EntityTypeListSetting;
import meteordevelopment.cookieclient.settings.Setting;
import meteordevelopment.cookieclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.cookieclient.utils.entity.EntityUtils;
import meteordevelopment.cookieclient.utils.player.PlayerUtils;
import meteordevelopment.cookieclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Hand;

import java.util.Set;

public class AutoMount extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> checkSaddle = sgGeneral.add(new BoolSetting.Builder()
        .name("check-saddle")
        .description("Checks if the entity contains a saddle before mounting.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate")
        .description("Faces the entity you mount.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Set<EntityType<?>>> entities = sgGeneral.add(new EntityTypeListSetting.Builder()
        .name("entities")
        .description("Rideable entities.")
        .filter(EntityUtils::isRideable)
        .build()
    );

    public AutoMount() {
        super(Categories.World, "auto-mount", "Automatically mounts entities.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player.hasVehicle()) return;
        if (mc.player.isSneaking()) return;
        if (mc.player.getMainHandStack().getItem() instanceof SpawnEggItem) return;

        for (Entity entity : mc.world.getEntities()) {
            if (!entities.get().contains(entity.getType())) continue;
            if (!PlayerUtils.isWithin(entity, 4)) continue;
            if ((entity instanceof MobEntity mobEntity) && !(mobEntity.hasSaddleEquipped())) continue;
            if (!(entity instanceof LlamaEntity) && entity instanceof MobEntity mobEntity && checkSaddle.get() && !mobEntity.hasSaddleEquipped()) continue;
            interact(entity);
            return;
        }
    }

    private void interact(Entity entity) {
        if (rotate.get()) Rotations.rotate(Rotations.getYaw(entity), Rotations.getPitch(entity), -100, () -> mc.interactionManager.interactEntity(mc.player, entity, Hand.MAIN_HAND));
        else mc.interactionManager.interactEntity(mc.player, entity, Hand.MAIN_HAND);
    }
}
