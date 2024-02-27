package io.github.pandier.multisnake.network.packet.server;

import io.github.pandier.multisnake.network.packet.message.PacketMessage;
import org.jetbrains.annotations.NotNull;

/**
 * Sent by the server when a client has successfully authenticated.
 */
public record ServerLoginSuccessPacket() implements ServerPacket {

    @Override
    public void write(@NotNull PacketMessage message) {
    }
}
