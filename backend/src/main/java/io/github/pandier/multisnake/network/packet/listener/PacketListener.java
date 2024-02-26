package io.github.pandier.multisnake.network.packet.listener;

import io.github.pandier.multisnake.network.packet.client.ClientLoginPacket;
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
}
