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
package rickbw.crud.http;

import java.util.Objects;
import java.util.concurrent.Future;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.async.ITypeListener;

import rx.Observer;


/**
 * An Adapter from Jersey's {@link ITypeListener} to RxJava's {@link Observer}.
 */
/*package*/ final class ResponseListener implements ITypeListener<ClientResponse> {

    private final Observer<? super ClientResponse> observer;


    public static ResponseListener adapt(final Observer<? super ClientResponse> observer) {
        return new ResponseListener(observer);
    }

    @Override
    public void onComplete(final Future<ClientResponse> futureResponse)
    throws InterruptedException {
        try {
            final ClientResponse response = futureResponse.get();
            this.observer.onNext(response);
            this.observer.onCompleted();
        } catch (final Throwable ex) {
            this.observer.onError(ex);
        }
    }

    @Override
    public Class<ClientResponse> getType() {
        return ClientResponse.class;
    }

    @Override
    public GenericType<ClientResponse> getGenericType() {
        return null; // ClientResponse is not generic
    }

    private ResponseListener(final Observer<? super ClientResponse> observer) {
        this.observer = Objects.requireNonNull(observer);
    }

}
