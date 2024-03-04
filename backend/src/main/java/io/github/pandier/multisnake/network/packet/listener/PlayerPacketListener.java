package io.github.pandier.multisnake.network.packet.listener;

import io.github.pandier.multisnake.Multisnake;
import io.github.pandier.multisnake.network.packet.client.ClientReadyPacket;
import io.github.pandier.multisnake.player.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles packets of a player.
 */
public class PlayerPacketListener implements PacketListener {
    public static final Logger LOGGER = LoggerFactory.getLogger(PlayerPacketListener.class);

    private final Multisnake multisnake;
    private final Player player;

    public PlayerPacketListener(Multisnake multisnake, Player player) {
        this.multisnake = multisnake;
        this.player = player;
    }

    @Override
    public void onReady(@NotNull ClientReadyPacket packet) {
        player.setReady(packet.ready());

        LOGGER.info("Player {} has set ready to {}", player.getUuid(), packet.ready());

        if (packet.ready()) {
            multisnake.startGame(false);
        }
    }

    @Override
    public void handleDisconnect() {
        multisnake.getPlayerManager().remove(player);
    }
}
