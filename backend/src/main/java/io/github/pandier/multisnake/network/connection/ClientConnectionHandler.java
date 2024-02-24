package io.github.pandier.multisnake.network.connection;

import io.github.pandier.multisnake.network.MultisnakeServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages {@link ClientConnection} instances.
 */
public class ClientConnectionHandler {
    private final MultisnakeServer server;
    private final Map<SocketChannel, ClientConnection> connections = new HashMap<>();

    public ClientConnectionHandler(@NotNull MultisnakeServer server) {
        this.server = server;
    }

    /**
     * Creates a new client connection for a socket channel.
     * If a client connection is already created for the channel,
     * the existing connection is returned.
     *
     * @param channel the client socket channel
     * @return the created client connection of the channel
     */
    public @NotNull ClientConnection create(@NotNull SocketChannel channel) {
        return connections.computeIfAbsent(channel, computeChannel -> new ClientConnection(server, computeChannel, UUID.randomUUID()));
    }

    /**
     * Returns a client connection instance assigned to a socket channel.
     *
     * @param channel the client socket channel
     * @return the client connection of the channel
     */
    public @Nullable ClientConnection get(@NotNull SocketChannel channel) {
        return connections.get(channel);
    }

    /**
     * Removes a client connection of a socket channel.
     *
     * @param channel the client socket channel
     */
    public void remove(@NotNull SocketChannel channel) {
        connections.remove(channel);
    }
}
