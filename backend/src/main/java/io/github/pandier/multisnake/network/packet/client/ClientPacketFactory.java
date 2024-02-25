package io.github.pandier.multisnake.network.packet.client;

import io.github.pandier.multisnake.network.packet.message.PacketMessage;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ClientPacketFactory<T extends ClientPacket> {

    @NotNull T read(@NotNull PacketMessage message);
}
