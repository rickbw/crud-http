Crud HTTP
=========

This project provides an implementation of the [Crud API](https://github.com/rickbw/crud-api) for HTTP, based on [Jersey](https://jersey.java.net).

The four primary HTTP methods are all supported:
* `GET`: [JerseyReadableResource](https://github.com/rickbw/crud-http/blob/master/src/main/java/rickbw/crud/http/JerseyReadableResource.java), a [ReadableResource](https://github.com/rickbw/crud-api/blob/master/src/main/java/rickbw/crud/ReadableResource.java)
* `PUT`: [JerseyWritableResource](https://github.com/rickbw/crud-http/blob/master/src/main/java/rickbw/crud/http/JerseyWritableResource.java), a [WritableResource](https://github.com/rickbw/crud-api/blob/master/src/main/java/rickbw/crud/WritableResource.java)
* `POST`: [JerseyUpdatableResource](https://github.com/rickbw/crud-http/blob/master/src/main/java/rickbw/crud/http/JerseyUpdatableResource.java), an [UpdatableResource](https://github.com/rickbw/crud-api/blob/master/src/main/java/rickbw/crud/UpdatableResource.java)
* `DELETE`: [JerseyDeletableResource](https://github.com/rickbw/crud-http/blob/master/src/main/java/rickbw/crud/http/JerseyDeletableResource.java), a [DeletableResource](https://github.com/rickbw/crud-api/blob/master/src/main/java/rickbw/crud/DeletableResource.java)

Most applications will not use these `Resource` implementation classes directly. Instead, they will start with the corresponding `ResourceProviders`, which implement URI-based lookup of particular `Resources`. For example, [JerseyReadableResourceProvider](https://github.com/rickbw/crud-http/blob/master/src/main/java/rickbw/crud/http/JerseyReadableResourceProvider.java) provides instances of `JerseyReadableResource` on demand.


Copyright and License
---------------------
All files in this project are copyright Rick Warren and, unless otherwise
noted, licensed under the terms of the Apache 2 license.
