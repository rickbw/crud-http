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

import java.net.URI;

import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import rickbw.crud.DeletableResourceProvider;
import rickbw.crud.ReadableResourceProvider;
import rickbw.crud.ResourceProvider;
import rickbw.crud.UpdatableResourceProvider;
import rickbw.crud.WritableResourceProvider;


/**
 * A {@link ResourceProvider} based on Jersey that provides
 * {@link HttpResource}s at given {@link URI}s. These resources are capable of
 * all four CRUD actions: reading, writing, updating, and deleting.
 */
public final class HttpResourceProvider
implements ReadableResourceProvider<URI, ClientResponse>,
           DeletableResourceProvider<URI, ClientResponse>,
           WritableResourceProvider<URI, ClientRequest, ClientResponse>,
           UpdatableResourceProvider<URI, ClientRequest, ClientResponse> {

    private final Client restClient;
    private final ClientRequest requestTemplate;


    /**
     * Create a new {@link ResourceProvider} backed by the given
     * {@link Client}.
     */
    public static HttpResourceProvider forClient(final Client restClient) {
        return new HttpResourceProvider(restClient, ClientRequest.empty());
    }

    /**
     * Create a new {@link ResourceProvider} backed by the given
     * {@link Client}. Each request will include all of the elements of the
     * given request. For example, if all communication should use JSON, you
     * might pass the result of the following:
     *
     * <pre><code>
     *  ClientRequest.newBuilder()
     *      .acceptedMediaType(MediaType.APPLICATION_JSON_TYPE)
     *      .contentType(MediaType.APPLICATION_JSON_TYPE)
     *      .build();
     * </code></pre>
     */
    public static HttpResourceProvider forClientWithTemplate(
            final Client restClient,
            final ClientRequest requestTemplate) {
        return new HttpResourceProvider(restClient, requestTemplate);
    }

    @Override
    public HttpResource get(final URI uri) {
        final WebResource resource = this.restClient.resource(uri);
        /* Pass just the WebResource and the ExecutorService, since that's all
         * the HttpResource needs. No reason to expose e.g. the ability to
         * look up additional resources.
         */
        return new HttpResource(
                resource,
                this.requestTemplate,
                this.restClient.getExecutorService());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + " [restClient=" + this.restClient
                + ", requestTemplate=" + this.requestTemplate
                + ']';
    }

    private HttpResourceProvider(
            final Client restClient,
            final ClientRequest requestTemplate) {
        this.restClient = Preconditions.checkNotNull(restClient);
        this.requestTemplate = Preconditions.checkNotNull(requestTemplate);
    }

}
