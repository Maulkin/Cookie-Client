/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.utils.entity.fakeplayer;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static meteordevelopment.meteorclient.CookieClient.mc;

public class FakePlayerEntity extends OtherClientPlayerEntity {
    public boolean doNotPush, hideWhenInsideCamera, noHit;

    public FakePlayerEntity(PlayerEntity player, String name, float health, boolean copyInv) {
        super(mc.world, new GameProfile(UUID.randomUUID(), name));

        copyPositionAndRotation(player);

        lastYaw = getYaw();
        lastPitch = getPitch();
        headYaw = player.headYaw;
        lastHeadYaw = headYaw;
        bodyYaw = player.bodyYaw;
        lastBodyYaw = bodyYaw;

        Byte playerModel = player.getDataTracker().get(PlayerEntity.PLAYER_MODEL_PARTS);
        dataTracker.set(PlayerEntity.PLAYER_MODEL_PARTS, playerModel);

        getAttributes().setFrom(player.getAttributes());
        setPose(player.getPose());

        capeX = getX();
        capeY = getY();
        capeZ = getZ();

        if (health <= 20) {
            setHealth(health);
        } else {
            setHealth(health);
            setAbsorptionAmount(health - 20);
        }

        if (copyInv) getInventory().clone(player.getInventory());
    }

    public void spawn() {
        unsetRemoved();
        mc.world.addEntity(this);
    }

    public void despawn() {
        mc.world.removeEntity(getId(), RemovalReason.DISCARDED);
        setRemoved(RemovalReason.DISCARDED);
    }

    @Nullable
    @Override
    protected PlayerListEntry getPlayerListEntry() {
        if (playerListEntry == null) {
            playerListEntry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        }

        return playerListEntry;
    }
}
