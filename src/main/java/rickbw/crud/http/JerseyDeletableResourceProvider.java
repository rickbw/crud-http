package rickbw.crud.http;

import java.net.URI;
import java.util.concurrent.ExecutorService;

import rickbw.crud.DeletableResourceProvider;
import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterface;


public final class JerseyDeletableResourceProvider<RESPONSE>
implements DeletableResourceProvider<URI, HttpResponse<RESPONSE>> {

    private final RequestProvider requester;
    private final Class<? extends RESPONSE> responseClass;
    private final ExecutorService executor;


    public JerseyDeletableResourceProvider(
            final Client restClient,
            final Class<? extends RESPONSE> resourceClass,
            final ClientConfiguration config) {
        this.requester = new RequestProvider(restClient, config);
        this.responseClass = Preconditions.checkNotNull(resourceClass);
        this.executor = config.getExecutor();
    }

    @Override
    public JerseyDeletableResource<RESPONSE> get(final URI uri) {
        final UniformInterface resource = this.requester.getResource(uri);
        return new JerseyDeletableResource<RESPONSE>(resource, this.responseClass, this.executor);
    }

}
