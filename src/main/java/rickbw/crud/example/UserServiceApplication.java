package rickbw.crud.example;

import rickbw.crud.ReadableResource;
import rickbw.crud.ReadableResourceProvider;
import com.google.common.base.Preconditions;

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
            public void onError(final Exception ex) {
                System.err.println("Failed to get user.");
            }
        });
    }

}
