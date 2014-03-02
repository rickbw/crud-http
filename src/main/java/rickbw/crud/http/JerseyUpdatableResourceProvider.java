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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterface;

import rickbw.crud.UpdatableResourceProvider;


public final class JerseyUpdatableResourceProvider<RESPONSE>
extends AbstractResourceProvider<RESPONSE>
implements UpdatableResourceProvider<URI, Object, HttpResponse<RESPONSE>> {

    public JerseyUpdatableResourceProvider(
            final Client restClient,
            final Class<? extends RESPONSE> responseClass,
            final ClientConfiguration config) {
        super(restClient, responseClass, config);
    }

    @Override
    public JerseyUpdatableResource<RESPONSE> get(final URI uri) {
        final UniformInterface resource = getResource(uri);
        return new JerseyUpdatableResource<RESPONSE>(resource, getResponseClass(), getExecutor());
    }

}
