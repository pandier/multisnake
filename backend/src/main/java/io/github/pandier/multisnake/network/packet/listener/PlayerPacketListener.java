package io.github.pandier.multisnake.network.packet.listener;

import io.github.pandier.multisnake.Multisnake;
import io.github.pandier.multisnake.network.packet.client.ClientReadyPacket;
import io.github.pandier.multisnake.player.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerPacketListener implements PacketListener {

    private final Multisnake multisnake;
    private final Player player;

    public PlayerPacketListener(Multisnake multisnake, Player player) {
        this.multisnake = multisnake;
        this.player = player;
    }

    @Override
    public void onReady(@NotNull ClientReadyPacket packet) {
        player.setReady(packet.ready());
    }

    @Override
    public void handleDisconnect() {
        multisnake.getPlayerManager().remove(player);
    }
}
