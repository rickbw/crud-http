package rickbw.crud.http;

import java.net.URL;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import rickbw.crud.MapResourceConsumer;
import rickbw.crud.MapResourceProvider;


public final class JerseyMapGetter implements MapResourceProvider<String, ClientResponse> {

    private static final Logger log = LoggerFactory.getLogger(JerseyMapGetter.class);

    private final URL baseUrl;
    private final Client restClient;
    private final MapResourceConsumer<? super String, ? super Exception> exceptionConsumer;


    public JerseyMapGetter(final URL baseUrl, final Client restClient) {
        this(baseUrl, restClient, null);
    }

    public JerseyMapGetter(
            final URL baseUrl,
            final Client restClient,
            @Nullable final MapResourceConsumer<? super String, ? super Exception> exceptionConsumer) {
        this.baseUrl = Preconditions.checkNotNull(baseUrl);
        this.restClient = Preconditions.checkNotNull(restClient);

        if (null != exceptionConsumer) {
            this.exceptionConsumer = exceptionConsumer;
        } else {
            this.exceptionConsumer = new MapResourceConsumer<String, Exception>() {
                @Override
                public void accept(final String key, final Exception value) {
                    log.error("Error occurred while getting " + getFullUrl(key), value);
                }
            };
        }
    }

    @Override
    public void get(
            final String relativeUrl,
            final MapResourceConsumer<? super String, ? super ClientResponse> consumer) {
        Preconditions.checkNotNull(consumer);
        final String fullUrl = getFullUrl(relativeUrl);

        /* Since we don't want to block on a Future, we don't use the
         * AsyncWebResource. However, we do use the client's own
         * ExecutorService.
         */
        final WebResource webResource = this.restClient.resource(fullUrl);
        this.restClient.getExecutorService().submit(new Runnable() {
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
                    try {
                        exceptionConsumer.accept(relativeUrl, rex);
                    } catch (final RuntimeException rex2) {
                        log.error(
                            "New error while processing earlier error " + rex +
                                " while getting " + fullUrl,
                            rex2);
                    }
                }
            }
        });
    }

    private String getFullUrl(final String relativeUrl) {
        if (!StringUtils.isEmpty(relativeUrl)) {
            return this.baseUrl.toExternalForm() + relativeUrl;
        } else {
            return this.baseUrl.toExternalForm();
        }
    }

}
