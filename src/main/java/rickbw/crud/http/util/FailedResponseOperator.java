package rickbw.crud.http.util;

import javax.ws.rs.core.Response;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;

import rickbw.crud.util.FluentReadableResource;
import rickbw.crud.util.FluentReadableResourceProvider;
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

    private static FailedResponseOperator serverErrors = null;

    private final ImmutableSet<Integer> failedStatuses;


    /**
     * Treat all 500-range responses as errors.
     */
    public static FailedResponseOperator serverErrors() {
        if (serverErrors == null) {
            // Don't delegate to fromStatusCodes(): it does extraneous checking
            serverErrors = new FailedResponseOperator(ContiguousSet.create(
                    Range.closed(500, 599),
                    DiscreteDomain.integers()));
        }
        return serverErrors;
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

        private ErrorResponseSubscriber(final Subscriber<? super ClientResponse> delegate) {
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
