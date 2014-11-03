Crud HTTP
=========

This project provides an implementation of the [Crud API](https://github.com/rickbw/crud-api) for HTTP, based on [Jersey](https://jersey.java.net).

The four primary HTTP methods are all supported:
* `GET`: [JerseyReadableResource](https://github.com/rickbw/crud-http/blob/master/src/main/java/rickbw/crud/http/JerseyReadableResource.java), a [ReadableResource](https://github.com/rickbw/crud-api/blob/master/src/main/java/rickbw/crud/spi/ReadableResource.java)
* `PUT`: [JerseyWritableResource](https://github.com/rickbw/crud-http/blob/master/src/main/java/rickbw/crud/http/JerseyWritableResource.java), a [WritableResource](https://github.com/rickbw/crud-api/blob/master/src/main/java/rickbw/crud/spi/WritableResource.java)
* `POST`: [JerseyUpdatableResource](https://github.com/rickbw/crud-http/blob/master/src/main/java/rickbw/crud/http/JerseyUpdatableResource.java), an [UpdatableResource](https://github.com/rickbw/crud-api/blob/master/src/main/java/rickbw/crud/spi/UpdatableResource.java)
* `DELETE`: [JerseyDeletableResource](https://github.com/rickbw/crud-http/blob/master/src/main/java/rickbw/crud/http/JerseyDeletableResource.java), a [DeletableResource](https://github.com/rickbw/crud-api/blob/master/src/main/java/rickbw/crud/spi/DeletableResource.java)

Most applications will not use these `Resource` implementation classes directly. Instead, they will start with the corresponding `ResourceProviders`, which implement URI-based lookup of particular `Resources`. For example, [JerseyReadableResourceProvider](https://github.com/rickbw/crud-http/blob/master/src/main/java/rickbw/crud/http/JerseyReadableResourceProvider.java) provides instances of `JerseyReadableResource` on demand.


See Also
--------
* The [Crud API](https://github.com/rickbw/crud-api) project (`crud-api`) defines the core abstractions and the public API on which this project is based.
* `crud-api` is built on top of [RxJava](https://github.com/Netflix/RxJava/).
* The Crud HTTP implementation is based on [Jersey](https://jersey.java.net), a widely used REST toolkit, and the reference implementation of JAX-RS.
* [Crud JDBC](https://github.com/rickbw/crud-jdbc) (`crud-jdbc`) is a sister project to this project, implemented for JDBC instead of HTTP.
* [Crud Voldemort](https://github.com/rickbw/crud-voldemort) (`crud-voldemort`) is a sister project to this project, implemented for [Project Voldemort](http://www.project-voldemort.com) instead of HTTP.


Copyright and License
---------------------
All files in this project are copyright Rick Warren and, unless otherwise noted, licensed under the terms of the Apache 2 license.
