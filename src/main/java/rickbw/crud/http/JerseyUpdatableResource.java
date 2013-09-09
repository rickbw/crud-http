package rickbw.crud.http;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import rickbw.crud.UpdatableResource;
import rickbw.crud.adapter.AsyncObservationFunction;
import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterface;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.util.functions.Func1;


public final class JerseyUpdatableResource<RESPONSE>
implements UpdatableResource<Object, HttpResponse<RESPONSE>> {

    private final UniformInterface resource;
    private final Class<? extends RESPONSE> responseClass;
    private final ExecutorService executor;


    public JerseyUpdatableResource(
            final UniformInterface resource,
            final Class<? extends RESPONSE> responseClass,
            final ExecutorService executor) {
        this.resource = Preconditions.checkNotNull(resource);
        this.responseClass = Preconditions.checkNotNull(responseClass);
        this.executor = Preconditions.checkNotNull(executor);
    }

    @Override
    public Observable<HttpResponse<RESPONSE>> update(final Object update) {
        final Callable<HttpResponse<RESPONSE>> responseProvider = new Callable<HttpResponse<RESPONSE>>() {
            @Override
            public HttpResponse<RESPONSE> call() {
                final ClientResponse response = resource.post(ClientResponse.class, update);
                final HttpResponse<RESPONSE> safeResponse = HttpResponse.wrapAndClose(response, responseClass);
                return safeResponse;
            }
        };
        final Func1<Observer<HttpResponse<RESPONSE>>, Subscription> subscribeAction =
                new AsyncObservationFunction<HttpResponse<RESPONSE>>(responseProvider, this.executor);
        return Observable.create(subscribeAction);
    }

    // TODO: equals() and hashCode()

}
