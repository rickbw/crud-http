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
import java.util.concurrent.ExecutorService;

import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import rickbw.crud.WritableResourceProvider;


public final class JerseyWritableResourceProvider
implements WritableResourceProvider<URI, ClientRequest, ClientResponse> {

    private final Client restClient;
    private final ClientRequest requestTemplate;
    private final ExecutorService executor;


    public JerseyWritableResourceProvider(
            final Client restClient,
            final ClientRequest requestTemplate) {
        this.restClient = Preconditions.checkNotNull(restClient);
        this.requestTemplate = Preconditions.checkNotNull(requestTemplate);
        this.executor = restClient.getExecutorService();
        assert this.executor != null;
    }

    @Override
    public JerseyWritableResource get(final URI uri) {
        final WebResource resource = this.restClient.resource(uri);
        return new JerseyWritableResource(
                resource,
                this.requestTemplate,
                this.executor);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + " [restClient=" + this.restClient
                + ", requestTemplate=" + this.requestTemplate
                + ']';
    }

}
