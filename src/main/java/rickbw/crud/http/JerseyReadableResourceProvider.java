package rickbw.crud.http;

import java.net.URI;
import java.util.concurrent.ExecutorService;

import rickbw.crud.ReadableResourceProvider;
import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterface;


public final class JerseyReadableResourceProvider<RSRC>
implements ReadableResourceProvider<URI, HttpResponse<RSRC>> {

    private final RequestProvider requester;
    private final Class<? extends RSRC> resourceClass;
    private final ExecutorService executor;


    public JerseyReadableResourceProvider(
            final Client restClient,
            final Class<? extends RSRC> resourceClass,
            final ClientConfiguration config) {
        this.requester = new RequestProvider(restClient, config);
        this.resourceClass = Preconditions.checkNotNull(resourceClass);
        this.executor = config.getExecutor();
    }

    @Override
    public JerseyReadableResource<RSRC> get(final URI uri) {
        final UniformInterface resource = this.requester.getResource(uri);
        return new JerseyReadableResource<RSRC>(resource, this.resourceClass, this.executor);
    }

}
