package rickbw.crud.http;

import java.net.URI;

import rickbw.crud.sync.SyncMapResourceUpdater;
import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterface;
import com.sun.jersey.api.client.UniformInterfaceException;


public final class JerseyResourceUpdater<RESPONSE>
implements SyncMapResourceUpdater<URI, Object, HttpResponse<RESPONSE>> {

    private final RequestProvider requester;
    private final Class<? extends RESPONSE> responseClass;


    public JerseyResourceUpdater(
            final Client restClient,
            final ClientConfiguration config,
            final Class<? extends RESPONSE> resourceClass) {
        this.requester = new RequestProvider(restClient, config);
        this.responseClass = Preconditions.checkNotNull(resourceClass);
    }

    @Override
    public HttpResponse<RESPONSE> updateSync(final URI uri, final Object update) throws UniformInterfaceIOException, ClientHandlerIOException {
        final UniformInterface webResource = this.requester.getResource(uri);
        try {
            final ClientResponse response = webResource.post(ClientResponse.class, update);
            return HttpResponse.wrapAndClose(response, this.responseClass);
        } catch (final UniformInterfaceException uix) {
            throw new UniformInterfaceIOException(uix);
        } catch (final ClientHandlerException chx) {
            throw new ClientHandlerIOException(chx);
        }
    }

}
