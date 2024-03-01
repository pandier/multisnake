package io.github.pandier.multisnake.network.packet.message;

import org.jetbrains.annotations.NotNull;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Represents a message that is sent or received as a packet.
 * <p>
 * This is a wrapper around the {@link ByteBuffer}
 * with utility methods that follow the current protocol specification,
 * and error handling for easier data reading and writing.
 */
@SuppressWarnings("unused")
public class PacketMessage {
    private final ByteBuffer buffer;

    public PacketMessage(@NotNull ByteBuffer buffer) {
        this.buffer = buffer;
    }

    /**
     * Reads a byte at the buffer position and increments the position.
     *
     * @return the byte at the current position
     * @throws InvalidPacketMessageException if the current position is not smaller than the limit
     */
    public byte getByte() throws InvalidPacketMessageException {
        try {
            return buffer.get();
        } catch (BufferUnderflowException e) {
            throw new InvalidPacketMessageException("Expected byte at position " + buffer.position() + " but found end of buffer instead");
        }
    }

    /**
     * Transfers bytes from the buffer to the given destination array
     * at the current position of the buffer.
     *
     * @param dst the destination array to transfer data to
     * @throws InvalidPacketMessageException if there aren't enough bytes remaining
     */
    public void getBytes(byte[] dst) throws InvalidPacketMessageException {
        try {
            buffer.get(dst);
        } catch (BufferUnderflowException e) {
            throw new InvalidPacketMessageException("Expected byte array at position " + buffer.position() + " but found end of buffer instead");
        }
    }

    /**
     * Constructs a new byte array with the given length
     * and transfers data into the array at the current position of the buffer.
     *
     * @param length the length of the array
     * @return the constructed array
     * @throws InvalidPacketMessageException if there aren't enough bytes remaining
     */
    public byte[] getBytes(int length) throws InvalidPacketMessageException {
        byte[] bytes = new byte[length];
        getBytes(bytes);
        return bytes;
    }

    /**
     * Reads the next four bytes, composing an integer value out of them.
     * The position is incremented by four.
     *
     * @return the composed integer value
     * @throws InvalidPacketMessageException if there aren't enough bytes remaining
     */
    public int getInt() throws InvalidPacketMessageException {
        try {
            return buffer.getInt();
        } catch (BufferUnderflowException e) {
            throw new InvalidPacketMessageException("Expected integer at position " + buffer.position() + " but found end of buffer instead");
        }
    }

    /**
     * Reads the next eight bytes, composing a long value out of them.
     * The position is incremented by eight.
     *
     * @return the composed long value
     * @throws InvalidPacketMessageException if there aren't enough bytes remaining
     */
    public long getLong() throws InvalidPacketMessageException {
        try {
            return buffer.getLong();
        } catch (BufferUnderflowException e) {
            throw new InvalidPacketMessageException("Expected long at position " + buffer.position() + " but found end of buffer instead");
        }
    }

    /**
     * Reads a string at the current position of the buffer using a UTF-8 charset.
     * The string is prefixed with an integer indicating the size of the string.
     *
     * @return the decoded string
     * @throws InvalidPacketMessageException if there aren't enough bytes remaining
     */
    public @NotNull String getString() throws InvalidPacketMessageException {
        return getString(StandardCharsets.UTF_8);
    }

    /**
     * Reads a string using the given charset at the current position of the buffer.
     * The string is prefixed with an integer indicating the size of the string.
     *
     * @param charset the charset to be used to decode the bytes
     * @return the decoded string
     * @throws InvalidPacketMessageException if there aren't enough bytes remaining
     */
    public @NotNull String getString(@NotNull Charset charset) throws InvalidPacketMessageException {
        int previousPosition = buffer.position();
        try {
            int length = getInt();
            byte[] bytes = getBytes(length);
            return new String(bytes, charset);
        } catch (InvalidPacketMessageException e) {
            throw new InvalidPacketMessageException("Expected string at position " + previousPosition + " but found end of buffer instead");
        }
    }

    /**
     * Reads the next eight bytes as most significant bits
     * and another eight bytes as least significant bits.
     * A unique identifier value is constructed out of them.
     * The position is incremented by sixteen.
     *
     * @return the constructed unique identifier
     * @throws InvalidPacketMessageException if there aren't enough bits
     */
    public @NotNull UUID getUuid() throws InvalidPacketMessageException {
        int previousPosition = buffer.position();
        try {
            long mostSigBits = getLong();
            long leastSigBits = getLong();
            return new UUID(mostSigBits, leastSigBits);
        } catch (InvalidPacketMessageException e) {
            throw new InvalidPacketMessageException("Expected uuid at position " + previousPosition + " but found end of buffer instead");
        }
    }

    /**
     * Writes the given byte into the buffer at the current position and increments the position.
     *
     * @param b the byte to be written
     */
    public void putByte(byte b) {
        buffer.put(b);
    }

    /**
     * Transfers the entire content of the source array into the buffer at the current position
     * and increments the position by the amount of bytes that were transfered.
     *
     * @param src the source array
     */
    public void putBytes(byte[] src) {
        buffer.put(src);
    }

    /**
     * Writes four bytes containing the given integer value into the buffer
     * at the current position and increments the position by four.
     *
     * @param i the integer value
     */
    public void putInt(int i) {
        buffer.putInt(i);
    }

    /**
     * Writes eight bytes containing the given long value into the buffer
     * at the current position and increments the position by eight.
     *
     * @param l the long value
     */
    public void putLong(long l) {
        buffer.putLong(l);
    }

    /**
     * Encodes and writes the given string with a UTF-8 charset
     * into the buffer at the current position.
     * The string is prefixed with an integer value containg
     * the amount of bytes in the encoded string.
     *
     * @param s the string to be encoded
     */
    public void putString(@NotNull String s) {
        putString(s, StandardCharsets.UTF_8);
    }

    /**
     * Encodes and writes the given string with the given charset
     * into the buffer at the current position.
     * The string is prefixed with an integer value containg
     * the amount of bytes in the encoded string.
     *
     * @param s       the string to be encoded
     * @param charset the charset to be used in encoding
     */
    public void putString(@NotNull String s, @NotNull Charset charset) {
        byte[] bytes = s.getBytes(charset);
        putInt(bytes.length);
        putBytes(bytes);
    }

    /**
     * Writes eight bytes as the most significant bits of the given unique identifier
     * and another eight bytes as the least significant bits.
     *
     * @param uuid the unique identifier
     */
    public void putUuid(@NotNull UUID uuid) {
        putLong(uuid.getMostSignificantBits());
        putLong(uuid.getLeastSignificantBits());
    }

    /**
     * Returns the internal byte buffer of this message.
     *
     * @return the byte buffer
     */
    public @NotNull ByteBuffer getBuffer() {
        return buffer;
    }
}
