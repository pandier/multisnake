package io.github.pandier.multisnake.network.packet.client;

import io.github.pandier.multisnake.network.packet.message.InvalidPacketMessageException;
import io.github.pandier.multisnake.network.packet.message.PacketMessage;
import org.jetbrains.annotations.NotNull;

public interface ClientPacketFactory<T extends ClientPacket> {

    /**
     * Reads a packet message and constructs a packet object out of it.
     *
     * @param message the packet message
     * @return the constructed packet object
     * @throws InvalidPacketMessageException if the packet message is invalid
     */
    @NotNull T read(@NotNull PacketMessage message) throws InvalidPacketMessageException;
}
