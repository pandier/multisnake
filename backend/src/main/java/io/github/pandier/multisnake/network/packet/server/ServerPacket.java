package io.github.pandier.multisnake.network.packet.server;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

public interface ServerPacket {

    void write(@NotNull ByteBuffer buffer);
}
