/* Copyright 2014 Rick Warren
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

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import com.sun.jersey.api.client.AsyncWebResource;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.async.ITypeListener;
import com.sun.jersey.core.header.InBoundHeaders;

import rickbw.crud.WritableResourceTest;


public class HttpWritableResourceTest extends WritableResourceTest<ClientRequest, ClientResponse> {

    private final AsyncWebResource mockResource = mock(AsyncWebResource.class);
    private final AsyncWebResource.Builder mockResourceBuilder = mock(AsyncWebResource.Builder.class);

    /**
     * Keep the reference to the {@link ClientResponse} object as an
     * instance variable, and return the same one every time, because
     * {@code ClientResponse} doesn't implement {@code equals()}.
     */
    private final ClientResponse expectedResponse = createResponse();


    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(this.mockResource.getRequestBuilder()).thenReturn(this.mockResourceBuilder);
        when(this.mockResourceBuilder.put(Matchers.any(ITypeListener.class))).thenAnswer(invokeListener());
    }

    @Test
    public void subscribeCallsMocks() {
        // given:
        final HttpResource resource = createDefaultResource();
        final ClientRequest newValue = createDefaultResourceState();

        // when:
        final ClientResponse response = resource.write(newValue).toBlockingObservable().single();

        // then:
        assertSame(this.expectedResponse, response);
        verify(this.mockResource).getRequestBuilder();
    }

    @Test
    public void clientRequestsCopied() {
        // given:
        final ClientRequest mockRequestTemplate = createDefaultResourceState();
        final ClientRequest mockRequest = createDefaultResourceState();
        final HttpResource resource = new HttpResource(this.mockResource, mockRequestTemplate);

        // when:
        resource.write(mockRequest).subscribe();

        // then:
        verify(mockRequestTemplate).updateResource(this.mockResourceBuilder);
        verify(mockRequest).updateResource(this.mockResourceBuilder);
    }

    @Override
    protected HttpResource createDefaultResource() {
        return new HttpResource(this.mockResource, ClientRequest.empty());
    }

    @Override
    protected ClientRequest createDefaultResourceState() {
        return mock(ClientRequest.class);
    }

    private static ClientResponse createResponse() {
        return new ClientResponse(
                200,
                new InBoundHeaders(),
                mock(InputStream.class),
                null);
    }

    /**
     * Invoke the passed-in {@link ITypeListener}, as a real
     * {@link AsyncWebResource} would do.
     */
    private ListenerInvokingAnswer invokeListener() {
        return new ListenerInvokingAnswer(this.expectedResponse);
    }

}
