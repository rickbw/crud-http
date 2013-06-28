package rickbw.crud.http;

import rickbw.crud.sync.SyncDeletableResource;
import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterface;
import com.sun.jersey.api.client.UniformInterfaceException;


public final class JerseyDeletableResource<RESPONSE>
implements SyncDeletableResource<HttpResponse<RESPONSE>> {

    private final UniformInterface resource;
    private final Class<? extends RESPONSE> responseClass;


    public JerseyDeletableResource(
            final UniformInterface resource,
            final Class<? extends RESPONSE> responseClass) {
        this.resource = Preconditions.checkNotNull(resource);
        this.responseClass = Preconditions.checkNotNull(responseClass);
    }

    @Override
    public HttpResponse<RESPONSE> deleteSync() throws UniformInterfaceIOException, ClientHandlerIOException {
        try {
            final ClientResponse response = this.resource.delete(ClientResponse.class);
            return HttpResponse.wrapAndClose(response, this.responseClass);
        } catch (final UniformInterfaceException uix) {
            throw new UniformInterfaceIOException(uix);
        } catch (final ClientHandlerException chx) {
            throw new ClientHandlerIOException(chx);
        }
    }

}
