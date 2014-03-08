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

package rickbw.crud.http.example;

import java.net.URI;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

import rickbw.crud.ReadableResourceProvider;
import rickbw.crud.WritableResourceProvider;
import rickbw.crud.http.ClientRequest;
import rickbw.crud.http.JerseyReadableResourceProvider;
import rickbw.crud.http.JerseyWritableResourceProvider;
import rickbw.crud.util.FluentReadableResourceProvider;
import rickbw.crud.util.FluentWritableResourceProvider;
import rx.functions.Func1;


/*package*/ class FakeApplicationContext {

    private final Client restClientBean = new Client();

    private final ClientRequest templateRequest = ClientRequest.newBuilder()
            .acceptedMediaType(MediaType.APPLICATION_JSON_TYPE)
            .contentType(MediaType.APPLICATION_JSON_TYPE)
            .build();

    private final Func1<Long, URI> urlBuilderBean = new Func1<Long, URI>() {
        @Override
        public URI call(final Long input) {
            return URI.create("http://localhost/user/" + input);
        }
    };

    private final ReadableResourceProvider<URI, ClientResponse> restGetBean =
            new JerseyReadableResourceProvider(
                    this.restClientBean,
                    this.templateRequest);

    private final WritableResourceProvider<URI, ClientRequest, ClientResponse> restPutBean =
            new JerseyWritableResourceProvider(
                    this.restClientBean,
                    this.templateRequest);

    /* With different implementations, this could be backed by a Voldemort
     * or some other source. It doesn't have to be a REST service.
     */
    private final ReadableResourceProvider<Long, User> adaptedGetBean =
            FluentReadableResourceProvider.from(this.restGetBean)
                    .map(new Func1<ClientResponse, User>() {
                        @Override
                        public User call(final ClientResponse input) {
                            return input.getEntity(User.class);
                        }
                    })
                    .adaptKey(this.urlBuilderBean);

    /* With different implementations, this could be backed by a Voldemort
     * or some other source. It doesn't have to be a REST service.
     */
    private final WritableResourceProvider<Long, User, Boolean> adaptedPutBean =
            FluentWritableResourceProvider.from(this.restPutBean)
                    .map(new Func1<ClientResponse, Boolean>() {
                        @Override
                        public Boolean call(final ClientResponse input) {
                            return input.getStatus() < 300;
                        }
                    })
                    .adaptNewValue(new Func1<User, ClientRequest>() {
                        @Override
                        public ClientRequest call(final User user) {
                            final ClientRequest request = ClientRequest.newBuilder()
                                    .entity(user)
                                    .build();
                            return request;
                        }
                    })
                    .adaptKey(this.urlBuilderBean);

    /* The appBean doesn't care that this ResourceProvider supports both read
     * and write operations. That combination is just included here by way of
     * illustration.
     */
    public final ReadWritableUserResource.Provider providerBean = new ReadWritableUserResource.Provider(
            this.adaptedGetBean,
            this.adaptedPutBean);

    public final UserServiceApplication appBean = new UserServiceApplication(
            this.providerBean);

}
