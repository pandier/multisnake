package io.github.pandier.multisnake.player;

import io.github.pandier.multisnake.network.connection.ClientConnection;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a player that has passed the login process
 * and can participate in the game.
 */
public class Player {

    private final ClientConnection connection;
    private final String username;

    public Player(@NotNull ClientConnection connection, @NotNull String username) {
        this.connection = connection;
        this.username = username;
    }

    /**
     * Returns the network connection of this player.
     *
     * @return the connection
     */
    public @NotNull ClientConnection getConnection() {
        return connection;
    }

    /**
     * Returns the username of this player
     * determined in the login process.
     *
     * @return the player username
     */
    public @NotNull String getUsername() {
        return username;
    }
}
