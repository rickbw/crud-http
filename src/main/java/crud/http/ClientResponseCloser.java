/* Copyright 2014 Rick Warren
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package crud.http;

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
            super(delegate);
            this.delegate = delegate;
            assert this.delegate != null;
        }

        @Override
        public void onNext(final ClientResponse response) {
            assert this.latestResponse == null; // only one response per request
            /* Save the response object for now, so the delegate can keep
             * using it if it needs to. Then close it when the delegate is all
             * finished.
             */
            this.latestResponse = response;
            this.delegate.onNext(response);
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
                /* If onCompleted() itself throws, onError() can still be
                 * called. That could result in duplicate calls to close().
                 * Clear the state so that won't happen.
                 */
                this.latestResponse = null;
            }
        }
    }
}
