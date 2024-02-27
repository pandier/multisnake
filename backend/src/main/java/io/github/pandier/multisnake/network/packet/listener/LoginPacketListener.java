package io.github.pandier.multisnake.network.packet.listener;

import io.github.pandier.multisnake.network.NetworkingException;
import io.github.pandier.multisnake.network.connection.ClientConnection;
import io.github.pandier.multisnake.network.packet.client.ClientLoginPacket;
import io.github.pandier.multisnake.network.packet.server.ServerLoginSuccessPacket;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controls the login process of a connection.
 */
// TODO: Add timeout
public class LoginPacketListener implements PacketListener {
    public static final Logger LOGGER = LoggerFactory.getLogger(LoginPacketListener.class);

    private final ClientConnection connection;

    /**
     * Constructs a packet listener for controlling
     * the login process of the given client connection.
     *
     * @param connection the client connection
     */
    public LoginPacketListener(@NotNull ClientConnection connection) {
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
        // TODO: Handle the login process

        try {
            connection.send(new ServerLoginSuccessPacket());
        } catch (NetworkingException e) {
            LOGGER.error("Failed to send login success packet to {}", connection.getUuid(), e);
            connection.disconnect();
        }
    }
}
