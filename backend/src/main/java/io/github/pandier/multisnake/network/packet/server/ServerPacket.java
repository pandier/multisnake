package io.github.pandier.multisnake.network.packet.server;

import io.github.pandier.multisnake.network.packet.message.PacketMessage;
import org.jetbrains.annotations.NotNull;

/**
 * A packet that is sent by the server to a client.
 */
public interface ServerPacket {

    /**
     * Writes data of the packet into a {@link PacketMessage}.
     *
     * @param message the message to be written to
     */
    void write(@NotNull PacketMessage message);
}
