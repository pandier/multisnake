package io.github.pandier.multisnake.network.packet.client;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

@FunctionalInterface
public interface ClientPacketFactory<T extends ClientPacket> {

    @NotNull T read(@NotNull ByteBuffer buffer);
}
