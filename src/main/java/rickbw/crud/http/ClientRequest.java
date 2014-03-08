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
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sun.jersey.api.client.WebResource;

import rickbw.crud.ResourceProvider;


/**
 * A container for the state used to initialize HTTP
 * {@link ResourceProvider}s.
 */
public final class ClientRequest {

    private final Optional<Object> entityBody;
    private final Optional<MediaType> contentType;
    private final ImmutableSet<MediaType> acceptedTypes;
    private final ImmutableSet<Locale> acceptedLanguages;
    private final ImmutableSet<Cookie> cookies;
    private final ImmutableMap<String, Object> headers;


    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Get the {@link MediaType} that will be used to set the Content-Type
     * header, when a message has an entity body.
     */
    public Optional<MediaType> getContentType() {
        return this.contentType;
    }

    /**
     * Get the {@link MediaType}s that will be used in Accept request headers
     * to indicate those types that will be acceptable in the response.
     */
    public Set<MediaType> getAcceptedMediaTypes() {
        return this.acceptedTypes;
    }

    /**
     * Get the {@link Locale}s that will be used in Accept-Language request
     * headers to indicate those languages that will be acceptable in the
     * response.
     */
    public Set<Locale> getAcceptedLanguages() {
        return this.acceptedLanguages;
    }

    public Set<Cookie> getCookies() {
        return this.cookies;
    }

    public Map<String, Object> getHeaders() {
        return ImmutableMap.copyOf(this.headers);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ClientRequest)) {
            return false;
        }
        final ClientRequest other = (ClientRequest) obj;
        if (!this.acceptedLanguages.equals(other.acceptedLanguages)) {
            return false;
        }
        if (!this.acceptedTypes.equals(other.acceptedTypes)) {
            return false;
        }
        if (!this.contentType.equals(other.contentType)) {
            return false;
        }
        if (!this.cookies.equals(other.cookies)) {
            return false;
        }
        if (!this.entityBody.equals(other.entityBody)) {
            return false;
        }
        if (!this.headers.equals(other.headers)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + " [entityBody=" + this.entityBody
                + ", contentType=" + this.contentType
                + ", acceptedTypes=" + this.acceptedTypes
                + ", acceptedLanguages=" + this.acceptedLanguages
                + ", cookies=" + this.cookies
                + ", headers=" + this.headers
                + ']';
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.acceptedLanguages.hashCode();
        result = prime * result + this.acceptedTypes.hashCode();
        result = prime * result + this.contentType.hashCode();
        result = prime * result + this.cookies.hashCode();
        result = prime * result + this.entityBody.hashCode();
        result = prime * result + this.headers.hashCode();
        return result;
    }

    /**
     * Update the given resource request with the contents of this request
     * and return it back again.
     */
    /*package*/ WebResource.Builder updateResource(WebResource.Builder resource) {
        if (!this.acceptedTypes.isEmpty()) {
            final MediaType[] typesArray = this.acceptedTypes.toArray(
                    new MediaType[this.acceptedTypes.size()]);
            resource = resource.accept(typesArray);
        }
        if (!this.acceptedLanguages.isEmpty()) {
            final Locale[] languageArray = new Locale[this.acceptedLanguages.size()];
            resource = resource.acceptLanguage(languageArray);
        }
        if (this.contentType.isPresent()) {
            resource = resource.type(this.contentType.get());
        }
        for (final Cookie cookie : this.cookies) {
            resource = resource.cookie(cookie);
        }
        for (final Map.Entry<String, Object> entry : this.headers.entrySet()) {
            resource = resource.header(entry.getKey(), entry.getValue());
        }
        if (this.entityBody.isPresent()) {
            resource = resource.entity(this.entityBody.get());
        }
        return resource;
    }

    private ClientRequest(
            final Optional<Object> entityBody,
            final Optional<MediaType> contentType,
            final Iterable<MediaType> acceptedTypes,
            final Iterable<Locale> acceptedLanguages,
            final Iterable<Cookie> cookies,
            final Map<String, Object> headers) {
        this.entityBody = entityBody;
        assert this.entityBody != null;
        this.contentType = contentType;
        assert this.contentType != null;
        this.acceptedTypes = ImmutableSet.copyOf(acceptedTypes);
        this.acceptedLanguages = ImmutableSet.copyOf(acceptedLanguages);
        this.cookies = ImmutableSet.copyOf(cookies);
        this.headers = ImmutableMap.copyOf(headers);
    }


    public static final class Builder {
        private Optional<Object> entityBody = Optional.absent();
        private Optional<MediaType> contentType = Optional.absent();
        private final Set<MediaType> acceptedTypes = Sets.newHashSet();
        private final Set<Locale> acceptedLanguages = Sets.newHashSet();
        private final Set<Cookie> cookies = Sets.newHashSet();
        private final Map<String, Object> headers = Maps.newHashMap();

        /**
         * Set the entity body for the request. It must agree with the
         * content type set with {@link #contentType(MediaType)}.
         */
        public Builder entity(final Object body) {
            this.entityBody = Optional.of(body);
            return this;
        }

        /**
         * Set the {@link MediaType} to be used to set the Content-Type
         * header, when a message has an entity body. The body, set by
         * {@link #entity(Object)}, must agree with this type.
         */
        public Builder contentType(final MediaType type) {
            this.contentType = Optional.of(type);
            return this;
        }

        /**
         * Set the entity body and its content type together, to make sure
         * that they agree with one another.
         *
         * @see #entity(Object)
         * @see #contentType(MediaType)
         */
        public Builder entity(final Object body, final MediaType type) {
            entity(body);
            contentType(type);
            return this;
        }

        /**
         * Add a {@link MediaType} to be used in Accept request headers
         * to indicate those types that will be acceptable in the response.
         */
        public Builder acceptedMediaType(final MediaType newType) {
            this.acceptedTypes.add(Preconditions.checkNotNull(newType));
            return this;
        }

        /**
         * Set the {@link MediaType}s to be used in Accept request headers
         * to indicate those types that will be acceptable in the response.
         */
        public Builder acceptedMediaTypes(final Collection<MediaType> newMediaTypes) {
            this.acceptedTypes.clear();
            this.acceptedTypes.addAll(newMediaTypes);
            return this;
        }

        /**
         * Add a language to be used in Accept-Language request headers
         * to indicate those languages that will be acceptable in the
         * response.
         */
        public Builder acceptedLanguage(final Locale language) {
            this.acceptedLanguages.add(Preconditions.checkNotNull(language));
            return this;
        }

        /**
         * Set the languages to be used in Accept-Language request headers
         * to indicate those languages that will be acceptable in the
         * response.
         */
        public Builder acceptedLanguages(final Collection<Locale> languages) {
            this.acceptedLanguages.clear();
            this.acceptedLanguages.addAll(languages);
            return this;
        }

        public Builder cookie(final Cookie cookie) {
            this.cookies.add(Preconditions.checkNotNull(cookie));
            return this;
        }

        public Builder cookies(final Collection<Cookie> newCookies) {
            this.cookies.clear();
            this.cookies.addAll(newCookies);
            return this;
        }

        public Builder header(final String name, final Object value) {
            Preconditions.checkNotNull(name);
            Preconditions.checkNotNull(value);
            this.headers.put(name, value);
            return this;
        }

        public Builder headers(final Map<String, Object> newHeaders) {
            this.headers.clear();
            this.headers.putAll(newHeaders);
            return this;
        }

        public ClientRequest build() {
            return new ClientRequest(
                    this.entityBody,
                    this.contentType,
                    this.acceptedTypes,
                    this.acceptedLanguages,
                    this.cookies,
                    this.headers);
        }

        private Builder() {
            // prevent instantiation
        }
    }

}
