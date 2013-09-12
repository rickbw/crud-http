package rickbw.crud.http;

import java.net.URI;

import rickbw.crud.ReadableResourceProvider;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterface;


public final class JerseyReadableResourceProvider<RSRC>
extends AbstractResourceProvider<RSRC>
implements ReadableResourceProvider<URI, HttpResponse<RSRC>> {

    public JerseyReadableResourceProvider(
            final Client restClient,
            final Class<? extends RSRC> resourceClass,
            final ClientConfiguration config) {
        super(restClient, resourceClass, config);
    }

    @Override
    public JerseyReadableResource<RSRC> get(final URI uri) {
        final UniformInterface resource = getResource(uri);
        return new JerseyReadableResource<RSRC>(resource, getResponseClass(), getExecutor());
    }

}
