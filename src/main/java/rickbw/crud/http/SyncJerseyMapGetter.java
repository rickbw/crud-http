package rickbw.crud.http;

import java.net.URI;

import rickbw.crud.sync.SyncMapResourceProvider;
import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;


public final class SyncJerseyMapGetter
extends JerseyResponseCloser
implements SyncMapResourceProvider<URI, ClientResponse> {

    private final Client restClient;


    public SyncJerseyMapGetter(final Client restClient) {
        this.restClient = Preconditions.checkNotNull(restClient);
    }

    @Override
    public ClientResponse get(final URI uri) throws UniformInterfaceIOException, ClientHandlerIOException {
        final WebResource webResource = this.restClient.resource(uri);
        try {
            final ClientResponse response = webResource.get(ClientResponse.class);
            return response;
        } catch (final UniformInterfaceException uix) {
            throw new UniformInterfaceIOException(uix);
        } catch (final ClientHandlerException chx) {
            throw new ClientHandlerIOException(chx);
        }
    }

}
