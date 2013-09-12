package rickbw.crud.http;

import java.net.URI;

import rickbw.crud.WritableResourceProvider;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterface;


public final class JerseyWritableResourceProvider<RESPONSE>
extends AbstractResourceProvider<RESPONSE>
implements WritableResourceProvider<URI, Object, HttpResponse<RESPONSE>> {

    public JerseyWritableResourceProvider(
            final Client restClient,
            final Class<? extends RESPONSE> resourceClass,
            final ClientConfiguration config) {
        super(restClient, resourceClass, config);
    }

    @Override
    public JerseyWritableResource<RESPONSE> get(final URI uri) {
        final UniformInterface resource = getResource(uri);
        return new JerseyWritableResource<RESPONSE>(resource, getResponseClass(), getExecutor());
    }

}
