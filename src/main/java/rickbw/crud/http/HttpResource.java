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

import java.util.concurrent.Future;

import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.AsyncWebResource;
import com.sun.jersey.api.client.ClientResponse;

import rickbw.crud.DeletableResource;
import rickbw.crud.ReadableResource;
import rickbw.crud.Resource;
import rickbw.crud.UpdatableResource;
import rickbw.crud.WritableResource;
import rx.Observable;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;


/**
 * A {@link Resource} based on Jersey, capable of all four CRUD actions:
 * reading, writing, updating, and deleting.
 */
public class HttpResource
implements ReadableResource<ClientResponse>,
           DeletableResource<ClientResponse>,
           WritableResource<ClientRequest, ClientResponse>,
           UpdatableResource<ClientRequest, ClientResponse> {

    private final AsyncWebResource resource;
    private final ClientRequest requestTemplate;

    private final Observable.OnSubscribe<ClientResponse> cachedOnGetAction;
    private final Observable.OnSubscribe<ClientResponse> cachedOnDeleteAction;


    @Override
    public Observable<ClientResponse> get() {
        final Observable<ClientResponse> obs = Observable.create(this.cachedOnGetAction)
                .lift(ClientResponseCloser.instance());
        return obs;
    }

    @Override
    public Observable<ClientResponse> delete() {
        final Observable<ClientResponse> obs = Observable.create(this.cachedOnDeleteAction)
                .lift(ClientResponseCloser.instance());
        return obs;
    }

    @Override
    public Observable<ClientResponse> write(final ClientRequest resourceState) {
        final Observable.OnSubscribe<ClientResponse> subscribeAction = new Observable.OnSubscribe<ClientResponse>() {
            @Override
            public void call(final Subscriber<? super ClientResponse> subscriber) {
                final AsyncWebResource.Builder request = HttpResource.this.resource.getRequestBuilder();
                HttpResource.this.requestTemplate.updateResource(request);
                resourceState.updateResource(request);
                // Don't pass resourceState to put(): already in request
                final Future<ClientResponse> response = request.put(ResponseListener.adapt(subscriber));
                subscriber.add(Subscriptions.from(response));
            }
        };
        final Observable<ClientResponse> obs = Observable.create(subscribeAction)
                .lift(ClientResponseCloser.instance());
        return obs;
    }

    @Override
    public Observable<ClientResponse> update(final ClientRequest update) {
        final Observable.OnSubscribe<ClientResponse> subscribeAction = new Observable.OnSubscribe<ClientResponse>() {
            @Override
            public void call(final Subscriber<? super ClientResponse> subscriber) {
                final AsyncWebResource.Builder request = HttpResource.this.resource.getRequestBuilder();
                HttpResource.this.requestTemplate.updateResource(request);
                update.updateResource(request);
                // Don't pass resourceState to post(): already in request
                final Future<ClientResponse> response = request.post(ResponseListener.adapt(subscriber));
                subscriber.add(Subscriptions.from(response));
            }
        };
        final Observable<ClientResponse> obs = Observable.create(subscribeAction)
                .lift(ClientResponseCloser.instance());
        return obs;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + " [resource=" + this.resource
                + ", requestTemplate=" + this.requestTemplate
                + ']';
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HttpResource other = (HttpResource) obj;
        if (!this.resource.equals(other.resource)) {
            return false;
        }
        if (!this.requestTemplate.equals(other.requestTemplate)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.resource.hashCode();
        result = prime * result + this.requestTemplate.hashCode();
        return result;
    }

    /*package*/ HttpResource(
            final AsyncWebResource resource,
            final ClientRequest requestTemplate) {
        this.resource = Preconditions.checkNotNull(resource);
        this.requestTemplate = Preconditions.checkNotNull(requestTemplate);

        this.cachedOnGetAction = new Observable.OnSubscribe<ClientResponse>() {
            @Override
            public void call(final Subscriber<? super ClientResponse> subscriber) {
                final AsyncWebResource.Builder request = HttpResource.this.resource.getRequestBuilder();
                HttpResource.this.requestTemplate.updateResource(request);
                final Future<ClientResponse> response = request.get(ResponseListener.adapt(subscriber));
                subscriber.add(Subscriptions.from(response));
            }
        };
        this.cachedOnDeleteAction = new Observable.OnSubscribe<ClientResponse>() {
            @Override
            public void call(final Subscriber<? super ClientResponse> subscriber) {
                final AsyncWebResource.Builder request = HttpResource.this.resource.getRequestBuilder();
                HttpResource.this.requestTemplate.updateResource(request);
                final Future<ClientResponse> response = request.delete(ResponseListener.adapt(subscriber));
                subscriber.add(Subscriptions.from(response));
            }
        };
    }

}
