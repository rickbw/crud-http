package rickbw.crud.http;

import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

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
    private final Executor consumerExecutor;


    public JerseyMapGetter(final URL baseUrl, final Client restClient, final Executor consumerExecutor) {
        this(baseUrl, restClient, consumerExecutor, null);
    }

    /**
     * @param baseUrl           A URL relative to which those paths passed to
     *        {@link #get(String, MapResourceConsumer)} will be evaluated.
     * @param restClient        HTTP requests will be dispatched using this
     *        client and its {@link ExecutorService}.
     * @param consumerExecutor  {@link MapResourceConsumer}s provided to
     *        {@link #get(String, MapResourceConsumer)} will be executed on
     *        this {@link ExecutorService}.
     * @param exceptionConsumer Exceptions that occur along the way will be
     *        dispatched to this {@link MapResourceConsumer} using the same
     *        relative path passed to
     *        {@link #get(String, MapResourceConsumer)}. Exceptions that occur
     *        while dispatching to this consumer will be logged; they will not
     *        be dispatched recursively.
     */
    public JerseyMapGetter(
            final URL baseUrl,
            final Client restClient,
            final Executor consumerExecutor,
            @Nullable final MapResourceConsumer<? super String, ? super Exception> exceptionConsumer) {
        this.baseUrl = Preconditions.checkNotNull(baseUrl);
        this.restClient = Preconditions.checkNotNull(restClient);
        this.consumerExecutor = Preconditions.checkNotNull(consumerExecutor);

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
        assert null != this.exceptionConsumer;
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
                    consumerExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                consumer.accept(relativeUrl, response);
                            } catch (final RuntimeException rex) {
                                handleException(rex, relativeUrl, fullUrl);
                            } finally {
                                response.close();
                            }
                        }
                    });
                } catch (final RuntimeException rex) {
                    handleException(rex, relativeUrl, fullUrl);
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

    private void handleException(
            final Exception exception,
            final String relativeUrl, final String fullUrl) {
        try {
            this.consumerExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        exceptionConsumer.accept(relativeUrl, exception);
                    } catch (final RuntimeException rex) {
                        log.error("Error in exception consumer for " + exception,
                                  rex);
                    }
                }
            });
        } catch (final RuntimeException rex) {
            log.error("Error dispatching exception consumer for " + exception +
                        " while getting " + fullUrl,
                      rex);
        }
    }

}
