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

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterface;
import com.sun.jersey.api.client.WebResource;

import rickbw.crud.ReadableResource;
import rickbw.crud.util.AsyncObservationFunction;
import rx.Observable;


public final class JerseyReadableResource
extends AbstractJerseyResource
implements ReadableResource<ClientResponse> {

    private final Observable.OnSubscribe<ClientResponse> subscribeAction;


    public JerseyReadableResource(
            final WebResource resource,
            final ClientRequest requestTemplate,
            final ExecutorService executor) {
        super(resource, requestTemplate);
        final UniformInterface configuredResource = configuredResource();
        final Callable<ClientResponse> responseProvider = new Callable<ClientResponse>() {
            @Override
            public ClientResponse call() {
                return configuredResource.get(ClientResponse.class);
            }
        };
        this.subscribeAction = new AsyncObservationFunction<>(responseProvider, executor);
    }

    @Override
    public Observable<ClientResponse> get() {
        final Observable<ClientResponse> obs = Observable.create(this.subscribeAction)
                .lift(ClientResponseCloser.instance());
        return obs;
    }

}
