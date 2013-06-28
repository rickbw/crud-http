package rickbw.crud.http;

import java.net.URI;

import rickbw.crud.sync.SyncMapResourceProvider;
import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterface;
import com.sun.jersey.api.client.UniformInterfaceException;


public final class JerseyResourceProvider<RSRC>
implements SyncMapResourceProvider<URI, HttpResponse<RSRC>> {

    private final RequestProvider requester;
    private final Class<? extends RSRC> resourceClass;


    public JerseyResourceProvider(
            final Client restClient,
            final ClientConfiguration config,
            final Class<? extends RSRC> resourceClass) {
        this.requester = new RequestProvider(restClient, config);
        this.resourceClass = Preconditions.checkNotNull(resourceClass);
    }

    @Override
    public HttpResponse<RSRC> getSync(final URI uri) throws UniformInterfaceIOException, ClientHandlerIOException {
        final UniformInterface webResource = this.requester.getResource(uri);
        try {
            final ClientResponse response = webResource.get(ClientResponse.class);
            return HttpResponse.wrapAndClose(response, this.resourceClass);
        } catch (final UniformInterfaceException uix) {
            throw new UniformInterfaceIOException(uix);
        } catch (final ClientHandlerException chx) {
            throw new ClientHandlerIOException(chx);
        }
    }

}
