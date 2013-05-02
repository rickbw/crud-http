package rickbw.crud.http;

import java.io.IOException;

import com.sun.jersey.api.client.UniformInterfaceException;


/**
 * A class parallel to {@link UniformInterfaceException} that extends
 * {@link IOException}. The new parent class allows both Jersey-aware and
 * Jersey-agnostic clients to respond to the exception in reasonable ways.
 */
public final class UniformInterfaceIOException extends IOException {

    private static final long serialVersionUID = -6982024020382951365L;


    public UniformInterfaceIOException() {
        super();
    }

    public UniformInterfaceIOException(final String message) {
        super(message);
    }

    public UniformInterfaceIOException(final Throwable cause) {
        super(cause);
    }

    public UniformInterfaceIOException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
