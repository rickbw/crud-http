package rickbw.crud.http;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.core.MediaType;

import rickbw.crud.sync.SyncMapResourceDeleter;
import rickbw.crud.sync.SyncMapResourceProvider;
import rickbw.crud.sync.SyncMapResourceSetter;
import rickbw.crud.sync.SyncMapResourceUpdater;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.RequestBuilder;
import com.sun.jersey.api.client.UniformInterface;
import com.sun.jersey.api.client.UniformInterfaceException;


public final class SyncJerseyMapClient
implements SyncMapResourceProvider<URI, ClientResponse>,
           SyncMapResourceDeleter<URI, ClientResponse>,
           SyncMapResourceSetter<URI, Object, ClientResponse>,
           SyncMapResourceUpdater<URI, Object, ClientResponse> {

    private final Client restClient;
    private final Optional<MediaType> type;
    private final ImmutableMap<String, Object> headers;

    /**
     * Keeping this as an array is less safe than using an {@link ImmutableSet},
     * but since we need it as an array for every request, the performance is
     * better if we just do the transformation to an array once, up front.
     */
    private final MediaType[] mediaTypes;


    public SyncJerseyMapClient(final Client restClient, final Configuration config) {
        this.restClient = Preconditions.checkNotNull(restClient);

        this.type = Optional.fromNullable(config.getType());
        this.headers = ImmutableMap.copyOf(config.getHeaders());

        final Collection<MediaType> mediaTypesSet = config.getMediaTypes();
        this.mediaTypes = mediaTypesSet.toArray(new MediaType[mediaTypesSet.size()]);
    }

    @Override
    public ClientResponse getSync(final URI uri) throws UniformInterfaceIOException, ClientHandlerIOException {
        final UniformInterface webResource = getResource(uri);
        try {
            final ClientResponse response = webResource.get(ClientResponse.class);
            return response;
        } catch (final UniformInterfaceException uix) {
            throw new UniformInterfaceIOException(uix);
        } catch (final ClientHandlerException chx) {
            throw new ClientHandlerIOException(chx);
        }
    }

    @Override
    public ClientResponse deleteSync(final URI uri) throws UniformInterfaceIOException, ClientHandlerIOException {
        final UniformInterface webResource = getResource(uri);
        try {
            final ClientResponse response = webResource.delete(ClientResponse.class);
            return response;
        } catch (final UniformInterfaceException uix) {
            throw new UniformInterfaceIOException(uix);
        } catch (final ClientHandlerException chx) {
            throw new ClientHandlerIOException(chx);
        }
    }

    @Override
    public ClientResponse putSync(final URI uri, final Object update) throws UniformInterfaceIOException, ClientHandlerIOException {
        final UniformInterface webResource = getResource(uri);
        try {
            final ClientResponse response = webResource.put(ClientResponse.class, update);
            return response;
        } catch (final UniformInterfaceException uix) {
            throw new UniformInterfaceIOException(uix);
        } catch (final ClientHandlerException chx) {
            throw new ClientHandlerIOException(chx);
        }
    }

    @Override
    public ClientResponse updateSync(final URI uri, final Object update) throws UniformInterfaceIOException, ClientHandlerIOException {
        final UniformInterface webResource = getResource(uri);
        try {
            final ClientResponse response = webResource.post(ClientResponse.class, update);
            return response;
        } catch (final UniformInterfaceException uix) {
            throw new UniformInterfaceIOException(uix);
        } catch (final ClientHandlerException chx) {
            throw new ClientHandlerIOException(chx);
        }
    }

    @Override
    public final void close(final ClientResponse response) throws ClientHandlerIOException {
        try {
            response.close();
        } catch (final ClientHandlerException chx) {
            throw new ClientHandlerIOException(chx);
        }
    }

    private UniformInterface getResource(final URI uri) {
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


    public static final class Configuration {
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

}
