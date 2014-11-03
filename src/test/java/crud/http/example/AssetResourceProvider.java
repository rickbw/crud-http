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

import crud.rsrc.ReadableProvider;
import crud.rsrc.WritableProvider;
import crud.spi.ReadableProviderSpec;
import crud.spi.ReadableSpec;
import crud.spi.Resource;
import crud.spi.ResourceProviderSpec;
import crud.spi.WritableProviderSpec;
import crud.spi.WritableSpec;
import rx.Observable;
import rx.functions.Func1;


/**
 * A {@link ResourceProviderSpec} for retrieving readable and writable
 * {@link Asset}s, encapsulated by {@link AssetResource}.
 */
class AssetResourceProvider
implements ReadableProviderSpec<UUID, Asset>, WritableProviderSpec<UUID, Asset, Boolean> {

    private final ReadableProviderSpec<UUID, Asset> readDelegate;
    private final WritableProviderSpec<UUID, Asset, Boolean> writeDelegate;


    /**
     * Wrap a pair of {@link ResourceProviderSpec}s with a new
     * AssetResourceProvider. These input providers might be, for example, a
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
    public static <K, RV, WV, R> AssetResourceProvider create(
            final ReadableProviderSpec<K, RV> readDelegate,
            final WritableProviderSpec<K, WV, R> writeDelegate,
            final Func1<? super UUID, ? extends K> keyAdapter,
            final Func1<? super RV, ? extends Asset> assetReadMapper,
            final Func1<? super Asset, ? extends WV> assetWriteMapper,
            final Func1<? super R, ? extends Boolean> responseMapper) {
        final ReadableProvider<UUID, Asset> reader
                = ReadableProvider.from(readDelegate)
                    .adaptKey(keyAdapter)
                    .mapValue(assetReadMapper);
        final WritableProvider<UUID, Asset, Boolean> writer
                = WritableProvider.from(writeDelegate)
                    .adaptKey(keyAdapter)
                    .adaptNewValue(assetWriteMapper)
                    .mapResponse(responseMapper);
        return new AssetResourceProvider(reader, writer);
    }

    @Override
    public AssetResource writer(final UUID key) {
        return create(key);
    }

    @Override
    public AssetResource reader(final UUID key) {
        return create(key);
    }

    /**
     * @return  a {@link Resource} encapsulating an {@link Asset} with the
     *          given ID. The state of that Asset may be read or written using
     *          that Resource.
     */
    private AssetResource create(final UUID assetId) {
        final ReadableSpec<Asset> readRsrc = this.readDelegate.reader(assetId);
        final WritableSpec<Asset, Boolean> writeRsrc = this.writeDelegate.writer(assetId);
        return new AssetResourceImpl(writeRsrc, readRsrc);
    }

    private AssetResourceProvider(
            final ReadableProviderSpec<UUID, Asset> readDelegate,
            final WritableProviderSpec<UUID, Asset, Boolean> writeDelegate) {
        this.readDelegate = readDelegate;
        this.writeDelegate = writeDelegate;
    }


    private static final class AssetResourceImpl implements AssetResource {
        private final WritableSpec<Asset, Boolean> writeRsrc;
        private final ReadableSpec<Asset> readRsrc;

        private AssetResourceImpl(
                final WritableSpec<Asset, Boolean> writeRsrc,
                final ReadableSpec<Asset> readRsrc) {
            this.writeRsrc = writeRsrc;
            this.readRsrc = readRsrc;
        }

        @Override
        public Observable<Asset> get() {
            return this.readRsrc.get();
        }

        @Override
        public Observable<Boolean> write(final Asset newValue) {
            return this.writeRsrc.write(newValue);
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
