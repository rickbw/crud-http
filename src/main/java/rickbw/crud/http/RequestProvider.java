package rickbw.crud.http;

import java.net.URI;
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

}
