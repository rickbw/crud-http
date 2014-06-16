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


/**
 * An example of an application-specific data type.
 */
class Asset {

    private final UUID id;
    private final long answerToLifeTheUniverseAndEverything;


    public Asset(final UUID id, final long answerToLifeTheUniverseAndEverything) {
        this.id = id;
        this.answerToLifeTheUniverseAndEverything = answerToLifeTheUniverseAndEverything;
    }

    public UUID getAssetId() {
        return this.id;
    }

    public long getTheAnswerToLifeTheUniverseAndEverything() {
        return this.answerToLifeTheUniverseAndEverything;
    }

}
