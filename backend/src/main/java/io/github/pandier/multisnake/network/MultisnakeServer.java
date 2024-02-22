package io.github.pandier.multisnake.network;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Accepts and listens to client connections.
 */
public class MultisnakeServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MultisnakeServer.class);

    private final InetSocketAddress address;
    private final ServerSocketChannel channel;
    private final Selector selector;

    private MultisnakeServer(InetSocketAddress address, ServerSocketChannel channel, Selector selector) {
        this.address = address;
        this.channel = channel;
        this.selector = selector;
    }

    /**
     * Opens a multisnake server and binds it at a specific {@link InetSocketAddress} address.
     *
     * @param address the address to bind to
     * @return the opened multisnake server
     */
    public static @NotNull MultisnakeServer open(@NotNull InetSocketAddress address) throws NetworkingException {
        requireNonNull(address, "Address cannot be null");

        ServerSocketChannel socket = NetworkingException.wrap(ServerSocketChannel::open, "Failed to open server socket channel");
        Selector selector = NetworkingException.wrap(Selector::open, "Failed to open selector");

        try {
            socket.bind(address);
        } catch (IOException e) {
            throw new NetworkingException("Failed to bind address", e);
        }

        try {
            socket.configureBlocking(false);
            socket.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            throw new NetworkingException("Failed to configure socket", e);
        }

        return new MultisnakeServer(address, socket, selector);
    }

    /**
     * Starts processing selection keys from the selector until the channel is closed.
     * <p>
     * This method is blocking and only uses one thread.
     * It utilizes {@link Selector} for handling multiple channels.
     *
     * @throws NetworkingException if an error happens
     */
    public void start() throws NetworkingException {
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

    private void process(@NotNull SelectionKey key) {
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
            // TODO: Read from the channel
        }
    }

    /**
     * Returns the {@link InetSocketAddress} of this server.
     *
     * @return the address
     */
    public InetSocketAddress getAddress() {
        return address;
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
