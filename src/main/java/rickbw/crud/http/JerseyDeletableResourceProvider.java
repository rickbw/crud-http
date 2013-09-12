package rickbw.crud.http;

import java.net.URI;

import rickbw.crud.DeletableResourceProvider;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterface;


public final class JerseyDeletableResourceProvider<RESPONSE>
extends AbstractResourceProvider<RESPONSE>
implements DeletableResourceProvider<URI, HttpResponse<RESPONSE>> {

    public JerseyDeletableResourceProvider(
            final Client restClient,
            final Class<? extends RESPONSE> resourceClass,
            final ClientConfiguration config) {
        super(restClient, resourceClass, config);
    }

    @Override
    public JerseyDeletableResource<RESPONSE> get(final URI uri) {
        final UniformInterface resource = getResource(uri);
        return new JerseyDeletableResource<RESPONSE>(resource, getResponseClass(), getExecutor());
    }

}
