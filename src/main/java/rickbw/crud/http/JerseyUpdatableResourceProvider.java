package rickbw.crud.http;

import java.net.URI;

import rickbw.crud.UpdatableResourceProvider;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterface;


public final class JerseyUpdatableResourceProvider<RESPONSE>
extends AbstractResourceProvider<RESPONSE>
implements UpdatableResourceProvider<URI, Object, HttpResponse<RESPONSE>> {

    public JerseyUpdatableResourceProvider(
            final Client restClient,
            final Class<? extends RESPONSE> responseClass,
            final ClientConfiguration config) {
        super(restClient, responseClass, config);
    }

    @Override
    public JerseyUpdatableResource<RESPONSE> get(final URI uri) {
        final UniformInterface resource = getResource(uri);
        return new JerseyUpdatableResource<RESPONSE>(resource, getResponseClass(), getExecutor());
    }

}
