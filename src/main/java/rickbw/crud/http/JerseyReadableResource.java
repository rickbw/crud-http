package rickbw.crud.http;

import rickbw.crud.sync.SyncReadableResource;
import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterface;
import com.sun.jersey.api.client.UniformInterfaceException;


public final class JerseyReadableResource<RSRC>
implements SyncReadableResource<HttpResponse<RSRC>> {

    private final UniformInterface resource;
    private final Class<? extends RSRC> resourceClass;


    public JerseyReadableResource(
            final UniformInterface resource,
            final Class<? extends RSRC> resourceClass) {
        this.resource = Preconditions.checkNotNull(resource);
        this.resourceClass = Preconditions.checkNotNull(resourceClass);
    }

    @Override
    public HttpResponse<RSRC> getSync() throws UniformInterfaceIOException, ClientHandlerIOException {
        try {
            final ClientResponse response = this.resource.get(ClientResponse.class);
            return HttpResponse.wrapAndClose(response, this.resourceClass);
        } catch (final UniformInterfaceException uix) {
            throw new UniformInterfaceIOException(uix);
        } catch (final ClientHandlerException chx) {
            throw new ClientHandlerIOException(chx);
        }
    }

}
