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

import rickbw.crud.ReadableResource;
import rickbw.crud.util.AsyncObservationFunction;
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
