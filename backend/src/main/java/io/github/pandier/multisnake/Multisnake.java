package io.github.pandier.multisnake;

import io.github.pandier.multisnake.network.MultisnakeServer;
import io.github.pandier.multisnake.network.NetworkingException;
import io.github.pandier.multisnake.network.packet.server.ServerGameStartPacket;
import io.github.pandier.multisnake.player.Player;
import io.github.pandier.multisnake.player.PlayerManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

public class Multisnake {
    public static final Logger LOGGER = LoggerFactory.getLogger(Multisnake.class);

    private final MultisnakeServer server;

    private final PlayerManager playerManager;

    private boolean runningGame = false;

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
     * Starts the game.
     * If force is false, it first checks if the game can start.
     * If the game cannot start, false is returned.
     *
     * @param force true if checks should be ignored
     * @return true if the game started successfully
     * @see #canStartGame()
     */
    public boolean startGame(boolean force) {
        if (!force && !canStartGame())
            return false;

        LOGGER.info("Starting the game");

        runningGame = true;

        for (Player player : getPlayers()) {
            try {
                player.getConnection().send(new ServerGameStartPacket());
            } catch (NetworkingException e) {
                LOGGER.error("Failed to sent game start packet to {}", player.getUuid(), e);
                player.getConnection().disconnect();
            }
        }
        return true;
    }

    /**
     * Returns true if a game isn't already running,
     * if there are at least two players connected and if everyone is ready.
     *
     * @return true if the game can start
     */
    public boolean canStartGame() {
        return !runningGame && getPlayers().size() > 1 && getPlayers()
                .stream()
                .filter(player -> !player.isReady())
                .findFirst()
                .isEmpty();
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
     * Returns all players connected to this server.
     *
     * @return list of connected players
     */
    public @NotNull List<Player> getPlayers() {
        return playerManager.getPlayers();
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
