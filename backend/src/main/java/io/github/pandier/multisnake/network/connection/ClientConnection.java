package io.github.pandier.multisnake.network.connection;

import io.github.pandier.multisnake.network.MultisnakeServer;
import io.github.pandier.multisnake.network.NetworkingException;
import io.github.pandier.multisnake.network.packet.listener.PacketListener;
import io.github.pandier.multisnake.network.packet.server.ServerErrorPacket;
import io.github.pandier.multisnake.network.packet.server.ServerPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    private PacketListener packetListener;

    public ClientConnection(@NotNull MultisnakeServer server, @NotNull SocketChannel channel, @NotNull UUID uuid) {
        this.server = server;
        this.channel = channel;
        this.uuid = uuid;

        this.outputBuffer = ByteBuffer.allocate(256);

        this.packetListener = PacketListener.IGNORE;
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
     * Sents a {@link ServerErrorPacket} with the given error to the client.
     *
     * @param error the error
     * @throws NetworkingException if an error occurs
     */
    public void sendError(@NotNull ServerErrorPacket.Error error) throws NetworkingException {
        send(new ServerErrorPacket(error));
    }

    /**
     * Disconnects the client from the server.
     *
     * @throws NetworkingException if an error occurs
     */
    public void disconnect() throws NetworkingException {
        try {
            channel.close();
        } catch (IOException e) {
            throw new NetworkingException("Failed to close client connection", e);
        }
    }

    /**
     * Returns the packet listener that listens to packets sent by this connection.
     *
     * @return the packet listener
     */
    public @NotNull PacketListener getPacketListener() {
        return packetListener;
    }

    /**
     * Changes the packet listener that listens to packets sent by this connection.
     *
     * @param packetListener the packet listener, {@link PacketListener#IGNORE} is used when null
     */
    public void setPacketListener(@Nullable PacketListener packetListener) {
        this.packetListener = packetListener != null ? packetListener : PacketListener.IGNORE;
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
