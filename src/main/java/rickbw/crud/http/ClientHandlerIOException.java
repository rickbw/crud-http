package rickbw.crud.http;

import java.io.IOException;

import com.sun.jersey.api.client.ClientHandlerException;


/**
 * A class parallel to {@link ClientHandlerException} that extends
 * {@link IOException}. The new parent class allows both Jersey-aware and
 * Jersey-agnostic clients to respond to the exception in reasonable ways.
 */
public final class ClientHandlerIOException extends IOException {

    private static final long serialVersionUID = -3529239592278034914L;


    public ClientHandlerIOException() {
        super();
    }

    public ClientHandlerIOException(final String message) {
        super(message);
    }

    public ClientHandlerIOException(final Throwable cause) {
        super(cause);
    }

    public ClientHandlerIOException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
