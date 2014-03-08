/* Copyright 2013–2014 Rick Warren
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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterface;

import rickbw.crud.UpdatableResource;
import rickbw.crud.util.AsyncObservationFunction;
import rx.Observable;


public final class JerseyUpdatableResource
extends AbstractJerseyResource
implements UpdatableResource<Object, ClientResponse> {

    private final ExecutorService executor;


    public JerseyUpdatableResource(
            final UniformInterface resource,
            final ExecutorService executor) {
        super(resource);
        this.executor = Preconditions.checkNotNull(executor);
    }

    @Override
    public Observable<ClientResponse> update(final Object update) {
        Preconditions.checkNotNull(update);

        final Callable<ClientResponse> responseProvider = new Callable<ClientResponse>() {
            @Override
            public ClientResponse call() {
                return getResource().post(ClientResponse.class, update);
            }
        };
        final Observable.OnSubscribe<ClientResponse> subscribeAction = new AsyncObservationFunction<>(
                responseProvider,
                this.executor);
        final Observable<ClientResponse> obs = Observable.create(subscribeAction);
        final Observable<ClientResponse> safeObs = obs.lift(ClientResponseCloser.instance());
        return safeObs;
    }

}
