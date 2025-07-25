/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.systems.modules.movement.elytrafly;

import meteordevelopment.meteorclient.CookieClient;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.modes.Bounce;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.modes.Packet;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.modes.Pitch40;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.modes.Vanilla;
import meteordevelopment.meteorclient.systems.modules.player.ChestSwap;
import meteordevelopment.meteorclient.systems.modules.player.Rotation;
import meteordevelopment.meteorclient.systems.modules.render.Freecam;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class ElytraFly extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgInventory = settings.createGroup("Inventory");
    private final SettingGroup sgAutopilot = settings.createGroup("Autopilot");

    // General

    public final Setting<ElytraFlightModes> flightMode = sgGeneral.add(new EnumSetting.Builder<ElytraFlightModes>()
        .name("mode")
        .description("The mode of flying.")
        .defaultValue(ElytraFlightModes.Vanilla)
        .onModuleActivated(flightModesSetting -> onModeChanged(flightModesSetting.get()))
        .onChanged(this::onModeChanged)
        .build()
    );

    public final Setting<Boolean> autoTakeOff = sgGeneral.add(new BoolSetting.Builder()
        .name("auto-take-off")
        .description("Automatically takes off when you hold jump without needing to double jump.")
        .defaultValue(false)
        .visible(() -> flightMode.get() != ElytraFlightModes.Pitch40 && flightMode.get() != ElytraFlightModes.Bounce)
        .build()
    );

    public final Setting<Double> fallMultiplier = sgGeneral.add(new DoubleSetting.Builder()
        .name("fall-multiplier")
        .description("Controls how fast will you go down naturally.")
        .defaultValue(0.01)
        .min(0)
        .visible(() -> flightMode.get() != ElytraFlightModes.Pitch40 && flightMode.get() != ElytraFlightModes.Bounce)
        .build()
    );

    public final Setting<Double> horizontalSpeed = sgGeneral.add(new DoubleSetting.Builder()
        .name("horizontal-speed")
        .description("How fast you go forward and backward.")
        .defaultValue(1)
        .min(0)
        .visible(() -> flightMode.get() != ElytraFlightModes.Pitch40 && flightMode.get() != ElytraFlightModes.Bounce)
        .build()
    );

    public final Setting<Double> verticalSpeed = sgGeneral.add(new DoubleSetting.Builder()
        .name("vertical-speed")
        .description("How fast you go up and down.")
        .defaultValue(1)
        .min(0)
        .visible(() -> flightMode.get() != ElytraFlightModes.Pitch40 && flightMode.get() != ElytraFlightModes.Bounce)
        .build()
    );

    public final Setting<Boolean> acceleration = sgGeneral.add(new BoolSetting.Builder()
        .name("acceleration")
        .defaultValue(false)
        .visible(() -> flightMode.get() != ElytraFlightModes.Pitch40 && flightMode.get() != ElytraFlightModes.Bounce)
        .build()
    );

    public final Setting<Double> accelerationStep = sgGeneral.add(new DoubleSetting.Builder()
        .name("acceleration-step")
        .min(0.1)
        .max(5)
        .defaultValue(1)
        .visible(() -> flightMode.get() != ElytraFlightModes.Pitch40 && acceleration.get() && flightMode.get() != ElytraFlightModes.Bounce)
        .build()
    );

    public final Setting<Double> accelerationMin = sgGeneral.add(new DoubleSetting.Builder()
        .name("acceleration-start")
        .min(0.1)
        .defaultValue(0)
        .visible(() -> flightMode.get() != ElytraFlightModes.Pitch40 && acceleration.get() && flightMode.get() != ElytraFlightModes.Bounce)
        .build()
    );

    public final Setting<Boolean> stopInWater = sgGeneral.add(new BoolSetting.Builder()
        .name("stop-in-water")
        .description("Stops flying in water.")
        .defaultValue(true)
        .visible(() -> flightMode.get() != ElytraFlightModes.Bounce)
        .build()
    );

    public final Setting<Boolean> dontGoIntoUnloadedChunks = sgGeneral.add(new BoolSetting.Builder()
        .name("no-unloaded-chunks")
        .description("Stops you from going into unloaded chunks.")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> autoHover = sgGeneral.add(new BoolSetting.Builder()
        .name("auto-hover")
        .description("Automatically hover .3 blocks off ground when holding shift.")
        .defaultValue(false)
        .visible(() -> flightMode.get() != ElytraFlightModes.Bounce)
        .build()
    );

    public final Setting<Boolean> noCrash = sgGeneral.add(new BoolSetting.Builder()
        .name("no-crash")
        .description("Stops you from going into walls.")
        .defaultValue(false)
        .visible(() -> flightMode.get() != ElytraFlightModes.Bounce)
        .build()
    );

    public final Setting<Integer> crashLookAhead = sgGeneral.add(new IntSetting.Builder()
        .name("crash-look-ahead")
        .description("Distance to look ahead when flying.")
        .defaultValue(5)
        .range(1, 15)
        .sliderMin(1)
        .visible(() -> noCrash.get() && flightMode.get() != ElytraFlightModes.Bounce)
        .build()
    );

    private final Setting<Boolean> instaDrop = sgGeneral.add(new BoolSetting.Builder()
        .name("insta-drop")
        .description("Makes you drop out of flight instantly.")
        .defaultValue(false)
        .visible(() -> flightMode.get() != ElytraFlightModes.Bounce)
        .build()
    );

    public final Setting<Double> pitch40lowerBounds = sgGeneral.add(new DoubleSetting.Builder()
        .name("pitch40-lower-bounds")
        .description("The bottom height boundary for pitch40.")
        .defaultValue(80)
        .min(-128)
        .sliderMax(360)
        .visible(() -> flightMode.get() == ElytraFlightModes.Pitch40)
        .build()
    );

    public final Setting<Double> pitch40upperBounds = sgGeneral.add(new DoubleSetting.Builder()
        .name("pitch40-upper-bounds")
        .description("The upper height boundary for pitch40.")
        .defaultValue(120)
        .min(-128)
        .sliderMax(360)
        .visible(() -> flightMode.get() == ElytraFlightModes.Pitch40)
        .build()
    );

    public final Setting<Double> pitch40rotationSpeed = sgGeneral.add(new DoubleSetting.Builder()
        .name("pitch40-rotate-speed")
        .description("The speed for pitch rotation (degrees per tick)")
        .defaultValue(15)
        .min(1)
        .sliderMax(20)
        .visible(() -> flightMode.get() == ElytraFlightModes.Pitch40)
        .build()
    );

    public final Setting<Boolean> autoJump = sgGeneral.add(new BoolSetting.Builder()
        .name("auto-jump")
        .description("Automatically jumps for you.")
        .defaultValue(true)
        .visible(() -> flightMode.get() == ElytraFlightModes.Bounce)
        .build()
    );

    public final Setting<Rotation.LockMode> yawLockMode = sgGeneral.add(new EnumSetting.Builder<Rotation.LockMode>()
        .name("yaw-lock")
        .description("Whether to enable yaw lock or not")
        .defaultValue(Rotation.LockMode.Smart)
        .visible(() -> flightMode.get() == ElytraFlightModes.Bounce)
        .build()
    );

    public final Setting<Double> yaw = sgGeneral.add(new DoubleSetting.Builder()
        .name("yaw")
        .description("The yaw angle to look at when using simple rotation lock in bounce mode.")
        .defaultValue(0)
        .range(0, 360)
        .sliderRange(0,360)
        .visible(() -> flightMode.get() == ElytraFlightModes.Bounce && yawLockMode.get() == Rotation.LockMode.Simple)
        .build()
    );

    public final Setting<Boolean> lockPitch = sgGeneral.add(new BoolSetting.Builder()
        .name("pitch-lock")
        .description("Whether to lock your pitch angle.")
        .defaultValue(true)
        .visible(() -> flightMode.get() == ElytraFlightModes.Bounce)
        .build()
    );

    public final Setting<Double> pitch = sgGeneral.add(new DoubleSetting.Builder()
        .name("pitch")
        .description("The pitch angle to look at when using the bounce mode.")
        .defaultValue(85)
        .range(0, 90)
        .sliderRange(0, 90)
        .visible(() -> flightMode.get() == ElytraFlightModes.Bounce && lockPitch.get())
        .build()
    );

    public final Setting<Boolean> restart = sgGeneral.add(new BoolSetting.Builder()
        .name("restart")
        .description("Restarts flying with the elytra when rubberbanding.")
        .defaultValue(true)
        .visible(() -> flightMode.get() == ElytraFlightModes.Bounce)
        .build()
    );

    public final Setting<Integer> restartDelay = sgGeneral.add(new IntSetting.Builder()
        .name("restart-delay")
        .description("How many ticks to wait before restarting the elytra again after rubberbanding.")
        .defaultValue(7)
        .min(0)
        .sliderRange(0, 20)
        .visible(() -> flightMode.get() == ElytraFlightModes.Bounce && restart.get())
        .build()
    );

    public final Setting<Boolean> sprint = sgGeneral.add(new BoolSetting.Builder()
        .name("sprint-constantly")
        .description("Sprints all the time. If turned off, it will only sprint when the player is touching the ground.")
        .defaultValue(true)
        .visible(() -> flightMode.get() == ElytraFlightModes.Bounce)
        .build()
    );

    public final Setting<Boolean> manualTakeoff = sgGeneral.add(new BoolSetting.Builder()
        .name("manual-takeoff")
        .description("Does not automatically take off.")
        .defaultValue(false)
        .visible(() -> flightMode.get() == ElytraFlightModes.Bounce)
        .build()
    );

    // Inventory

    public final Setting<Boolean> replace = sgInventory.add(new BoolSetting.Builder()
        .name("elytra-replace")
        .description("Replaces broken elytra with a new elytra.")
        .defaultValue(false)
        .build()
    );

    public final Setting<Integer> replaceDurability = sgInventory.add(new IntSetting.Builder()
        .name("replace-durability")
        .description("The durability threshold your elytra will be replaced at.")
        .defaultValue(2)
        .range(1, Items.ELYTRA.getComponents().getOrDefault(DataComponentTypes.MAX_DAMAGE, 432) - 1)
        .sliderRange(1, Items.ELYTRA.getComponents().getOrDefault(DataComponentTypes.MAX_DAMAGE, 432) - 1)
        .visible(replace::get)
        .build()
    );

    public final Setting<ChestSwapMode> chestSwap = sgInventory.add(new EnumSetting.Builder<ChestSwapMode>()
        .name("chest-swap")
        .description("Enables ChestSwap when toggling this module.")
        .defaultValue(ChestSwapMode.Never)
        .build()
    );

    public final Setting<Boolean> autoReplenish = sgInventory.add(new BoolSetting.Builder()
        .name("replenish-fireworks")
        .description("Moves fireworks into a selected hotbar slot.")
        .defaultValue(false)
        .build()
    );

    public final Setting<Integer> replenishSlot = sgInventory.add(new IntSetting.Builder()
        .name("replenish-slot")
        .description("The slot auto move moves fireworks to.")
        .defaultValue(9)
        .range(1, 9)
        .sliderRange(1, 9)
        .visible(autoReplenish::get)
        .build()
    );

    // Autopilot

    public final Setting<Boolean> autoPilot = sgAutopilot.add(new BoolSetting.Builder()
        .name("auto-pilot")
        .description("Moves forward while elytra flying.")
        .defaultValue(false)
        .visible(() -> flightMode.get() != ElytraFlightModes.Pitch40 && flightMode.get() != ElytraFlightModes.Bounce)
        .build()
    );

    public final Setting<Boolean> useFireworks = sgAutopilot.add(new BoolSetting.Builder()
        .name("use-fireworks")
        .description("Uses firework rockets every second of your choice.")
        .defaultValue(false)
        .visible(() -> autoPilot.get() && flightMode.get() != ElytraFlightModes.Pitch40 && flightMode.get() != ElytraFlightModes.Bounce)
        .build()
    );

    public final Setting<Double> autoPilotFireworkDelay = sgAutopilot.add(new DoubleSetting.Builder()
        .name("firework-delay")
        .description("The delay in seconds in between using fireworks if \"Use Fireworks\" is enabled.")
        .min(1)
        .defaultValue(8)
        .sliderMax(20)
        .visible(() -> useFireworks.get() && flightMode.get() != ElytraFlightModes.Pitch40 && flightMode.get() != ElytraFlightModes.Bounce)
        .build()
    );

    public final Setting<Double> autoPilotMinimumHeight = sgAutopilot.add(new DoubleSetting.Builder()
        .name("minimum-height")
        .description("The minimum height for autopilot.")
        .defaultValue(120)
        .min(-128)
        .sliderMax(260)
        .visible(() -> autoPilot.get() && flightMode.get() != ElytraFlightModes.Pitch40 && flightMode.get() != ElytraFlightModes.Bounce)
        .build()
    );

    private ElytraFlightMode currentMode = new Vanilla();

    public ElytraFly() {
        super(Categories.Movement, "elytra-fly", "Gives you more control over your elytra.");
    }

    @Override
    public void onActivate() {
        currentMode.onActivate();
        if ((chestSwap.get() == ChestSwapMode.Always || chestSwap.get() == ChestSwapMode.WaitForGround)
            && mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() != Items.ELYTRA && isActive()) {
            Modules.get().get(ChestSwap.class).swap();
        }
    }

    @Override
    public void onDeactivate() {
        if (autoPilot.get()) mc.options.forwardKey.setPressed(false);

        if (chestSwap.get() == ChestSwapMode.Always && mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
            Modules.get().get(ChestSwap.class).swap();
        } else if (chestSwap.get() == ChestSwapMode.WaitForGround) {
            enableGroundListener();
        }

        if (mc.player.isGliding() && instaDrop.get()) {
            enableInstaDropListener();
        }

        currentMode.onDeactivate();
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        if (!(mc.player.getEquippedStack(EquipmentSlot.CHEST).contains(DataComponentTypes.GLIDER))) return;

        currentMode.autoTakeoff();

        if (mc.player.isGliding()) {

            if (flightMode.get() != ElytraFlightModes.Bounce) {
                currentMode.velX = 0;
                currentMode.velY = event.movement.y;
                currentMode.velZ = 0;
                currentMode.forward = Vec3d.fromPolar(0, mc.player.getYaw()).multiply(0.1);
                currentMode.right = Vec3d.fromPolar(0, mc.player.getYaw() + 90).multiply(0.1);

                // Handle stopInWater
                if (mc.player.isTouchingWater() && stopInWater.get()) {
                    mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
                    return;
                }

                currentMode.handleFallMultiplier();
                currentMode.handleAutopilot();

                currentMode.handleAcceleration();
                currentMode.handleHorizontalSpeed(event);
                currentMode.handleVerticalSpeed(event);
            }

            int chunkX = (int) ((mc.player.getX() + currentMode.velX) / 16);
            int chunkZ = (int) ((mc.player.getZ() + currentMode.velZ) / 16);
            if (dontGoIntoUnloadedChunks.get()) {
                if (mc.world.getChunkManager().isChunkLoaded(chunkX, chunkZ)) {
                    if (flightMode.get() != ElytraFlightModes.Bounce) ((IVec3d) event.movement).meteor$set(currentMode.velX, currentMode.velY, currentMode.velZ);
                } else {
                    currentMode.zeroAcceleration();
                    ((IVec3d) event.movement).meteor$set(0, currentMode.velY, 0);
                }
            } else if (flightMode.get() != ElytraFlightModes.Bounce) ((IVec3d) event.movement).meteor$set(currentMode.velX, currentMode.velY, currentMode.velZ);

            if (flightMode.get() != ElytraFlightModes.Bounce) currentMode.onPlayerMove();
        } else {
            if (currentMode.lastForwardPressed && flightMode.get() != ElytraFlightModes.Bounce) {
                mc.options.forwardKey.setPressed(false);
                currentMode.lastForwardPressed = false;
            }
        }

        if (noCrash.get() && mc.player.isGliding() && flightMode.get() != ElytraFlightModes.Bounce) {
            Vec3d lookAheadPos = mc.player.getPos().add(mc.player.getVelocity().normalize().multiply(crashLookAhead.get()));
            RaycastContext raycastContext = new RaycastContext(mc.player.getPos(), new Vec3d(lookAheadPos.getX(), mc.player.getY(), lookAheadPos.getZ()), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player);
            BlockHitResult hitResult = mc.world.raycast(raycastContext);
            if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
                ((IVec3d) event.movement).meteor$set(0, currentMode.velY, 0);
            }
        }

        if (autoHover.get() && mc.player.input.playerInput.sneak() && !Modules.get().get(Freecam.class).isActive() && mc.player.isGliding() && flightMode.get() != ElytraFlightModes.Bounce) {
            BlockState underState = mc.world.getBlockState(mc.player.getBlockPos().down());
            Block under = underState.getBlock();
            BlockState under2State = mc.world.getBlockState(mc.player.getBlockPos().down().down());
            Block under2 = under2State.getBlock();

            final boolean underCollidable = under.collidable || !underState.getFluidState().isEmpty();
            final boolean under2Collidable = under2.collidable || !under2State.getFluidState().isEmpty();

            if (!underCollidable && under2Collidable) {
                ((IVec3d)event.movement).meteor$set(event.movement.x, -0.1f, event.movement.z);

                mc.player.setPitch(MathHelper.clamp(mc.player.getPitch(0), -50.f, 20.f)); // clamp between -50 and 20 (>= 30 will pop you off, but lag makes that threshold lower)
            }

            if (underCollidable) {
                ((IVec3d)event.movement).meteor$set(event.movement.x, -0.03f, event.movement.z);

                mc.player.setPitch(MathHelper.clamp(mc.player.getPitch(0), -50.f, 20.f));

                if (mc.player.getPos().y <= mc.player.getBlockPos().down().getY() + 1.34f) {
                    ((IVec3d)event.movement).meteor$set(event.movement.x, 0, event.movement.z);
                    mc.player.setSneaking(false);
                }
            }
        }
    }

    public boolean canPacketEfly() {
        return isActive() && flightMode.get() == ElytraFlightModes.Packet && mc.player.getEquippedStack(EquipmentSlot.CHEST).contains(DataComponentTypes.GLIDER) && !mc.player.isOnGround();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        currentMode.onTick();
    }

    @EventHandler
    private void onPreTick(TickEvent.Pre event) {
        currentMode.onPreTick();
    }

    @EventHandler
    private void onPacketSend(PacketEvent.Send event) {
        currentMode.onPacketSend(event);
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event) {
        if (event.packet instanceof PlayerPositionLookS2CPacket) currentMode.zeroAcceleration();

        currentMode.onPacketReceive(event);
    }

    private void onModeChanged(ElytraFlightModes mode) {
        switch (mode) {
            case Vanilla -> currentMode = new Vanilla();
            case Packet -> currentMode = new Packet();
            case Pitch40 -> {
                currentMode = new Pitch40();
                autoPilot.set(false); // Pitch 40 is an autopilot of its own
            }
            case Bounce -> currentMode = new Bounce();
        }
    }

    //Ground
    private class StaticGroundListener {
        @EventHandler
        private void chestSwapGroundListener(PlayerMoveEvent event) {
            if (mc.player != null && mc.player.isOnGround()) {
                if (mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
                    Modules.get().get(ChestSwap.class).swap();
                    disableGroundListener();
                }
            }
        }
    }

    private final StaticGroundListener staticGroundListener = new StaticGroundListener();

    protected void enableGroundListener() {
        CookieClient.EVENT_BUS.subscribe(staticGroundListener);
    }

    protected void disableGroundListener() {
        CookieClient.EVENT_BUS.unsubscribe(staticGroundListener);
    }

    //Drop
    private class StaticInstaDropListener {
        @EventHandler
        private void onInstadropTick(TickEvent.Post event) {
            if (mc.player != null && mc.player.isGliding()) {
                mc.player.setVelocity(0, 0, 0);
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true, mc.player.horizontalCollision));
            } else {
                disableInstaDropListener();
            }
        }
    }

    private final StaticInstaDropListener staticInstadropListener = new StaticInstaDropListener();

    protected void enableInstaDropListener() {
        CookieClient.EVENT_BUS.subscribe(staticInstadropListener);
    }

    protected void disableInstaDropListener() {
        CookieClient.EVENT_BUS.unsubscribe(staticInstadropListener);
    }

    @Override
    public String getInfoString() {
        return currentMode.getHudString();
    }

    public enum ChestSwapMode {
        Always,
        Never,
        WaitForGround
    }

    public enum AutoPilotMode {
        Vanilla,
        Pitch40
    }
}
