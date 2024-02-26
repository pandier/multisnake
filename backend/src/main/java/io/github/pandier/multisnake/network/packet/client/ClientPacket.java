package io.github.pandier.multisnake.network.packet.client;

import io.github.pandier.multisnake.network.packet.Packet;
import io.github.pandier.multisnake.network.packet.listener.PacketListener;
import org.jetbrains.annotations.NotNull;

public interface ClientPacket extends Packet {

    /**
     * Applies this packet to a packet listener.
     *
     * @param listener the listener
     */
    void apply(@NotNull PacketListener listener);
}
