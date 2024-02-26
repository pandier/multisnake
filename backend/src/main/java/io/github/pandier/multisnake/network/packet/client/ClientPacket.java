package io.github.pandier.multisnake.network.packet.client;

import io.github.pandier.multisnake.network.packet.Packet;
import io.github.pandier.multisnake.network.packet.listener.PacketListener;
import org.jetbrains.annotations.NotNull;

/**
 * A packet that is sent from a client to the server.
 * <p>
 * When a packet is received, it is applied to a {@link PacketListener}
 * using the {@link #apply(PacketListener) apply} method.
 */
public interface ClientPacket extends Packet {

    /**
     * Applies this packet to a packet listener.
     *
     * @param listener the listener
     */
    void apply(@NotNull PacketListener listener);
}
