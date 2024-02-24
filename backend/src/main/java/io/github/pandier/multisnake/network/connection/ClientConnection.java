package io.github.pandier.multisnake.network.connection;

import io.github.pandier.multisnake.network.MultisnakeServer;
import io.github.pandier.multisnake.network.NetworkingException;
import io.github.pandier.multisnake.network.packet.server.ServerPacket;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.UUID;

/**
 * Represents a connection with a client.
 */
public class ClientConnection {
    private final MultisnakeServer server;
    private final SocketChannel channel;
    private final UUID uuid;

    private final ByteBuffer outputBuffer;

    public ClientConnection(@NotNull MultisnakeServer server, @NotNull SocketChannel channel, @NotNull UUID uuid) {
        this.server = server;
        this.channel = channel;
        this.uuid = uuid;

        this.outputBuffer = ByteBuffer.allocate(256);
    }

    /**
     * Sents a packet to the client.
     *
     * @param packet the packet
     * @throws IllegalArgumentException if the server packet is not registered in the packet handler of the multisnake server
     * @throws NetworkingException      if an error occurs
     */
    public void send(@NotNull ServerPacket packet) throws IllegalArgumentException, NetworkingException {
        outputBuffer.clear();
        server.getPacketHandler().write(outputBuffer, packet);
        outputBuffer.flip();
        try {
            channel.write(outputBuffer);
        } catch (IOException e) {
            throw new NetworkingException("Failed to write to a socket channel", e);
        }
    }

    /**
     * Returns the multisnake server of this connection.
     *
     * @return the multisnake server
     */
    public @NotNull MultisnakeServer getServer() {
        return server;
    }

    /**
     * Returns the socket channel of this connection.
     *
     * @return the socket channel
     */
    public @NotNull SocketChannel getChannel() {
        return channel;
    }

    /**
     * Returns the uuid of this connection.
     *
     * @return the uuid
     */
    public @NotNull UUID getUuid() {
        return uuid;
    }
}
