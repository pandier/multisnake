package io.github.pandier.multisnake.network;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;

/**
 * An exception that occurs during multisnake networking operations.
 */
public class NetworkingException extends Exception {

    public NetworkingException() {
        super();
    }

    public NetworkingException(String message) {
        super(message);
    }

    public NetworkingException(String message, Throwable cause) {
        super(message, cause);
    }

    public NetworkingException(Throwable cause) {
        super(cause);
    }

    public NetworkingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * Runs a {@link Callable} and if an exception is catched, it is wrapped into a {@link NetworkingException}.
     *
     * @param callable the callable
     * @param message the message of the {@link NetworkingException}
     * @return the result of the callable
     * @param <T> the result type of the callable
     * @throws NetworkingException if the callable throws an exception
     */
    public static <T> T wrap(@NotNull Callable<T> callable, @Nullable String message) throws NetworkingException {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new NetworkingException(message, e);
        }
    }
}
