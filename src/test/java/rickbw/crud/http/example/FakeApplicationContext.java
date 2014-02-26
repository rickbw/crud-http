package rickbw.crud.http.example;

import java.net.URI;

import javax.annotation.Nullable;
import javax.ws.rs.core.MediaType;

import rickbw.crud.ReadableResourceProvider;
import rickbw.crud.WritableResourceProvider;
import rickbw.crud.http.ClientConfiguration;
import rickbw.crud.http.HttpResponse;
import rickbw.crud.http.JerseyReadableResourceProvider;
import rickbw.crud.http.JerseyWritableResourceProvider;
import rickbw.crud.util.ReadableResourceProviders;
import rickbw.crud.util.WritableResourceProviders;
import com.sun.jersey.api.client.Client;

import rx.util.functions.Func1;


/*package*/ class FakeApplicationContext {

    private final Client restClientBean = new Client();

    private final ClientConfiguration restConfigBean = new ClientConfiguration();
    {
        final MediaType protoMediaType = new MediaType("application", "x-protobuf");
        this.restConfigBean.addMediaType(MediaType.APPLICATION_JSON_TYPE);
        this.restConfigBean.addMediaType(protoMediaType);
        this.restConfigBean.setType(protoMediaType);
        this.restConfigBean.addHeader("X-protobuf-message", User.class.getName());
    }

    private final Func1<Long, URI> urlBuilderBean = new Func1<Long, URI>() {
        @Override
        public URI call(@Nullable final Long input) {
            if (null == input) {
                return null;
            }
            return URI.create("http://localhost/user/" + input);
        }
    };

    private final ReadableResourceProvider<URI, HttpResponse<User>> restGetBean =
            new JerseyReadableResourceProvider<User>(
                    this.restClientBean,
                    User.class,
                    this.restConfigBean);

    private final WritableResourceProvider<URI, Object, HttpResponse<User>> restPutBean =
            new JerseyWritableResourceProvider<User>(
                    this.restClientBean,
                    User.class,
                    this.restConfigBean);

    /* With different implementations, this could be backed by a Voldemort
     * or some other source. It doesn't have to be a REST service.
     */
    private final ReadableResourceProvider<Long, User> adaptedGetBean =
            /* TODO: Provide utility method to adapt key and response in one
             * step, to avoid this nested construction.
             */
            ReadableResourceProviders.adaptKey(
                    ReadableResourceProviders.map(
                            this.restGetBean,
                            new Func1<HttpResponse<User>, User>() {
                                @Override
                                public User call(@Nullable final HttpResponse<User> input) {
                                    return input.getEntity().get();
                                }
                            }),
                    this.urlBuilderBean);

    /* With different implementations, this could be backed by a Voldemort
     * or some other source. It doesn't have to be a REST service.
     */
    private final WritableResourceProvider<Long, User, Boolean> adaptedPutBean =
            /* TODO: Provide utility method to adapt key and response in one
             * step, to avoid this nested construction.
             */
            WritableResourceProviders.adaptKey(
                    WritableResourceProviders.<URI, User, HttpResponse<User>, Boolean>map(
                            this.restPutBean,
                            new Func1<HttpResponse<User>, Boolean>() {
                                @Override
                                public Boolean call(@Nullable final HttpResponse<User> input) {
                                    return input.getStatusCode() < 300;
                                }
                            }),
                    this.urlBuilderBean);

    /* The appBean doesn't care that this ResourceProvider supports both read
     * and write operations. That combination is just included here by way of
     * illustration.
     */
    public final ReadWritableUserResource.Provider providerBean = new ReadWritableUserResource.Provider(
            this.adaptedGetBean,
            this.adaptedPutBean);

    public final UserServiceApplication appBean = new UserServiceApplication(
            this.providerBean);

}
