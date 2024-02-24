package io.github.pandier.multisnake;

import io.github.pandier.multisnake.network.MultisnakeServer;
import io.github.pandier.multisnake.network.NetworkingException;

import java.net.InetSocketAddress;

public class Multisnake {
    private final MultisnakeServer server;

    /**
     * Creates a new multisnake instance.
     * <p>
     * This opens a new server.
     *
     * @throws Exception if an error occurs
     */
    public Multisnake() throws Exception {
        try {
            this.server = MultisnakeServer.open();
        } catch (NetworkingException e) {
            throw new Exception("Failed to open server", e);
        }
    }

    /**
     * Starts the server loop.
     *
     * @throws Exception if an error occurs
     */
    public void start() throws Exception {
        server.start(new InetSocketAddress(35236));
    }
}
