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

import java.util.UUID;

import rx.Observer;


/**
 * An example of trivial business logic that deals with {@link Asset}s.
 */
class AssetApplication {

    private final AssetResourceProvider assetProvider;


    public AssetApplication(final AssetResourceProvider isThisAWebServiceIDontCare) {
        this.assetProvider = isThisAWebServiceIDontCare;
    }

    public void processAsset(final UUID assetId) {
        final AssetResource resource = this.assetProvider.get(assetId);
        resource.get().subscribe(new Observer<Asset>() {
            @Override
            public void onNext(final Asset asset) {
                System.out.println("Got the asset " + assetId);
                final Asset betterAsset = new Asset(assetId, 42L);
                resource.write(betterAsset).subscribe();
            }

            @Override
            public void onCompleted() {
                System.out.println("Done.");
            }

            @Override
            public void onError(final Throwable ex) {
                System.err.println("Failed to get the asset.");
            }
        });
    }

}
