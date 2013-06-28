package rickbw.crud.http;

import java.net.URI;

import rickbw.crud.sync.SyncWritableResourceProvider;
import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterface;


public final class JerseyWritableResourceProvider<RESPONSE>
implements SyncWritableResourceProvider<URI, Object, HttpResponse<RESPONSE>> {

    private final RequestProvider requester;
    private final Class<? extends RESPONSE> resourceClass;


    public JerseyWritableResourceProvider(
            final Client restClient,
            final Class<? extends RESPONSE> resourceClass,
            final ClientConfiguration config) {
        this.requester = new RequestProvider(restClient, config);
        this.resourceClass = Preconditions.checkNotNull(resourceClass);
    }

    @Override
    public JerseyWritableResource<RESPONSE> get(final URI uri) {
        final UniformInterface resource = this.requester.getResource(uri);
        return new JerseyWritableResource<RESPONSE>(resource, this.resourceClass);
    }

}
