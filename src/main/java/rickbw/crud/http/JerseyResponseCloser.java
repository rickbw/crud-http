package rickbw.crud.http;

import rickbw.crud.sync.ResourceCloser;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;


public class JerseyResponseCloser implements ResourceCloser<ClientResponse> {

    @Override
    public final void close(final ClientResponse response) throws ClientHandlerIOException {
        try {
            response.close();
        } catch (final ClientHandlerException chx) {
            throw new ClientHandlerIOException(chx);
        }
    }

}
