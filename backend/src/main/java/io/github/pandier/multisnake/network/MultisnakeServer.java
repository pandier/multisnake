package io.github.pandier.multisnake.network;

import io.github.pandier.multisnake.network.connection.ClientConnection;
import io.github.pandier.multisnake.network.connection.ClientConnectionHandler;
import io.github.pandier.multisnake.network.packet.PacketHandler;
import io.github.pandier.multisnake.network.packet.client.ClientLoginPacket;
import io.github.pandier.multisnake.network.packet.server.ServerErrorPacket;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Accepts and listens to client connections.
 */
public class MultisnakeServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MultisnakeServer.class);

    private final ServerSocketChannel channel;
    private final Selector selector;

    private final PacketHandler packetHandler;
    private final ClientConnectionHandler clientConnectionHandler;

    private final ByteBuffer inputBuffer;

    private MultisnakeServer(ServerSocketChannel channel, Selector selector) {
        this.channel = channel;
        this.selector = selector;

        this.packetHandler = new PacketHandler();
        this.clientConnectionHandler = new ClientConnectionHandler(this);

        this.inputBuffer = ByteBuffer.allocate(256);

        // Register client packets
        packetHandler.registerClientPacket((byte) 0, new ClientLoginPacket.Factory());

        // Register server packets
        packetHandler.registerServerPacket((byte) 0, ServerErrorPacket.class);
    }

    /**
     * Opens a server-socket channel and a selector for a multisnake server.
     *
     * @return the multisnake server
     */
    public static @NotNull MultisnakeServer open() throws NetworkingException {
        ServerSocketChannel socket = NetworkingException.wrap(ServerSocketChannel::open, "Failed to open server socket channel");
        Selector selector = NetworkingException.wrap(Selector::open, "Failed to open selector");

        return new MultisnakeServer(socket, selector);
    }

    /**
     * Starts processing selection keys from the selector until the channel is closed.
     * <p>
     * This method is blocking and only uses one thread.
     * It utilizes {@link Selector} for handling multiple channels.
     *
     * @throws NetworkingException if an error happens
     */
    public void start(@NotNull InetSocketAddress address) throws NetworkingException {
        requireNonNull(address, "Address cannot be null");

        try {
            channel.bind(address);
        } catch (IOException e) {
            throw new NetworkingException("Failed to bind address", e);
        }

        try {
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            throw new NetworkingException("Failed to configure socket", e);
        }

        try (selector; channel) {
            LOGGER.info("Accepting connections on {}:{}", address.getAddress().getHostAddress(), address.getPort());

            while (channel.isOpen()) {
                selector.select();

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();

                while (iterator.hasNext()) {
                    process(iterator.next());
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            throw new NetworkingException("An error occured during server connection loop", e);
        }
    }

    private void process(@NotNull SelectionKey key) throws NetworkingException {
        if (key.channel() == channel) {
            if (key.isAcceptable()) {
                SocketChannel clientChannel = null;
                try {
                    clientChannel = channel.accept();
                    if (clientChannel == null) {
                        LOGGER.warn("Ignoring acceptable selection key, because no connection can be accepted");
                        return;
                    }

                    clientChannel.configureBlocking(false);
                    clientChannel.register(selector, SelectionKey.OP_READ);

                    ClientConnection clientConnection = clientConnectionHandler.create(clientChannel);

                    LOGGER.info("Accepted new connection from {} as {}", clientChannel.getRemoteAddress(), clientConnection.getUuid());
                } catch (IOException e) {
                    LOGGER.error("Failed to accept socket", e);
                    if (clientChannel != null) {
                        try {
                            clientChannel.close();
                        } catch (IOException closeException) {
                            throw new NetworkingException("Failed to close a socket channel", closeException);
                        }
                    }
                }
            }
        } else if (key.channel() instanceof SocketChannel clientChannel) {
            if (key.isReadable()) {
                ClientConnection clientConnection = clientConnectionHandler.get(clientChannel);
                if (clientConnection == null) {
                    try {
                        LOGGER.warn("Client {} does not have an assigned connection instance, closing the connection", clientChannel.getRemoteAddress());
                        clientChannel.close();
                        return;
                    } catch (IOException e) {
                        throw new NetworkingException("Failed to close a socket channel", e);
                    }
                }

                try {
                    inputBuffer.clear();

                    int i = clientChannel.read(inputBuffer);
                    if (i == 0) {
                        return;
                    } else if (i < 0) {
                        clientConnectionHandler.remove(clientChannel);
                        clientChannel.close();
                        key.cancel();
                        LOGGER.info("Closed connection with client {}", clientConnection.getUuid());
                        return;
                    }

                    inputBuffer.flip();
                    packetHandler.process(clientConnection, inputBuffer);
                } catch (IOException | NetworkingException e) {
                    LOGGER.error("Failed to process packet received from client {}", clientConnection.getUuid(), e);
                }
            }
        } else {
            // Cancel unwanted selection keys
            key.cancel();
        }
    }

    /**
     * Returns the {@link PacketHandler} of this server.
     *
     * @return the packet handler
     */
    public @NotNull PacketHandler getPacketHandler() {
        return packetHandler;
    }

    /**
     * Returns the {@link ClientConnectionHandler} of this server.
     *
     * @return the client connection handler
     */
    public ClientConnectionHandler getClientConnectionHandler() {
        return clientConnectionHandler;
    }

    /**
     * Returns the {@link ServerSocketChannel} of this server.
     *
     * @return the socket channel
     */
    public @NotNull ServerSocketChannel getChannel() {
        return channel;
    }
}
