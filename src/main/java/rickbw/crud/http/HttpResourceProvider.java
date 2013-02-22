package rickbw.crud.http;

import java.net.URL;

import com.google.common.util.concurrent.ListenableFuture;

import rickbw.crud.ResourceConsumer;
import rickbw.crud.ResourceProvider;


/**
 * An extension of {@link ResourceProvider} for the HTTP protocol, which
 * understands a few more verbs than the standard Create
 * (PUT/{@link #set(Object, Object, ResourceConsumer)}),
 * Read (GET/{@link #get(Object, ResourceConsumer)}),
 * Update (POST/{@link #update(Object, Object, ResourceConsumer)}), and
 * Delete (DELETE/{@link #delete(Object, ResourceConsumer)}).
 *
 * This interface does not support the HTTP CONNECT method, which is not used
 * for acting on or describing resources, but rather for changing the behavior
 * of HTTP proxies.
 */
public interface HttpResourceProvider<V> extends ResourceProvider<URL, V> {

    public abstract <VR> ListenableFuture<?> head(URL url, ResourceConsumer<? super URL, VR> consumer);

    /**
     * FIXME: Doesn't OPTIONS have a standard response form? We shouldn't
     *        need the generic parameter.
     */
    public abstract <VR> ListenableFuture<?> options(URL url, ResourceConsumer<? super URL, VR> consumer);

    /**
     * FIXME: TRACE requests have headers only, and the response body contains
     * an echo, so V is the incorrect type.
     */
    public abstract ListenableFuture<?> trace(URL url, ResourceConsumer<? super URL, ? super V> consumer);

}
