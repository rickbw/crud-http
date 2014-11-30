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
package crud.http.example;

import java.net.URI;
import java.util.UUID;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

import crud.http.ClientRequest;
import crud.http.HttpResourceProvider;
import crud.http.util.FailedResponseOperator;
import crud.rsrc.GettableSet;
import crud.rsrc.SettableSet;
import crud.spi.ResourceSet;
import rx.Observable;
import rx.functions.Func1;


/**
 * Imagine that your {@link Asset}-handling application is assembled in a
 * Spring application context. If it were, that context would do something
 * like this.
 */
class ExampleApplicationContext {

    /**
     * Somewhere on the network is a web service that can read and write
     * {@link Asset}s.
     */
    private static final String assetServiceBaseUrl = "http://localhost/asset";

    /**
     * This Jersey client is going to talk to the web service located at the
     * {@link #assetServiceBaseUrl}.
     */
    private final Client restClient = new Client();

    /**
     * All requests to the {@link #restClient} will indicate JSON data in
     * and JSON data out.
     */
    private final ClientRequest templateRequest = ClientRequest.newBuilder()
            .acceptedMediaType(MediaType.APPLICATION_JSON_TYPE)
            .contentType(MediaType.APPLICATION_JSON_TYPE)
            .build();

    /**
     * A provider of resources that can read and write JSON-encoded
     * {@link Asset}s from the web service.
     */
    private final HttpResourceProvider restResource = HttpResourceProvider.forClientWithTemplate(
            this.restClient,
            this.templateRequest);

    /**
     * Provides the {@link URI} at which an {@link Asset} of a given ID can
     * be found. (Hint: it will be at a path underneath the
     * {@link #assetServiceBaseUrl}.)
     */
    private final Func1<UUID, URI> urlBuilder = new Func1<UUID, URI>() {
        @Override
        public URI call(final UUID id) {
            return URI.create(assetServiceBaseUrl + '/' + id);
        }
    };

    /**
     * Encapsulates an {@link Asset} into an HTTP request message.
     */
    private final Func1<Asset, ClientRequest> assetEncoder = new Func1<Asset, ClientRequest>() {
        @Override
        public ClientRequest call(final Asset asset) {
            final ClientRequest request = ClientRequest.newBuilder()
                    .entity(asset)
                    .build();
            return request;
        }
    };

    /**
     * Parses an {@link Asset} out of an HTTP response message.
     */
    private final Func1<ClientResponse, Asset> assetDecoder = new Func1<ClientResponse, Asset>() {
        @Override
        public Asset call(final ClientResponse response) {
            return response.getEntity(Asset.class);
        }
    };

    /**
     * Determines whether a request to PUT a remote {@link Asset} resource
     * was successful.
     */
    private final Func1<ClientResponse, Boolean> assetPutSuccessDecoder = new Func1<ClientResponse, Boolean>() {
        @Override
        public Boolean call(final ClientResponse input) {
            return input.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL;
        }
    };

    private final Func1<Observable<ClientResponse>, Observable<ClientResponse>> retryServerErrors = new Func1<Observable<ClientResponse>, Observable<ClientResponse>>() {
        @Override
        public Observable<ClientResponse> call(final Observable<ClientResponse> t1) {
            return t1.lift(FailedResponseOperator.serverErrors()).retry(3);
        }
    };

    /**
     * Assemble the {@link ResourceSet} for {@link Asset}s by indicating:
     * <ol>
     *  <li>how to read them (i.e. from a web service),</li>
     *  <li>how to write them (i.e. to a web service),</li>
     *  <li>and how to transform between the native formats of those delegate
     *      providers and our application-specific language of "assets".</li>
     * </ol>
     */
    public final AssetSet assetProvider = AssetSet.create(
            // Retry all server errors on GET up to 3 times:
            GettableSet.from(restResource).mapValue(retryServerErrors),
            // Retry all server errors on PUT up to 3 times:
            SettableSet.from(restResource).mapResponse(retryServerErrors),
            urlBuilder,
            assetDecoder,
            assetEncoder,
            assetPutSuccessDecoder);

    /**
     * Business logic goes in here!
     */
    public final AssetApplication application = new AssetApplication(
            this.assetProvider);

}
