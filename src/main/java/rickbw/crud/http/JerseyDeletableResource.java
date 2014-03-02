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

import rickbw.crud.DeletableResource;
import rickbw.crud.util.AsyncObservationFunction;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.util.functions.Func1;


public final class JerseyDeletableResource<RESPONSE>
extends AbstractJerseyResource<RESPONSE>
implements DeletableResource<HttpResponse<RESPONSE>> {

    private final Func1<Observer<HttpResponse<RESPONSE>>, Subscription> subscribeAction;


    public JerseyDeletableResource(
            final UniformInterface resource,
            final Class<? extends RESPONSE> responseClass,
            final ExecutorService executor) {
        super(resource, responseClass);

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
