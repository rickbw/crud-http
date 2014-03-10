/* Copyright 2013–2014 Rick Warren
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

package rickbw.crud.http.example;

import com.google.common.base.Preconditions;

import rickbw.crud.ReadableResource;
import rickbw.crud.ReadableResourceProvider;
import rickbw.crud.WritableResource;
import rickbw.crud.WritableResourceProvider;
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

    // TODO: override equals() and hashCode()

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
