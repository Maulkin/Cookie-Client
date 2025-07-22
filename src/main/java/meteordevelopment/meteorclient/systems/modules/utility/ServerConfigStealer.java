/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.systems.modules.utility;

import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;

public class ServerConfigStealer extends Module {
    public ServerConfigStealer() {
        super(Categories.Utility, "server-config-stealer", "Attempts to collect public server config info such as version, software, and plugins.");
    }

    @Override
    public void onActivate() {
        sendCmd("/pl");
        sendCmd("/plugins");
        sendCmd("/about");
        sendCmd("/version");
        sendCmd("/forge");
        sendCmd("/mods");

        info("Sent info requests. Watch chat for plugin/version info.");
    }

    private void sendCmd(String cmd) {
        if (mc.player != null) {
            ChatUtils.sendPlayerMsg(cmd);
        }
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event) {
        if (event.packet instanceof GameMessageS2CPacket packet) {
            String msg = packet.content().getString();
            String lower = msg.toLowerCase();
            if (lower.contains("plugin") || lower.contains("version")) {
                info("[ServerConfigStealer] " + msg);
            }
        }
    }

    @EventHandler
    private void onGameJoin(GameJoinedEvent event) {
        String serverBrand = "unknown";
        if (mc.getCurrentServerEntry() != null && mc.getCurrentServerEntry().label != null) {
            serverBrand = mc.getCurrentServerEntry().label.getString();
        }
        info("Server Brand: " + serverBrand);
    }
}
