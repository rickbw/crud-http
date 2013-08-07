/**
 * This package contains implementations of the interfaces in the package
 * {@link rickbw.crud} in terms of the Jersey HTTP
 * library. It contains both
 * {@link rickbw.crud.Resource}s (e.g.
 * {@link rickbw.crud.http.JerseyReadableResource})
 * and {@link rickbw.crud.ResourceProvider}s (e.g.
 * {@link rickbw.crud.http.JerseyReadableResourceProvider}).
 * Resources are asynchronous, and use the
 * {@link java.util.concurrent.ExecutorService} from the
 * {@link com.sun.jersey.api.client.Client} itself.
 */
package rickbw.crud.http;
