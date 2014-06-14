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

import java.util.concurrent.Future;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.async.ITypeListener;


public final class ListenerInvokingAnswer implements Answer<Future<ClientResponse>> {

    private final ListenableFuture<ClientResponse> mockFuture;


    public ListenerInvokingAnswer(final ClientResponse response) {
        this(Futures.immediateFuture(response));
    }

    public ListenerInvokingAnswer(final Throwable ex) {
        this(Futures.<ClientResponse>immediateFailedFuture(ex));
    }

    private ListenerInvokingAnswer(final ListenableFuture<ClientResponse> future) {
        this.mockFuture = future;
    }

    @Override
    public Future<ClientResponse> answer(final InvocationOnMock invocation) {
        @SuppressWarnings("unchecked")
        final ITypeListener<ClientResponse> listener = (ITypeListener<ClientResponse>) invocation.getArguments()[0];

        Futures.addCallback(this.mockFuture, new FutureCallback<ClientResponse>() {
            @Override
            public void onSuccess(final ClientResponse result) {
                try {
                    listener.onComplete(ListenerInvokingAnswer.this.mockFuture);
                } catch (final InterruptedException ex) {
                    Throwables.propagate(ex);
                }
            }

            @Override
            public void onFailure(final Throwable t) {
                try {
                    listener.onComplete(ListenerInvokingAnswer.this.mockFuture);
                } catch (final InterruptedException ex) {
                    Throwables.propagate(ex);
                }
            }
        });
        return this.mockFuture;
    }

}
