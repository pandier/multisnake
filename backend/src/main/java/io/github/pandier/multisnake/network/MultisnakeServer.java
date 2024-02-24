package io.github.pandier.multisnake.network;

import io.github.pandier.multisnake.network.packet.PacketHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
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

    private final ByteBuffer inputBuffer;

    private MultisnakeServer(ServerSocketChannel channel, Selector selector) {
        this.channel = channel;
        this.selector = selector;

        this.packetHandler = new PacketHandler();

        this.inputBuffer = ByteBuffer.allocate(256);
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
                Iterator<SelectionKey> keyIterator = keys.iterator();

                while (keyIterator.hasNext()) {
                    process(keyIterator.next());
                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            throw new NetworkingException("An error occured during server connection loop", e);
        }
    }

    private void process(@NotNull SelectionKey key) throws NetworkingException {
        if (key.channel() == channel) {
            if (key.isAcceptable()) {
                try {
                    SocketChannel clientChannel = channel.accept();
                    if (clientChannel == null) {
                        LOGGER.warn("Ignoring acceptable selection key, because no connection can be accepted");
                        return;
                    }
                    clientChannel.configureBlocking(false);
                    clientChannel.register(selector, SelectionKey.OP_READ);

                    LOGGER.info("Accepted new connection from {}", clientChannel.getRemoteAddress());
                } catch (IOException e) {
                    LOGGER.error("Failed to accept socket", e);
                }
            }
        } else if (key.isReadable()) {
            if (!(key.channel() instanceof SocketChannel clientChannel))
                return;
            try {
                inputBuffer.clear();
                if (clientChannel.read(inputBuffer) <= 0)
                    return;
                inputBuffer.flip();
                packetHandler.process(clientChannel, inputBuffer);
            } catch (IOException | NetworkingException e) {
                try {
                    LOGGER.error("Failed to process packet received from client {}", clientChannel.getRemoteAddress(), e);
                } catch (IOException ex) {
                    throw new NetworkingException("Failed to get remote address of client", ex);
                }
            }
        }
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
