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
import java.util.Objects;
import java.util.Set;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sun.jersey.api.client.PartialRequestBuilder;

import rickbw.crud.ResourceProvider;


/**
 * A container for the state used to initialize HTTP
 * {@link ResourceProvider}s.
 */
public final class ClientRequest {

    private static final ClientRequest emptyRequest = newBuilder().build();

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
     * @throws NullPointerException If the given source is null.
     */
    public static Builder newBuilder(final ClientRequest source) {
        return new Builder(source);
    }

    /**
     * @return  A new request object with empty contents, as by calling
     *          {@link #newBuilder()} followed by {@link Builder#build()}.
     */
    public static ClientRequest empty() {
        return emptyRequest;
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
        final StringBuilder buf = new StringBuilder(getClass().getSimpleName());
        buf.append('[');
        boolean previousField = false;
        if (this.entityBody.isPresent()) {
            buf.append("entityBody=").append(this.entityBody.get());
            previousField = true;
        }
        if (this.contentType.isPresent()) {
            if (previousField) {
                buf.append(", ");
            }
            buf.append("contentType=").append(this.contentType.get());
            previousField = true;
        }
        if (!this.acceptedTypes.isEmpty()) {
            if (previousField) {
                buf.append(", ");
            }
            buf.append("acceptedTypes=").append(this.acceptedTypes);
            previousField = true;
        }
        if (!this.acceptedLanguages.isEmpty()) {
            if (previousField) {
                buf.append(", ");
            }
            buf.append("acceptedLanguages=").append(this.acceptedLanguages);
            previousField = true;
        }
        if (!this.cookies.isEmpty()) {
            if (previousField) {
                buf.append(", ");
            }
            buf.append("cookies=").append(this.cookies);
            previousField = true;
        }
        if (!this.headers.isEmpty()) {
            if (previousField) {
                buf.append(", ");
            }
            buf.append("headers=").append(this.headers);
        }
        buf.append(']');
        return buf.toString();
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
    /*package*/ void updateResource(final PartialRequestBuilder<?> resource) {
        if (!this.acceptedTypes.isEmpty()) {
            final MediaType[] typesArray = this.acceptedTypes.toArray(
                    new MediaType[this.acceptedTypes.size()]);
            resource.accept(typesArray);
        }
        if (!this.acceptedLanguages.isEmpty()) {
            final Locale[] languageArray = new Locale[this.acceptedLanguages.size()];
            resource.acceptLanguage(languageArray);
        }
        if (this.contentType.isPresent()) {
            resource.type(this.contentType.get());
        }
        for (final Cookie cookie : this.cookies) {
            resource.cookie(cookie);
        }
        for (final Map.Entry<String, Object> entry : this.headers.entrySet()) {
            resource.header(entry.getKey(), entry.getValue());
        }
        if (this.entityBody.isPresent()) {
            resource.entity(this.entityBody.get());
        }
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
        private Optional<Object> entityBody;
        private Optional<MediaType> contentType;
        private final Set<MediaType> acceptedTypes;
        private final Set<Locale> acceptedLanguages;
        private final Set<Cookie> cookies;
        private final Map<String, Object> headers;

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
            this.acceptedTypes.add(Objects.requireNonNull(newType));
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
            this.acceptedLanguages.add(Objects.requireNonNull(language));
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
            this.cookies.add(Objects.requireNonNull(cookie));
            return this;
        }

        public Builder cookies(final Collection<Cookie> newCookies) {
            this.cookies.clear();
            this.cookies.addAll(newCookies);
            return this;
        }

        public Builder header(final String name, final Object value) {
            Objects.requireNonNull(name);
            Objects.requireNonNull(value);
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
            this.entityBody = Optional.absent();
            this.contentType = Optional.absent();
            this.acceptedTypes = Sets.newHashSet();
            this.acceptedLanguages = Sets.newHashSet();
            this.cookies = Sets.newHashSet();
            this.headers = Maps.newHashMap();
        }

        /**
         * @throws NullPointerException If the given source is null.
         */
        private Builder(final ClientRequest source) {
            this.entityBody = source.entityBody;
            this.contentType = source.contentType;
            this.acceptedTypes = Sets.newHashSet(source.acceptedTypes);
            this.acceptedLanguages = Sets.newHashSet(source.acceptedLanguages);
            this.cookies = Sets.newHashSet(source.cookies);
            this.headers = Maps.newHashMap(source.headers);
        }
    }

}
