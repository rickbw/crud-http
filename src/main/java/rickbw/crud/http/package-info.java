/**
 * This package contains implementations of the interfaces in the package
 * {@link rickbw.crud} in terms of the Jersey HTTP
 * library.
 *
 * Implementations use two {@link java.util.concurrent.Executor}s to make it
 * easier to scale data access and application logic independently.
 * <ol>
 *  <li>The {@link java.util.concurrent.ExecutorService} belonging to the REST
 *      {@link com.sun.jersey.api.client.Client} itself is used to execute
 *      HTTP requests. Increase the number of threads here based on the
 *      request volume and the network's performance.</li>
 *  <li>An {@link java.util.concurrent.Executor} provided by the application
 *      is used to execute its resource consumers. Increase the number of
 *      threads here based on the amount of computation done by the
 *      application in response to data.</li>
 * </ol>
 */
package rickbw.crud.http;
