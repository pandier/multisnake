package io.github.pandier.multisnake.network.packet.listener;

import io.github.pandier.multisnake.network.packet.client.ClientLoginPacket;
import io.github.pandier.multisnake.network.packet.client.ClientReadyPacket;
import org.jetbrains.annotations.NotNull;

/**
 * An interface that listens to packets and handles them.
 */
public interface PacketListener {

    /**
     * A packet listener that ignores every packet it receives.
     */
    PacketListener IGNORE = new PacketListener() {
    };

    /**
     * Called when the server receives a login packet.
     *
     * @param packet the login packet
     */
    default void onLogin(@NotNull ClientLoginPacket packet) {
    }

    /**
     * Called when the server receives a ready packet.
     *
     * @param packet the ready packet
     */
    default void onReady(ClientReadyPacket packet) {
    }

    /**
     * Called when a client disconnects from the server.
     */
    default void handleDisconnect() {
    }
}
