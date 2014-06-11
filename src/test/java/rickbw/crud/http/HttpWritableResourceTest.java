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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rickbw.crud.RxAssertions.subscribeAndWait;
import static rickbw.crud.RxAssertions.subscribeWithOnCompletedAndOnErrorFailures;
import static rickbw.crud.RxAssertions.subscribeWithOnCompletedFailure;
import static rickbw.crud.RxAssertions.subscribeWithOnNextAndOnErrorFailures;
import static rickbw.crud.RxAssertions.subscribeWithOnNextFailure;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import com.sun.jersey.api.client.AsyncWebResource;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.async.ITypeListener;
import com.sun.jersey.core.header.InBoundHeaders;

import rickbw.crud.WritableResourceTest;
import rx.Observer;


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
        final ClientResponse response = resource.write(newValue).toBlocking().single();

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

    @Test
    @SuppressWarnings("unchecked")
    public void httpPutErrorCallsOnError() throws InterruptedException {
        // given:
        final RuntimeException expectedException = new IllegalStateException("mock failure");
        final HttpResource resource = createDefaultResource();
        final ClientRequest newState = createDefaultResourceState();

        final AtomicBoolean failed = new AtomicBoolean();

        reset(this.mockResourceBuilder);

        // when:
        when(this.mockResourceBuilder.put(any(ITypeListener.class))).thenThrow(expectedException);
        subscribeAndWait(resource.write(newState), 1, new Observer<ClientResponse>() {
            @Override
            public void onNext(final ClientResponse response) {
                failed.set(true);
            }

            @Override
            public void onCompleted() {
                failed.set(true);
            }

            @Override
            public void onError(final Throwable e) {
                failed.set(!e.equals(expectedException));
            }
        });

        // then:
        assertFalse(failed.get());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void futureGetErrorCallsOnError() throws InterruptedException {
        // given:
        final RuntimeException expectedException = new IllegalStateException("mock exception");
        final HttpResource resource = createDefaultResource();
        final ClientRequest newState = createDefaultResourceState();

        final AtomicReference<String> failure = new AtomicReference<>("");

        reset(this.mockResourceBuilder);

        // when:
        when(this.mockResourceBuilder.put(any(ITypeListener.class)))
            .thenAnswer(new ListenerInvokingAnswer(expectedException));
        subscribeAndWait(resource.write(newState), 1, new Observer<ClientResponse>() {
            @Override
            public void onNext(final ClientResponse response) {
                failure.set("onNext called");
            }

            @Override
            public void onCompleted() {
                failure.set("onCompleted called");
            }

            @Override
            public void onError(final Throwable e) {
                // Check cause, because it got wrapped in ExecutionException.
                if (!expectedException.equals(e.getCause())) {
                    failure.set("Unexpected exception: " + e);
                }
            }
        });

        // then:
        assertEquals(failure.get(), "", failure.get());
    }

    @Test
    public void observerOnNextErrorClosesResponse() throws InterruptedException {
        // given:
        final HttpResource resource = createDefaultResource();
        final ClientResponse mockResponse = mock(ClientResponse.class);
        final ClientRequest newState = createDefaultResourceState();

        // when:
        whenResourceWriteThenReturn(mockResponse);
        subscribeWithOnNextFailure(resource.write(newState));

        // then:
        verify(mockResponse).close();
    }

    @Test
    public void observerOnCompletedErrorClosesResponse() throws InterruptedException {
        // given:
        final HttpResource resource = createDefaultResource();
        final ClientResponse mockResponse = mock(ClientResponse.class);
        final ClientRequest newState = createDefaultResourceState();

        // when:
        whenResourceWriteThenReturn(mockResponse);
        subscribeWithOnCompletedFailure(resource.write(newState));

        // then:
        verify(mockResponse).close();
    }

    @Test
    public void observerOnNextAndOnErrorErrorsClosesResponse() throws InterruptedException {
        // given:
        final HttpResource resource = createDefaultResource();
        final ClientResponse mockResponse = mock(ClientResponse.class);
        final ClientRequest newState = createDefaultResourceState();

        // when:
        whenResourceWriteThenReturn(mockResponse);
        subscribeWithOnNextAndOnErrorFailures(resource.write(newState));

        // then:
        verify(mockResponse).close();
    }

    @Test
    public void observerOnCompletedAndOnErrorErrorsClosesResponse() throws InterruptedException {
        // given:
        final HttpResource resource = createDefaultResource();
        final ClientResponse mockResponse = mock(ClientResponse.class);
        final ClientRequest newState = createDefaultResourceState();

        // when:
        whenResourceWriteThenReturn(mockResponse);
        subscribeWithOnCompletedAndOnErrorFailures(resource.write(newState));

        // then:
        verify(mockResponse).close();
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

    @SuppressWarnings("unchecked")
    private void whenResourceWriteThenReturn(final ClientResponse mockResponse) {
        reset(this.mockResourceBuilder);
        when(this.mockResourceBuilder.put(any(ITypeListener.class)))
            .thenAnswer(new ListenerInvokingAnswer(mockResponse));
    }

}
