/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

/*
 * Credit to Luna (https://github.com/InLieuOfLuna) for making the original Elytra Recast mod (https://github.com/InLieuOfLuna/elytra-recast)!
 */

package meteordevelopment.meteorclient.systems.modules.movement.elytrafly.modes;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.ElytraFlightMode;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.ElytraFlightModes;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class Bounce extends ElytraFlightMode {

    public Bounce() {
        super(ElytraFlightModes.Bounce);
    }

    boolean rubberbanded = false;

    int tickDelay = elytraFly.restartDelay.get();
    double prevFov;

    @Override
    public void onTick() {
        super.onTick();

        if (mc.options.jumpKey.isPressed() && !mc.player.isGliding() && !elytraFly.manualTakeoff.get()) mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));

        // Make sure all the conditions are met (player has an elytra, isn't in water, etc)
        if (checkConditions(mc.player)) {
            if (!rubberbanded) {
                if (prevFov != 0 && !elytraFly.sprint.get()) mc.options.getFovEffectScale().setValue(0.0); // This stops the FOV effects from constantly going on and off.
                if (elytraFly.autoJump.get()) mc.options.jumpKey.setPressed(true);
                mc.options.forwardKey.setPressed(true);
                mc.player.setYaw(getYawDirection());
                if (elytraFly.lockPitch.get()) mc.player.setPitch(elytraFly.pitch.get().floatValue());
            }

            if (!elytraFly.sprint.get()) {
                // Sprinting all the time (when not on ground) makes it rubberband on certain anticheats.
                if (mc.player.isGliding()) mc.player.setSprinting(mc.player.isOnGround());
                else mc.player.setSprinting(true);
            }

            // Rubberbanding
            if (rubberbanded && elytraFly.restart.get()) {
                if (tickDelay > 0) {
                    tickDelay--;
                } else {
                    mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
                    rubberbanded = false;
                    tickDelay = elytraFly.restartDelay.get();
                }
            }
        }
    }

    @Override
    public void onPreTick() {
        super.onPreTick();

        if (checkConditions(mc.player) && elytraFly.sprint.get()) mc.player.setSprinting(true);
    }

    private void unpress() {
        mc.options.forwardKey.setPressed(false);
        if (elytraFly.autoJump.get()) mc.options.jumpKey.setPressed(false);
    }

    @Override
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.packet instanceof PlayerPositionLookS2CPacket) {
            rubberbanded = true;
            mc.player.stopGliding();
        }
    }

    @Override
    public void onPacketSend(PacketEvent.Send event) {
        if (event.packet instanceof ClientCommandC2SPacket && ((ClientCommandC2SPacket) event.packet).getMode().equals(ClientCommandC2SPacket.Mode.START_FALL_FLYING) && !elytraFly.sprint.get()) {
            mc.player.setSprinting(true);
        }
    }

    public static boolean recastElytra(ClientPlayerEntity player) {
        if (checkConditions(player) && ignoreGround(player)) {
            player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            return true;
        } else return false;
    }

    public static boolean checkConditions(ClientPlayerEntity player) {
        ItemStack itemStack = player.getEquippedStack(EquipmentSlot.CHEST);
        return (!player.getAbilities().flying && !player.hasVehicle() && !player.isClimbing() && itemStack.contains(DataComponentTypes.GLIDER) && !itemStack.willBreakNextUse());
    }

    private static boolean ignoreGround(ClientPlayerEntity player) {
        if (!player.isTouchingWater() && !player.hasStatusEffect(StatusEffects.LEVITATION)) {
            ItemStack itemStack = player.getEquippedStack(EquipmentSlot.CHEST);
            if (itemStack.contains(DataComponentTypes.GLIDER) && !itemStack.willBreakNextUse()) {
                player.startGliding();
                return true;
            } else return false;
        } else return false;
    }

    private float getYawDirection() {
        return switch (elytraFly.yawLockMode.get()) {
            case None -> mc.player.getYaw();
            case Smart -> Math.round((mc.player.getYaw() + 1f) / 45f) * 45f;
            case Simple -> elytraFly.yaw.get().floatValue();
        };

    }

    @Override
    public void onActivate() {
        prevFov = mc.options.getFovEffectScale().getValue();
    }

    @Override
    public void onDeactivate() {
        unpress();
        rubberbanded = false;
        if (prevFov != 0 && !elytraFly.sprint.get()) mc.options.getFovEffectScale().setValue(prevFov);
    }
}
