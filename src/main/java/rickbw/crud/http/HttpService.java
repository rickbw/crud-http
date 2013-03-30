package rickbw.crud.http;

import java.net.URL;

import rickbw.crud.CrudService;
import rickbw.crud.ResourceProvider;


/**
 * An extension of {@link CrudService} for the HTTP protocol, which
 * understands a few more verbs than the standard Create
 * (PUT/{@link #setter()}), Read (GET/{@link #provider()}),
 * Update (POST/{@link #updater()}), and Delete (DELETE/{@link #deleter()}).
 *
 * This interface does not support the HTTP CONNECT method, which is not used
 * for acting on or describing resources, but rather for changing the behavior
 * of HTTP proxies.
 */
public interface HttpService extends CrudService<URL> {

    public abstract ResourceProvider<URL> headProvider();

    /**
     * FIXME: Doesn't OPTIONS have a standard response form?
     */
    public abstract ResourceProvider<URL> optionsProvider();

    /**
     * FIXME: TRACE requests have headers only, and the response body contains
     * an echo, so what is the incorrect response type?
     */
    public abstract ResourceProvider<URL> tracer();

}
