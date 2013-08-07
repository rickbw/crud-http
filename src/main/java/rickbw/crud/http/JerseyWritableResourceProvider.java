package rickbw.crud.http;

import java.net.URI;
import java.util.concurrent.ExecutorService;

import rickbw.crud.WritableResourceProvider;
import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterface;


public final class JerseyWritableResourceProvider<RESPONSE>
implements WritableResourceProvider<URI, Object, HttpResponse<RESPONSE>> {

    private final RequestProvider requester;
    private final Class<? extends RESPONSE> resourceClass;
    private final ExecutorService executor;


    public JerseyWritableResourceProvider(
            final Client restClient,
            final Class<? extends RESPONSE> resourceClass,
            final ClientConfiguration config) {
        this.requester = new RequestProvider(restClient, config);
        this.resourceClass = Preconditions.checkNotNull(resourceClass);
        this.executor = restClient.getExecutorService();
    }

    @Override
    public JerseyWritableResource<RESPONSE> get(final URI uri) {
        final UniformInterface resource = this.requester.getResource(uri);
        return new JerseyWritableResource<RESPONSE>(resource, this.resourceClass, this.executor);
    }

}
