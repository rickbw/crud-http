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
package crud.http.util;

import javax.ws.rs.core.Response;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;

import crud.fluent.FluentReadableResource;
import crud.fluent.FluentReadableResourceProvider;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;


/**
 * Allows an {@link Observer} to treat {@link ClientResponse}s that represent
 * errors intuitively, as errors, via {@link Observer#onError(Throwable)},
 * rather than as expected values, via {@link Observer#onNext(Object)}.
 * The application provides a set of statuses that it wishes to treat as
 * errors, and this class will wrap responses with those statuses into
 * {@link UniformInterfaceException} and dispatch them to {@code onError}.
 *
 * If retries are desired, it's recommended to pass an instance of this class
 * to {@code lift} before calling {@code retry}. Otherwise, the only errors
 * that will be detected and retried are those that result in exceptions from
 * Jersey.
 *
 * @see UniformInterfaceException#getResponse()
 * @see FluentReadableResource#lift(rx.Observable.Operator)
 * @see FluentReadableResourceProvider#lift(rx.Observable.Operator)
 * @see FluentReadableResource#retry(int)
 * @see FluentReadableResourceProvider#retry(int)
 */
public final class FailedResponseOperator
implements Observable.Operator<ClientResponse, ClientResponse> {

    private static final int MIN_STATUS_CODE = 100;
    private static final int MAX_STATUS_CODE = 599;
    private static final int MIN_SUCCESS_STATUS_CODE = 200;
    private static final int MAX_SUCCESS_STATUS_CODE = 299;
    private static final int MIN_SERVER_ERROR_STATUS_CODE = 500;
    private static final int MAX_SERVER_ERROR_STATUS_CODE = 599;

    private static FailedResponseOperator serverErrors = null;
    private static FailedResponseOperator nonSuccessResponses = null;

    private final ImmutableSet<Integer> failedStatuses;


    /**
     * Treat all 500-range responses as errors.
     */
    public static FailedResponseOperator serverErrors() {
        if (serverErrors == null) {
            // Don't delegate to fromStatusCodes(): it does extraneous checking
            serverErrors = new FailedResponseOperator(ContiguousSet.create(
                    Range.closed(MIN_SERVER_ERROR_STATUS_CODE, MAX_SERVER_ERROR_STATUS_CODE),
                    DiscreteDomain.integers()));
        }
        return serverErrors;
    }

    /**
     * Treat all non-200-range responses as errors.
     */
    public static FailedResponseOperator nonSuccessResponses() {
        if (nonSuccessResponses == null) {
            final ImmutableSet<Integer> prefix = ContiguousSet.create(
                    Range.closedOpen(MIN_STATUS_CODE, MIN_SUCCESS_STATUS_CODE),
                    DiscreteDomain.integers());
            final ImmutableSet<Integer> suffix = ContiguousSet.create(
                    Range.openClosed(MAX_SUCCESS_STATUS_CODE, MAX_STATUS_CODE),
                    DiscreteDomain.integers());
            final ImmutableSet<Integer> all = ImmutableSet.<Integer>builder()
                    .addAll(prefix)
                    .addAll(suffix)
                    .build();
            // Don't delegate to fromStatusCodes(): it does extraneous checking
            nonSuccessResponses = new FailedResponseOperator(all);
        }
        return nonSuccessResponses;
    }

    public static FailedResponseOperator fromClientResponseStatuses(final Iterable<ClientResponse.Status> statuses) {
        final ImmutableSet.Builder<Integer> statusCodes = new ImmutableSet.Builder<>();
        for (final ClientResponse.Status status : statuses) {
            statusCodes.add(status.getStatusCode());
        }
        // Don't delegate to fromStatusCodes(): it does extraneous checking
        return new FailedResponseOperator(statusCodes.build());
    }

    public static FailedResponseOperator fromResponseStatuses(final Iterable<Response.Status> statuses) {
        final ImmutableSet.Builder<Integer> statusCodes = new ImmutableSet.Builder<>();
        for (final Response.Status status : statuses) {
            statusCodes.add(status.getStatusCode());
        }
        // Don't delegate to fromStatusCodes(): it does extraneous checking
        return new FailedResponseOperator(statusCodes.build());
    }

    public static FailedResponseOperator fromStatusCodes(final Iterable<Integer> statuses) {
        for (final Integer status : statuses) {
            if (status < MIN_STATUS_CODE || status > MAX_STATUS_CODE) {
                throw new IllegalArgumentException("HTTP status code out of range: " + status);
            }
        }
        return new FailedResponseOperator(statuses);
    }

    private FailedResponseOperator(final Iterable<Integer> failedStatuses) {
        this.failedStatuses = ImmutableSet.copyOf(failedStatuses);
    }

    @Override
    public Subscriber<? super ClientResponse> call(final Subscriber<? super ClientResponse> sub) {
        return new ErrorResponseSubscriber(sub);
    }

    private final class ErrorResponseSubscriber extends Subscriber<ClientResponse> {
        private final Subscriber<? super ClientResponse> delegate;
        private volatile boolean errorOccurred = false;

        public ErrorResponseSubscriber(final Subscriber<? super ClientResponse> delegate) {
            super(delegate);
            this.delegate = delegate;
        }

        @Override
        public void onNext(final ClientResponse response) {
            if (this.errorOccurred) {
                return;
            }
            if (FailedResponseOperator.this.failedStatuses.contains(response.getStatus())) {
                this.errorOccurred = true;
                this.delegate.onError(new UniformInterfaceException(response));
            } else {
                this.delegate.onNext(response);
            }
        }

        @Override
        public void onCompleted() {
            if (this.errorOccurred) {
                return;
            }
            this.delegate.onCompleted();
        }

        @Override
        public void onError(final Throwable e) {
            if (this.errorOccurred) {
                return;
            }
            this.delegate.onError(e);
        }
    }

}
