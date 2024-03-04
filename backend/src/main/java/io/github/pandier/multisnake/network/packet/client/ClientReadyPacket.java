package io.github.pandier.multisnake.network.packet.client;

import io.github.pandier.multisnake.network.packet.listener.PacketListener;
import io.github.pandier.multisnake.network.packet.message.InvalidPacketMessageException;
import io.github.pandier.multisnake.network.packet.message.PacketMessage;
import org.jetbrains.annotations.NotNull;

/**
 * Sent by a player to the server when the player
 * is ready for the game. If all players are ready,
 * the game starts.
 */
public record ClientReadyPacket(
        boolean ready
) implements ClientPacket {

    @Override
    public void apply(@NotNull PacketListener listener) {
        listener.onReady(this);
    }

    public static class Factory implements ClientPacketFactory<ClientReadyPacket> {

        @Override
        public @NotNull ClientReadyPacket read(@NotNull PacketMessage message) throws InvalidPacketMessageException {
            boolean ready = message.getBoolean();
            return new ClientReadyPacket(ready);
        }
    }
}
