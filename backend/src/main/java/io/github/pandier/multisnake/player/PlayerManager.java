package io.github.pandier.multisnake.player;

import io.github.pandier.multisnake.network.connection.ClientConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Manages {@link Player} instances.
 */
public class PlayerManager {
    private final List<Player> players = new ArrayList<>();

    /**
     * Creates a new player instance with the given connection and the given username.
     * If a player with the username already exists, null is returned.
     *
     * @param connection the connection of the player
     * @param username the username of the player
     * @return the new player instance, null if a player with the given username already exists
     */
    public @Nullable Player create(@NotNull ClientConnection connection, @NotNull String username) {
        requireNonNull(username, "Username cannot be null");

        if (getPlayer(username).isPresent())
            return null;

        Player player = new Player(connection, username);
        players.add(player);
        return player;
    }

    /**
     * Removes the given player.
     *
     * @param player the player that will be removed
     * @return true if the player existed in the manager
     */
    public boolean remove(@Nullable Player player) {
        return players.remove(player);
    }

    /**
     * Finds a player with the given connection.
     *
     * @param connection the connection of the player
     * @return an optional describing the found player, empty if not found
     */
    public @NotNull Optional<Player> getPlayer(@Nullable ClientConnection connection) {
        return players.stream()
                .filter(player -> player.getConnection().equals(connection))
                .findFirst();
    }

    /**
     * Finds a player with the given username.
     *
     * @param username the username of the player
     * @return an optional describing the found player, empty if not found
     */
    public @NotNull Optional<Player> getPlayer(@Nullable String username) {
        return players.stream()
                .filter(player -> player.getUsername().equals(username))
                .findFirst();
    }

    /**
     * Returns all players managed in this manager.
     *
     * @return list of all players
     */
    public @NotNull List<Player> getPlayers() {
        return players;
    }
}
