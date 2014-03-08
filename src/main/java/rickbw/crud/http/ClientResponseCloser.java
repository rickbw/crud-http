package rickbw.crud.http;

import com.sun.jersey.api.client.ClientResponse;

import rx.Observable;
import rx.Subscriber;


/*package*/ final class ClientResponseCloser
implements Observable.Operator<ClientResponse, ClientResponse> {

    private static final ClientResponseCloser instance = new ClientResponseCloser();


    public static ClientResponseCloser instance() {
        return instance;
    }

    @Override
    public Subscriber<? super ClientResponse> call(
            final Subscriber<? super ClientResponse> subscriber) {
        return new ClosingSubscriber(subscriber);
    }

    private ClientResponseCloser() {
        // prevent instantiation
    }


    private static final class ClosingSubscriber extends Subscriber<ClientResponse> {
        private final Subscriber<? super ClientResponse> delegate;
        private transient ClientResponse latestResponse = null;

        public ClosingSubscriber(final Subscriber<? super ClientResponse> delegate) {
            this.delegate = delegate;
            assert this.delegate != null;
        }

        @Override
        public void onNext(final ClientResponse response) {
            closePreviousResponse();
            this.latestResponse = response;
            this.delegate.onNext(response);
        }

        @Override
        public void onCompleted() {
            closePreviousResponse();
            this.delegate.onCompleted();
        }

        @Override
        public void onError(final Throwable error) {
            closePreviousResponse();
            this.delegate.onError(error);
        }

        private void closePreviousResponse() {
            if (this.latestResponse != null) {
                this.latestResponse.close();
            }
        }
    }
}
