package io.github.pandier.multisnake.network.packet.server;

import io.github.pandier.multisnake.network.packet.message.PacketMessage;
import org.jetbrains.annotations.NotNull;

public interface ServerPacket {

    void write(@NotNull PacketMessage message);
}
