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

import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.WebResource;

import rickbw.crud.Resource;


/*package*/ abstract class AbstractJerseyResource implements Resource {

    private final WebResource resource;
    private final ClientRequest requestTemplate;


    @Override
    public String toString() {
        return getClass().getSimpleName() + " [resource=" + this.resource + ']';
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
        final AbstractJerseyResource other = (AbstractJerseyResource) obj;
        if (!this.resource.equals(other.resource)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.resource.hashCode();
        return result;
    }

    protected AbstractJerseyResource(
            final WebResource resource,
            final ClientRequest requestTemplate) {
        this.resource = Preconditions.checkNotNull(resource);
        this.requestTemplate = Preconditions.checkNotNull(requestTemplate);
    }

    protected final WebResource.Builder configuredResource() {
        final WebResource.Builder newResource = this.requestTemplate.updateResource(
                this.resource.getRequestBuilder());
        return newResource;
    }

}
