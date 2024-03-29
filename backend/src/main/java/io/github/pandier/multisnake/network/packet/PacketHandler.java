package io.github.pandier.multisnake.network.packet;

import io.github.pandier.multisnake.network.NetworkingException;
import io.github.pandier.multisnake.network.connection.ClientConnection;
import io.github.pandier.multisnake.network.packet.client.ClientPacket;
import io.github.pandier.multisnake.network.packet.client.ClientPacketFactory;
import io.github.pandier.multisnake.network.packet.message.InvalidPacketMessageException;
import io.github.pandier.multisnake.network.packet.message.PacketMessage;
import io.github.pandier.multisnake.network.packet.server.ServerErrorPacket;
import io.github.pandier.multisnake.network.packet.server.ServerPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Handles processing and writing of packets into and from a byte buffer.
 * <p>
 * Multisnake packets are registered
 * in {@link io.github.pandier.multisnake.network.MultisnakeServer MultisnakeServer} constructor.
 */
public class PacketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PacketHandler.class);

    private final Map<Byte, ClientPacketFactory<?>> clientPacketRegistry = new HashMap<>();
    private final Map<Class<? extends ServerPacket>, Byte> serverPacketRegistry = new HashMap<>();

    /**
     * Registers a new client packet factory with an identifier to this packet handler.
     * If a client packet with the identifier already exists, a {@link IllegalArgumentException} is thrown.
     * <p>
     * The client packet factory is used to construct received packets.
     *
     * @param identifier the identifier of the packet
     * @param factory    the packet factory
     * @throws IllegalArgumentException if a client packet with the identifier is already registered
     */
    public void registerClientPacket(byte identifier, @NotNull ClientPacketFactory<?> factory) throws IllegalArgumentException {
        requireNonNull(factory, "Client packet factory cannot be null");

        if (clientPacketRegistry.get(identifier) != null)
            throw new IllegalArgumentException("A client packet with this identifier is already registered");
        clientPacketRegistry.put(identifier, factory);
    }

    /**
     * Assigns a packet identifier to a server packet.
     *
     * @param identifier the identifier of the packet
     * @param clazz      the server packet class
     */
    public void registerServerPacket(byte identifier, @NotNull Class<? extends ServerPacket> clazz) throws IllegalArgumentException {
        requireNonNull(clazz, "Server packet class cannot be null");
        serverPacketRegistry.put(clazz, identifier);
    }

    /**
     * Returns a client packet factory that is registered
     * in this packet handler with a specific packet identifier.
     *
     * @param identifier the identifier of the packet
     * @return the client packet factory, null if the identifier is not registered
     */
    public @Nullable ClientPacketFactory<?> getClientPacketFactory(byte identifier) {
        return clientPacketRegistry.get(identifier);
    }

    /**
     * Returns an identifier of a server packet that is registered in this packet handler.
     *
     * @param packet the packet
     * @return the packet identifier, null if the packet is not registered
     */
    public @Nullable Byte getServerPacketIdentifier(@NotNull ServerPacket packet) {
        return serverPacketRegistry.get(packet.getClass());
    }

    /**
     * Processes a packet stored in a byte buffer.
     * <p>
     * A {@link ClientPacketFactory} is chosen using a client packet registry defined in this packet handler.
     * If the factory could not be chosen,
     * an {@link ServerErrorPacket.Error#INVALID_PACKET_IDENTIFIER INVALID_PACKET_IDENTIFIER} error is sent.
     * Then a {@link ClientPacket} is constructed using the packet factory and processed by its needs.
     *
     * @param clientConnection the sender of the packet
     * @param buffer           the packet data
     * @throws NetworkingException if an error occurs
     */
    public void process(@NotNull ClientConnection clientConnection, @NotNull ByteBuffer buffer) throws NetworkingException {
        byte identifier = buffer.get();
        ClientPacketFactory<?> factory = getClientPacketFactory(identifier);
        if (factory == null) {
            LOGGER.info("Received invalid packet identifier '{}' from client {}", identifier, clientConnection.getUuid());
            clientConnection.sendError(ServerErrorPacket.Error.INVALID_PACKET_IDENTIFIER);
            return;
        }

        try {
            ClientPacket packet = factory.read(new PacketMessage(buffer));
            packet.apply(clientConnection.getPacketListener());
            LOGGER.debug("Received packet with identifier '{}' from client {}", identifier, clientConnection.getUuid());
        } catch (InvalidPacketMessageException e) {
            LOGGER.info("Invalid packet message with identifier '{}' from client {} ({})", identifier, clientConnection.getUuid(), e.getMessage());
        }
    }

    /**
     * Writes packet data to a byte buffer based on the protocol specification.
     * The byte buffer is then ready to be sent to the client.
     * <p>
     * The packet identifier is determined using a server packet registry
     * defined in this packet handler. If the packet is not known
     * in the registry, an {@link IllegalArgumentException} is thrown.
     *
     * @param buffer the byte buffer
     * @param packet the packet to write
     * @throws IllegalArgumentException if the packet is not registered in this handler
     */
    public void write(@NotNull ByteBuffer buffer, @NotNull ServerPacket packet) throws IllegalArgumentException {
        Byte identifier = getServerPacketIdentifier(packet);
        if (identifier == null)
            throw new IllegalArgumentException("Server packet not registered");
        buffer.put(identifier);
        packet.write(new PacketMessage(buffer));
    }
}
