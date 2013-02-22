package rickbw.crud.http;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import rickbw.crud.MapResourceConsumer;
import rickbw.crud.MapResourceProvider;


public final class JerseyMapGetter implements MapResourceProvider<String, ClientResponse> {

    private static final Logger log = LoggerFactory.getLogger(JerseyMapGetter.class);

    private final URL baseUrl;
    private final Client restClient;
    private final ListeningExecutorService executor;


    public JerseyMapGetter(final URL baseUrl, final Client restClient) {
        this(baseUrl, restClient, MoreExecutors.listeningDecorator(restClient.getExecutorService()));
    }

    public JerseyMapGetter(final URL baseUrl, final Client restClient, final ListeningExecutorService executor) {
        this.baseUrl = Preconditions.checkNotNull(baseUrl);
        this.restClient = Preconditions.checkNotNull(restClient);
        this.executor = Preconditions.checkNotNull(executor);
    }

    @Override
    public ListenableFuture<?> get(
            final String relativeUrl,
            final MapResourceConsumer<? super String, ? super ClientResponse> consumer) {
        Preconditions.checkNotNull(consumer);

        final String fullUrl;
        if (null != relativeUrl ) {
            fullUrl = this.baseUrl.toExternalForm() + relativeUrl;
        } else {
            fullUrl = this.baseUrl.toExternalForm();
        }

        final WebResource webResource = this.restClient.resource(fullUrl);

        /* XXX: The Futures returned from AsyncWebResource.get() are not those
         * obtained by calling submit() on its ExecutorService. As a result,
         * even if we force the ExecutorService to be a
         * ListeningExecutorService, we can't listen to the futures.
         * Therefore, we use the synchronous methods, and schedule the tasks
         * ourselves.
         */
        return this.executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    final ClientResponse response = webResource.get(ClientResponse.class);
                    try {
                        consumer.accept(relativeUrl, response);
                    } finally {
                        response.close();
                    }
                } catch (final RuntimeException rex) {
                    // TODO: Pass to some other Consumer?
                    log.error("Error getting " + fullUrl, rex);
                }
            }
        });
    }

}
