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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.core.MediaType;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import rickbw.crud.ResourceProvider;


/**
 * A container for the state used to initialize HTTP
 * {@link ResourceProvider}s.
 */
public final class ClientConfiguration {
    @Nullable
    private MediaType type = null;
    private final Set<MediaType> mediaTypes = Sets.newHashSet();
    private final Map<String, Object> headers = Maps.newHashMap();


    @Nullable
    public MediaType getType() {
        return this.type;
    }

    public void setType(@Nullable final MediaType type) {
        this.type = type;
    }

    public Collection<MediaType> getMediaTypes() {
        return ImmutableSet.copyOf(this.mediaTypes);
    }

    public void setMediaTypes(final Collection<MediaType> newMediaTypes) {
        this.mediaTypes.clear();
        this.mediaTypes.addAll(newMediaTypes);
    }

    public void addMediaType(final MediaType newType) {
        Preconditions.checkNotNull(newType);
        this.mediaTypes.add(newType);
    }

    public Map<String, Object> getHeaders() {
        return ImmutableMap.copyOf(this.headers);
    }

    public void setHeaders(final Map<String, Object> newHeaders) {
        this.headers.clear();
        this.headers.putAll(newHeaders);
    }

    public void addHeader(final String name, final Object value) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(value);
        this.headers.put(name, value);
    }

}
