/**
 * This package contains implementations of the interfaces in the package
 * {@link crud.spi} in terms of the Jersey HTTP library.
 * Resources are asynchronous, and use the
 * {@link java.util.concurrent.ExecutorService} from the
 * {@link com.sun.jersey.api.client.Client} itself.
 */
package crud.http;
