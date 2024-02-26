package io.github.pandier.multisnake.network.packet.client;

import io.github.pandier.multisnake.network.packet.listener.PacketListener;
import io.github.pandier.multisnake.network.packet.message.InvalidPacketMessageException;
import io.github.pandier.multisnake.network.packet.message.PacketMessage;
import org.jetbrains.annotations.NotNull;

public record ClientLoginPacket(
        @NotNull String username
) implements ClientPacket {

    @Override
    public void apply(@NotNull PacketListener listener) {
        listener.onLogin(this);
    }

    public static class Factory implements ClientPacketFactory<ClientLoginPacket> {
        @Override
        public @NotNull ClientLoginPacket read(@NotNull PacketMessage message) throws InvalidPacketMessageException {
            String username = message.getString();
            return new ClientLoginPacket(username);
        }
    }
}
