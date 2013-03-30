package rickbw.crud.http;

import java.net.URL;

import com.google.common.util.concurrent.ListenableFuture;

import rickbw.crud.ResourceConsumer;
import rickbw.crud.ResourceProvider;


/**
 * An extension of {@link ResourceProvider} for the HTTP protocol, which
 * understands a few more verbs than the standard Create
 * (PUT/{@link #set(URL, Object, Class, ResourceConsumer)}),
 * Read (GET/{@link #get(URL, Class, ResourceConsumer)}),
 * Update (POST/{@link #update(URL, Object, Class, ResourceConsumer)}), and
 * Delete (DELETE/{@link #delete(URL, Class, ResourceConsumer)}).
 *
 * This interface does not support the HTTP CONNECT method, which is not used
 * for acting on or describing resources, but rather for changing the behavior
 * of HTTP proxies.
 */
public interface HttpResourceProvider extends ResourceProvider<URL> {

    public abstract <RESP> ListenableFuture<?> head(
            URL url,
            Class<? extends RESP> responseClass,
            ResourceConsumer<? super URL, RESP> consumer);

    /**
     * FIXME: Doesn't OPTIONS have a standard response form? We shouldn't
     *        need the generic parameter.
     */
    public abstract <RESP> ListenableFuture<?> options(
            URL url,
            Class<? extends RESP> responseClass,
            ResourceConsumer<? super URL, RESP> consumer);

    /**
     * FIXME: TRACE requests have headers only, and the response body contains
     * an echo, so what is the incorrect response type?
     */
    public abstract <RESP> ListenableFuture<?> trace(
            URL url,
            Class<? extends RESP> responseClass,
            ResourceConsumer<? super URL, RESP> consumer);

}
