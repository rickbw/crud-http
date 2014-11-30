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
package crud.http.example;

import java.util.UUID;

import crud.rsrc.Gettable;
import crud.rsrc.GettableSet;
import crud.rsrc.Settable;
import crud.rsrc.SettableSet;
import crud.spi.GettableSetSpec;
import crud.spi.GettableSpec;
import crud.spi.Resource;
import crud.spi.ResourceSet;
import crud.spi.SettableSetSpec;
import crud.spi.SettableSpec;
import rx.Observable;
import rx.functions.Func1;


/**
 * A {@link ResourceSet} for retrieving readable and writable
 * {@link Asset}s, encapsulated by {@link AssetResource}.
 */
class AssetSet
implements GettableSetSpec<UUID, Asset>, SettableSetSpec<UUID, Asset, Boolean> {

    private final GettableSetSpec<UUID, Asset> readDelegate;
    private final SettableSetSpec<UUID, Asset, Boolean> writeDelegate;


    /**
     * Wrap a pair of {@link ResourceSet}s with a new
     * AssetSet. These input providers might be, for example, a
     * {@link crud.http.HttpResourceProvider}, if the Assets are to
     * be backed by a web service. However, any backing providers will do,
     * provided there is some way to transform their inputs and outputs
     * appropriately.
     *
     * @param <K>   The type of the keys of the input providers.
     * @param <RV>  The type of the values read from the read delegate.
     * @param <WV>  The type of the values written by the write delegate.
     * @param <R>   The type of the response from the write delegate.
     */
    public static <K, RV, WV, R> AssetSet create(
            final GettableSetSpec<K, RV> readDelegate,
            final SettableSetSpec<K, WV, R> writeDelegate,
            final Func1<? super UUID, ? extends K> keyAdapter,
            final Func1<? super RV, ? extends Asset> assetReadMapper,
            final Func1<? super Asset, ? extends WV> assetWriteMapper,
            final Func1<? super R, ? extends Boolean> responseMapper) {
        final GettableSet<UUID, Asset> reader
                = GettableSet.from(readDelegate)
                    .adaptKey(keyAdapter)
                    .mapValue(new Func1<Observable<RV>, Observable<Asset>>() {
                        @Override
                        public Observable<Asset> call(final Observable<RV> value) {
                            return value.map(assetReadMapper);
                        }
                    });
        final SettableSet<UUID, Asset, Boolean> writer
                = SettableSet.from(writeDelegate)
                    .adaptKey(keyAdapter)
                    .adaptNewValue(new Func1<Observable<Asset>, Observable<WV>>() {
                        @Override
                        public Observable<WV> call(final Observable<Asset> asset) {
                            return asset.map(assetWriteMapper);
                        }
                    })
                    .mapResponse(new Func1<Observable<R>, Observable<Boolean>>() {
                        @Override
                        public Observable<Boolean> call(final Observable<R> t1) {
                            return t1.map(responseMapper);
                        }
                    });
        return new AssetSet(reader, writer);
    }

    @Override
    public Settable<Asset, Boolean> setter(final UUID key) {
        return Settable.from(create(key));
    }

    @Override
    public Gettable<Asset> getter(final UUID key) {
        return Gettable.from(create(key));
    }

    /**
     * @return  a {@link Resource} encapsulating an {@link Asset} with the
     *          given ID. The state of that Asset may be read or written using
     *          that Resource.
     */
    private AssetResource create(final UUID assetId) {
        final GettableSpec<Asset> readRsrc = this.readDelegate.getter(assetId);
        final SettableSpec<Asset, Boolean> writeRsrc = this.writeDelegate.setter(assetId);
        return new AssetResourceImpl(writeRsrc, readRsrc);
    }

    private AssetSet(
            final GettableSetSpec<UUID, Asset> readDelegate,
            final SettableSetSpec<UUID, Asset, Boolean> writeDelegate) {
        this.readDelegate = readDelegate;
        this.writeDelegate = writeDelegate;
    }


    private static final class AssetResourceImpl implements AssetResource {
        private final SettableSpec<Asset, Boolean> writeRsrc;
        private final GettableSpec<Asset> readRsrc;

        private AssetResourceImpl(
                final SettableSpec<Asset, Boolean> writeRsrc,
                final GettableSpec<Asset> readRsrc) {
            this.writeRsrc = writeRsrc;
            this.readRsrc = readRsrc;
        }

        @Override
        public Observable<Asset> get() {
            return this.readRsrc.get();
        }

        @Override
        public Observable<Boolean> set(final Observable<Asset> newValue) {
            return this.writeRsrc.set(newValue);
        }

        // Every concrete Resource class should override equals() and hashCode().
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final AssetResourceImpl other = (AssetResourceImpl) obj;
            return this.readRsrc.equals(other.readRsrc)
                && this.writeRsrc.equals(other.writeRsrc);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + this.readRsrc.hashCode();
            result = prime * result + this.writeRsrc.hashCode();
            return result;
        }
    }

}
