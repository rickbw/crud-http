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
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.RequestBuilder;
import com.sun.jersey.api.client.UniformInterface;


/*package*/ final class RequestProvider {

    private final Client restClient;
    private final Optional<MediaType> type;
    private final ImmutableMap<String, Object> headers;

    /**
     * Keeping this as an array is less safe than using an {@link ImmutableSet},
     * but since we need it as an array for every request, the performance is
     * better if we just do the transformation to an array once, up front.
     */
    private final MediaType[] mediaTypes;


    public RequestProvider(final Client restClient, final ClientConfiguration config) {
        this.restClient = Preconditions.checkNotNull(restClient);

        this.type = Optional.fromNullable(config.getType());
        this.headers = ImmutableMap.copyOf(config.getHeaders());

        final Collection<MediaType> mediaTypesSet = config.getMediaTypes();
        this.mediaTypes = mediaTypesSet.toArray(new MediaType[mediaTypesSet.size()]);
    }

    public UniformInterface getResource(final URI uri) {
        RequestBuilder<?> resource = this.restClient.resource(uri);
        if (this.mediaTypes.length > 0) {
            resource = resource.accept(this.mediaTypes);
        }
        if (this.type.isPresent()) {
            resource = resource.type(this.type.get());
        }
        for (final Map.Entry<String, Object> entry : this.headers.entrySet()) {
            resource = resource.header(entry.getKey(), entry.getValue());
        }
        // WebResource and WebResource.Builder both implement UniformInterface
        return (UniformInterface) resource;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [restClient=" + this.restClient +
                ", type=" + this.type +
                ", headers=" + this.headers +
                ", mediaTypes=" + Arrays.toString(this.mediaTypes) + ']';
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
        final RequestProvider other = (RequestProvider) obj;
        if (!this.headers.equals(other.headers)) {
            return false;
        }
        if (!Arrays.equals(this.mediaTypes, other.mediaTypes)) {
            return false;
        }
        if (!this.restClient.equals(other.restClient)) {
            return false;
        }
        if (!this.type.equals(other.type)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.headers.hashCode();
        result = prime * result + Arrays.hashCode(this.mediaTypes);
        result = prime * result + this.restClient.hashCode();
        result = prime * result + this.type.hashCode();
        return result;
    }

}
