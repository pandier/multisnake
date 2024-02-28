package io.github.pandier.multisnake.network.packet.server;

import io.github.pandier.multisnake.network.packet.message.PacketMessage;
import org.jetbrains.annotations.NotNull;

/**
 * Sent by the server to a client when an error happens.
 *
 * @param error the error
 */
public record ServerErrorPacket(
        @NotNull Error error
) implements ServerPacket {

    @Override
    public void write(@NotNull PacketMessage message) {
        message.putByte(error.getCode());
    }

    /**
     * An error that can either be caused client-side or server-side.
     * Every error has a code that is sent to a client in a {@link ServerErrorPacket}.
     * To get the code use {@link #getCode()}.
     */
    public enum Error {

        /**
         * Caused when the client sents an invalid packet identifier.
         */
        INVALID_PACKET_IDENTIFIER((byte) 0x00),

        /**
         * Caused when the client tries to log in with a username that is already connected.
         */
        USERNAME_TAKEN((byte) 0x01);

        private final byte code;

        Error(byte code) {
            this.code = code;
        }

        /**
         * Returns the error code that is sent to the client in a {@link ServerErrorPacket}.
         *
         * @return the code of the error
         */
        public byte getCode() {
            return code;
        }
    }
}
