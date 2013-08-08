package rickbw.crud.example;

import rickbw.crud.ReadableResource;
import rickbw.crud.ReadableResourceProvider;
import rickbw.crud.WritableResource;
import rickbw.crud.WritableResourceProvider;
import com.google.common.base.Preconditions;

import rx.Observable;


/*package*/ class ReadWritableUserResource
implements ReadableResource<User>, WritableResource<User, Boolean> {

    private final ReadableResource<User> readableDelegate;
    private final WritableResource<? super User, Boolean> writableDelegate;


    private ReadWritableUserResource(
            final ReadableResource<User> readableDelegate,
            final WritableResource<? super User, Boolean> writableDelegate) {
        this.readableDelegate = Preconditions.checkNotNull(readableDelegate);
        this.writableDelegate = Preconditions.checkNotNull(writableDelegate);
    }

    @Override
    public Observable<User> get() {
        return this.readableDelegate.get();
    }

    @Override
    public Observable<Boolean> write(final User newValue) {
        return this.writableDelegate.write(newValue);
    }

    /**
     * A convenience method.
     */
    public Observable<Boolean> refresh() {
        return write(null);
    }

    public static class Provider
    implements ReadableResourceProvider<Long, User>, WritableResourceProvider<Long, User, Boolean> {
        private final ReadableResourceProvider<? super Long, User> readableDelegate;
        private final WritableResourceProvider<? super Long, ? super User, Boolean> writableDelegate;

        public Provider(
                final ReadableResourceProvider<? super Long, User> readableDelegate,
                final WritableResourceProvider<Long, ? super User, Boolean> writableDelegate) {
            this.readableDelegate = Preconditions.checkNotNull(readableDelegate);
            this.writableDelegate = Preconditions.checkNotNull(writableDelegate);
        }

        @Override
        public ReadWritableUserResource get(final Long key) {
            final ReadableResource<User> readable = this.readableDelegate.get(key);
            final WritableResource<? super User, Boolean> writable = this.writableDelegate.get(key);
            return new ReadWritableUserResource(readable, writable);
        }
    }

}
