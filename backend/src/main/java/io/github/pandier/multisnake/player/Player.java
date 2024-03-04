package io.github.pandier.multisnake.player;

import io.github.pandier.multisnake.network.connection.ClientConnection;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents a player that has passed the login process
 * and can participate in the game.
 */
public class Player {

    private final ClientConnection connection;
    private final String username;

    private boolean ready = false;

    public Player(@NotNull ClientConnection connection, @NotNull String username) {
        this.connection = connection;
        this.username = username;
    }

    /**
     * Changes the ready status to the given boolean value.
     *
     * @param ready the ready status
     */
    public void setReady(boolean ready) {
        this.ready = ready;
    }

    /**
     * Returns true if the player is ready.
     *
     * @return the player's ready status
     */
    public boolean isReady() {
        return ready;
    }

    /**
     * Returns the unique identifier of this player.
     *
     * @return the uuid of player
     */
    public @NotNull UUID getUuid() {
        return getConnection().getUuid();
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
