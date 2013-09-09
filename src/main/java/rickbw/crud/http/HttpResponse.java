package rickbw.crud.http;

import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.ext.MessageBodyReader;

import org.joda.time.Instant;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResourceLinkHeaders;


/**
 * A wrapper for Jersey's {@link ClientResponse} that overcomes many design
 * flaws in that class:
 * <ul>
 *  <li>It is type-safe.</li>
 *  <li>It is immutable (with the possible exception of certain nested
 *      objects).</li>
 *  <li>It does not require closing, thereby preventing potentially severe
 *      resource leaks.</li>
 *  <li>It provides reasonable {@link #equals(Object)}, {@link #hashCode()},
 *      and {@link #toString()} implementations.</li>
 * </ul>
 */
public final class HttpResponse<ENTITY> {

    private final ClientResponse delegate;
    @Nullable
    private final Optional<ENTITY> entity;


    /**
     * Create a new HttpResponse to wrap the given {@link ClientResponse},
     * and then call {@link ClientResponse#close()} on it.
     *
     * @throws ClientHandlerException If there is an error reading from the
     *         response, such as if there is no {@link MessageBodyReader}
     *         available for the given response and entity type.
     */
    public static <ENTITY> HttpResponse<ENTITY> wrapAndClose(
            final ClientResponse delegate,
            final Class<? extends ENTITY> entityType) {
        try {
            if (delegate.getStatus() == 204 /*no content*/ || !delegate.hasEntity()) {
                return new HttpResponse<ENTITY>(delegate, null);
            } else {
                final ENTITY entity = delegate.getEntity(entityType);
                return new HttpResponse<ENTITY>(delegate, entity);
            }
        } finally {
            delegate.close();
        }
    }

    /**
     * Get the allowed HTTP methods from the Allow HTTP header.
     * <p>
     * Note that the Allow HTTP header will be returned from an OPTIONS
     * request.
     *
     * @return the allowed HTTP methods, all methods will returned as
     *         upper case strings.
     */
    public Set<String> getAllow() {
        return this.delegate.getAllow();
    }

    /**
     * Get the language (i.e. from a Content-Language header).
     *
     * @return the language, if present.
     */
    public Optional<String> getContentLanguage() {
        @Nullable final String language = this.delegate.getLanguage();
        return Optional.fromNullable(language);
    }

    /**
     * Get Content-Length.
     *
     * @return Content-Length as integer if present and valid number.
     */
    public Optional<Integer> getContentLength() {
        final int length = this.delegate.getLength();
        if (length < 0) {
            return Optional.absent();
        } else {
            return Optional.of(length);
        }
    }

    /**
     * Get the list of cookies.
     *
     * @return the cookies.
     */
    public List<NewCookie> getCookies() {
        return this.delegate.getCookies();
    }

    /**
     * Get the entity of the response. Note that not all responses have
     * entities, e.g. 2.04 responses, so it may not be present.
     */
    public Optional<ENTITY> getEntity() {
        return this.entity;
    }

    /**
     * Get the entity tag (i.e. from an ETag header).
     *
     * @return the entity tag, if present.
     */
    public Optional<EntityTag> getEntityTag() {
        @Nullable final EntityTag entityTag = this.delegate.getEntityTag();
        return Optional.fromNullable(entityTag);
    }

    /**
     * Get the HTTP headers of the response.
     *
     * @return the HTTP headers of the response.
     */
    public MultivaluedMap<String, String> getHeaders() {
        return this.delegate.getHeaders();
    }

    /**
     * Get the last modified date (i.e. from a Last-Modified header).
     *
     * @return the last modified date, if present.
     */
    public Optional<Instant> getLastModified() {
        @Nullable final Date lastModified = this.delegate.getLastModified();
        return toInstant(lastModified);
    }

    public WebResourceLinkHeaders getLinks() {
        return this.delegate.getLinks();
    }

    /**
     * Get the location (i.e. from a Location header).
     *
     * @return the location, if present.
     */
    public Optional<URI> getLocation() {
        @Nullable final URI location = this.delegate.getLocation();
        return Optional.fromNullable(location);
    }


    /**
     * Get the map of response properties.
     * <p>
     * A response property is an application-defined property that may be
     * added by the user, a filter, or the handler that is managing the
     * connection.
     *
     * @return the map of response properties.
     */
    public Map<String, Object> getProperties() {
        final Map<String, Object> properties = this.delegate.getProperties();
        return Collections.unmodifiableMap(properties);
    }


    /**
     * Get response date (server side, i.e. from the Date header).
     *
     * @return the server side response date, if present.
     */
    public Optional<Instant> getResponseDate() {
        @Nullable final Date responseDate = this.delegate.getResponseDate();
        return toInstant(responseDate);
    }

    /**
     * Get the status code.
     *
     * @return the status code, if there is no mapping between the
     *         integer value and the enumeration value.
     *
     * @see #getStatusCode()
     */
    @Nullable
    public Optional<ClientResponse.Status> getStatus() {
        @Nullable final Status status = this.delegate.getClientResponseStatus();
        return Optional.fromNullable(status);
    }

    /**
     * Get the status code.
     *
     * @return the status code.
     *
     * @see #getStatus()
     */
    public int getStatusCode() {
        return this.delegate.getStatus();
    }

    /**
     * Get the media type of the response (i.e. from the Content-Type header).
     *
     * @return the media type, if present.
     */
    public Optional<MediaType> getType() {
        @Nullable final MediaType type = this.delegate.getType();
        return Optional.fromNullable(type);
    }

    /**
     * Note that HttpResponse does not consider it equal to a "corresponding"
     * {@link ClientResponse}. That would be problematic, since ClientResponse
     * doesn't even override {@link Object#equals(Object)} or
     * {@link Object#hashCode()} itself, and would also violate the principle
     * that equality should be independent of order.
     */
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
        final HttpResponse<?> other = (HttpResponse<?>) obj;
        if (getStatusCode() != other.getStatusCode()) {
            return false;
        }
        if (!getEntity().equals(other.getEntity())) {
            return false;
        }
        if (!getHeaders().equals(other.getHeaders())) {
            return false;
        }
        if (!getProperties().equals(other.getProperties())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getStatusCode();
        result = prime * result + getEntity().hashCode();
        result = prime * result + getHeaders().hashCode();
        result = prime * result + getProperties().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [statusCode=" + getStatusCode() +
                ", entity=" + getEntity() +
                ", headers=" + getHeaders() +
                ", properties=" + getProperties() +
                ']';
    }

    private HttpResponse(final ClientResponse delegate, @Nullable final ENTITY entity) {
        this.delegate = Preconditions.checkNotNull(delegate);
        this.entity = Optional.fromNullable(entity);
    }

    private static Optional<Instant> toInstant(@Nullable final Date date) {
        if (null == date) {
            return Optional.absent();
        } else {
            return Optional.of(new Instant(date.getTime()));
        }
    }

}
