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
import rx.Observer;


/**
 * Example of an application that uses the User Service.
 */
/*package*/ class UserServiceApplication {

    private final ReadableResourceProvider<Long, User> userService;


    public UserServiceApplication(
            final ReadableResourceProvider<Long, User> isThisARestServiceIDontCare) {
        this.userService = Preconditions.checkNotNull(isThisARestServiceIDontCare);
    }

    public void processUser(final long userId) {
        final ReadableResource<User> resource = this.userService.get(userId);
        resource.get().subscribe(new Observer<User>() {
            @Override
            public void onNext(final User user) {
                System.out.println("Got user: " + user);
            }

            @Override
            public void onCompleted() {
                System.out.println("Done.");
            }

            @Override
            public void onError(final Throwable ex) {
                System.err.println("Failed to get user.");
            }
        });
    }

}
