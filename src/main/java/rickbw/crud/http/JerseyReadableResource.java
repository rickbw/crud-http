package rickbw.crud.http;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import rickbw.crud.ReadableResource;
import rickbw.crud.util.AsyncObservationFunction;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterface;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.util.functions.Func1;


public final class JerseyReadableResource<RSRC>
extends AbstractJerseyResource<RSRC>
implements ReadableResource<HttpResponse<RSRC>> {

    private final Func1<Observer<HttpResponse<RSRC>>, Subscription> subscribeAction;


    public JerseyReadableResource(
            final UniformInterface resource,
            final Class<? extends RSRC> resourceClass,
            final ExecutorService executor) {
        super(resource, resourceClass);

        final Callable<HttpResponse<RSRC>> responseProvider = new Callable<HttpResponse<RSRC>>() {
            @Override
            public HttpResponse<RSRC> call() {
                final ClientResponse response = resource.get(ClientResponse.class);
                final HttpResponse<RSRC> safeResponse = HttpResponse.wrapAndClose(response, resourceClass);
                return safeResponse;
            }
        };
        this.subscribeAction = new AsyncObservationFunction<HttpResponse<RSRC>>(responseProvider, executor);
    }

    @Override
    public Observable<HttpResponse<RSRC>> get() {
        return Observable.create(this.subscribeAction);
    }

}
