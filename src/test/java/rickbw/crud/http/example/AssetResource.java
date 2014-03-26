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

import rickbw.crud.ReadableResource;
import rickbw.crud.Resource;
import rickbw.crud.WritableResource;


/**
 * A {@link Resource} that encapsulates the reading and writing of a
 * particular {@link Asset}. Assets do not support partial updates or
 * deletion.
 */
interface AssetResource extends ReadableResource<Asset>, WritableResource<Asset, Boolean> {
    // empty
}