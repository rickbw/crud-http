package rickbw.crud.http;

import java.net.URI;

import rickbw.crud.sync.SyncUpdatableResourceProvider;
import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterface;


public final class JerseyUpdatableResourceProvider<RESPONSE>
implements SyncUpdatableResourceProvider<URI, Object, HttpResponse<RESPONSE>> {

    private final RequestProvider requester;
    private final Class<? extends RESPONSE> resourceClass;


    public JerseyUpdatableResourceProvider(
            final Client restClient,
            final Class<? extends RESPONSE> resourceClass,
            final ClientConfiguration config) {
        this.requester = new RequestProvider(restClient, config);
        this.resourceClass = Preconditions.checkNotNull(resourceClass);
    }

    @Override
    public JerseyUpdatableResource<RESPONSE> get(final URI uri) {
        final UniformInterface resource = this.requester.getResource(uri);
        return new JerseyUpdatableResource<RESPONSE>(resource, this.resourceClass);
    }

}
