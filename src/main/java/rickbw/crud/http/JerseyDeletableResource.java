package rickbw.crud.http;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import rickbw.crud.DeletableResource;
import rickbw.crud.adapter.AsyncObservationFunction;
import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterface;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.util.functions.Func1;


public final class JerseyDeletableResource<RESPONSE>
implements DeletableResource<HttpResponse<RESPONSE>> {

    private final Func1<Observer<HttpResponse<RESPONSE>>, Subscription> subscribeAction;


    public JerseyDeletableResource(
            final UniformInterface resource,
            final Class<? extends RESPONSE> responseClass,
            final ExecutorService executor) {
        Preconditions.checkNotNull(resource);
        Preconditions.checkNotNull(responseClass);

        final Callable<HttpResponse<RESPONSE>> responseProvider = new Callable<HttpResponse<RESPONSE>>() {
            @Override
            public HttpResponse<RESPONSE> call() {
                final ClientResponse response = resource.delete(ClientResponse.class);
                final HttpResponse<RESPONSE> safeResponse = HttpResponse.wrapAndClose(response, responseClass);
                return safeResponse;
            }
        };
        this.subscribeAction = new AsyncObservationFunction<HttpResponse<RESPONSE>>(responseProvider, executor);
    }

    @Override
    public Observable<HttpResponse<RESPONSE>> delete() {
        return Observable.create(this.subscribeAction);
    }

}
