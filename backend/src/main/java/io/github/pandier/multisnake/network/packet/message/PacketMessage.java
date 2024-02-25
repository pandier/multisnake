package io.github.pandier.multisnake.network.packet.message;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Represents a message that is sent or received as a packet.
 * <p>
 * This is a wrapper around the {@link ByteBuffer}
 * with utility methods that follow the current protocol specification
 * for easier data reading and writing.
 */
public class PacketMessage {
    private final ByteBuffer buffer;

    public PacketMessage(@NotNull ByteBuffer buffer) {
        this.buffer = buffer;
    }

    /**
     * Reads a byte at the buffer position and increments the position.
     *
     * @return the byte at the current position
     */
    public byte getByte() {
        return buffer.get();
    }

    /**
     * Transfers bytes from the buffer to the given destination array
     * at the current position of the buffer.
     *
     * @param dst the destination array to transfer data to
     */
    public void getBytes(byte[] dst) {
        buffer.get(dst);
    }

    /**
     * Constructs a new byte array with the given length
     * and transfers data into the array at the current position of the buffer.
     *
     * @param length the length of the array
     * @return the constructed array
     */
    public byte[] getBytes(int length) {
        byte[] bytes = new byte[length];
        getBytes(bytes);
        return bytes;
    }

    /**
     * Reads the next four bytes, composing an integer value out of them.
     * The position is incremented by four.
     *
     * @return the composed integer value
     */
    public int getInt() {
        return buffer.getInt();
    }

    /**
     * Reads a string at the current position of the buffer using a UTF-8 charset.
     * The string is prefixed with an integer indicating the size of the string.
     *
     * @return the decoded string
     */
    public @NotNull String getString() {
        return getString(StandardCharsets.UTF_8);
    }

    /**
     * Reads a string using the given charset at the current position of the buffer.
     * The string is prefixed with an integer indicating the size of the string.
     *
     * @param charset the charset to be used to decode the bytes
     * @return the decoded string
     */
    public @NotNull String getString(@NotNull Charset charset) {
        int length = getInt();
        byte[] bytes = getBytes(length);
        return new String(bytes, charset);
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
     * @param s the string to be encoded
     * @param charset the charset to be used in encoding
     */
    public void putString(@NotNull String s, @NotNull Charset charset) {
        byte[] bytes = s.getBytes(charset);
        putInt(bytes.length);
        putBytes(bytes);
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
