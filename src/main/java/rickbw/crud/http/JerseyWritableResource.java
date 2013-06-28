package rickbw.crud.http;

import rickbw.crud.sync.SyncWritableResource;
import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterface;
import com.sun.jersey.api.client.UniformInterfaceException;


public final class JerseyWritableResource<RESPONSE>
implements SyncWritableResource<Object, HttpResponse<RESPONSE>> {

    private final UniformInterface resource;
    private final Class<? extends RESPONSE> responseClass;


    public JerseyWritableResource(
            final UniformInterface resource,
            final Class<? extends RESPONSE> responseClass) {
        this.resource = Preconditions.checkNotNull(resource);
        this.responseClass = Preconditions.checkNotNull(responseClass);
    }

    @Override
    public HttpResponse<RESPONSE> writeSync(final Object update) throws UniformInterfaceIOException, ClientHandlerIOException {
        try {
            final ClientResponse response = this.resource.put(ClientResponse.class, update);
            return HttpResponse.wrapAndClose(response, this.responseClass);
        } catch (final UniformInterfaceException uix) {
            throw new UniformInterfaceIOException(uix);
        } catch (final ClientHandlerException chx) {
            throw new ClientHandlerIOException(chx);
        }
    }

}
