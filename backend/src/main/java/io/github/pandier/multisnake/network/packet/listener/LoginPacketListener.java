package io.github.pandier.multisnake.network.packet.listener;

import io.github.pandier.multisnake.Multisnake;
import io.github.pandier.multisnake.network.NetworkingException;
import io.github.pandier.multisnake.network.connection.ClientConnection;
import io.github.pandier.multisnake.network.packet.client.ClientLoginPacket;
import io.github.pandier.multisnake.network.packet.server.ServerErrorPacket;
import io.github.pandier.multisnake.network.packet.server.ServerLoginSuccessPacket;
import io.github.pandier.multisnake.player.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controls the login process of a connection.
 */
// TODO: Add timeout
public class LoginPacketListener implements PacketListener {
    public static final Logger LOGGER = LoggerFactory.getLogger(LoginPacketListener.class);

    private final Multisnake multisnake;
    private final ClientConnection connection;

    /**
     * Constructs a packet listener for controlling
     * the login process of the given client connection.
     *
     * @param multisnake the {@link Multisnake} instance of the login process
     * @param connection the client connection
     */
    public LoginPacketListener(@NotNull Multisnake multisnake, @NotNull ClientConnection connection) {
        this.multisnake = multisnake;
        this.connection = connection;
    }

    /**
     * Called when the server receives a login packet.
     * <p>
     * Starts the login process of a client.
     *
     * @param packet the login packet
     */
    @Override
    public void onLogin(@NotNull ClientLoginPacket packet) {
        Player player = multisnake.getPlayerManager().create(connection, packet.username());
        if (player == null) {
            try {
                connection.sendError(ServerErrorPacket.Error.USERNAME_TAKEN);
            } catch (NetworkingException e) {
                LOGGER.error("Failed to send username taken error to {}", connection.getUuid(), e);
            }

            connection.disconnect();
            return;
        }

        connection.setPacketListener(new PlayerPacketListener(multisnake, player));

        LOGGER.info("Authenticated '{}' as {}", player.getUsername(), connection.getUuid());

        try {
            connection.send(new ServerLoginSuccessPacket());
        } catch (NetworkingException e) {
            LOGGER.error("Failed to send login success packet to {}", connection.getUuid(), e);
            connection.disconnect();
        }
    }
}
