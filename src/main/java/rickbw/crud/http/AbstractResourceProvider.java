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
import com.sun.jersey.api.client.UniformInterface;

import rickbw.crud.ResourceProvider;


/*package*/ abstract class AbstractResourceProvider<RESPONSE>
implements ResourceProvider<URI> {

    // XXX: Should RequestProvider be inlined into this class?
    private final RequestProvider requester;
    private final Class<? extends RESPONSE> responseClass;
    private final ExecutorService executor;


    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [requester=" + this.requester +
                ", responseClass=" + getResponseClass() + ']';
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
        final AbstractResourceProvider<?> other = (AbstractResourceProvider<?>) obj;
        if (!this.requester.equals(other.requester)) {
            return false;
        }
        if (!getResponseClass().equals(other.getResponseClass())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.requester.hashCode();
        result = prime * result + getResponseClass().hashCode();
        return result;
    }

    protected AbstractResourceProvider(
            final Client restClient,
            final Class<? extends RESPONSE> resourceClass,
            final ClientConfiguration config) {
        this.requester = new RequestProvider(restClient, config);
        this.responseClass = Preconditions.checkNotNull(resourceClass);
        this.executor = restClient.getExecutorService();
    }

    protected final UniformInterface getResource(final URI uri) {
        return this.requester.getResource(uri);
    }

    protected final Class<? extends RESPONSE> getResponseClass() {
        return this.responseClass;
    }

    protected final ExecutorService getExecutor() {
        return this.executor;
    }

}
