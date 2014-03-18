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
        private volatile ClientResponse latestResponse = null;

        public ClosingSubscriber(final Subscriber<? super ClientResponse> delegate) {
            this.delegate = delegate;
            assert this.delegate != null;
        }

        @Override
        public void onNext(final ClientResponse response) {
            assert this.latestResponse == null; // only one response per request
            this.delegate.onNext(response);
            /* If onNext() returns successfully, save the response object for
             * now, so the delegate can keep using it if it needs to. Then
             * close it when the delegate is all finished.
             */
            this.latestResponse = response;
        }

        @Override
        public void onCompleted() {
            try {
                this.delegate.onCompleted();
            } finally {
                closeLatestResponse();
            }
        }

        @Override
        public void onError(final Throwable error) {
            try {
                this.delegate.onError(error);
            } finally {
                closeLatestResponse();
            }
        }

        private void closeLatestResponse() {
            if (this.latestResponse != null) {
                this.latestResponse.close();
            }
        }
    }
}
