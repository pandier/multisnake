package io.github.pandier.multisnake.network.packet.message;

/**
 * An exception that occurs when a message of a packet
 * could not be decoded properly.
 */
@SuppressWarnings("unused")
public class InvalidPacketMessageException extends Exception {

    public InvalidPacketMessageException() {
    }

    public InvalidPacketMessageException(String message) {
        super(message);
    }

    public InvalidPacketMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidPacketMessageException(Throwable cause) {
        super(cause);
    }

    public InvalidPacketMessageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
