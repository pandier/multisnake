package io.github.pandier.multisnake.network.packet.server;

import io.github.pandier.multisnake.network.packet.message.PacketMessage;
import org.jetbrains.annotations.NotNull;

/**
 * Sent by the server when the game starts.
 */
public record ServerGameStartPacket() implements ServerPacket {

    @Override
    public void write(@NotNull PacketMessage message) {
    }
}
