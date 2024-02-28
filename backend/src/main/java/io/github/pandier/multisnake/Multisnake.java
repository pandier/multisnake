package io.github.pandier.multisnake;

import io.github.pandier.multisnake.network.MultisnakeServer;
import io.github.pandier.multisnake.network.NetworkingException;
import io.github.pandier.multisnake.player.PlayerManager;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;

public class Multisnake {
    private final MultisnakeServer server;

    private final PlayerManager playerManager;

    /**
     * Creates a new multisnake instance.
     * <p>
     * This opens a new server.
     *
     * @throws Exception if an error occurs
     */
    public Multisnake() throws Exception {
        this.playerManager = new PlayerManager();

        try {
            this.server = MultisnakeServer.open(this);
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

    /**
     * Returns the player manager of this multisnake instance.
     *
     * @return the player manager
     */
    public @NotNull PlayerManager getPlayerManager() {
        return playerManager;
    }
}
