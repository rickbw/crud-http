package rickbw.crud.http;

import java.net.URI;

import rickbw.crud.sync.SyncReadableResourceProvider;
import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterface;


public final class JerseyReadableResourceProvider<RSRC>
implements SyncReadableResourceProvider<URI, HttpResponse<RSRC>> {

    private final RequestProvider requester;
    private final Class<? extends RSRC> resourceClass;


    public JerseyReadableResourceProvider(
            final Client restClient,
            final Class<? extends RSRC> resourceClass,
            final ClientConfiguration config) {
        this.requester = new RequestProvider(restClient, config);
        this.resourceClass = Preconditions.checkNotNull(resourceClass);
    }

    @Override
    public JerseyReadableResource<RSRC> get(final URI uri) {
        final UniformInterface resource = this.requester.getResource(uri);
        return new JerseyReadableResource<RSRC>(resource, this.resourceClass);
    }

}
