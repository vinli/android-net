Vinli Android Net SDK
=====================

An Android client for interacting with the Vinli backend from within your application. [Sample App](https://github.com/vinli/android-net-demo)

Download
--------

You can also depend on this library through Gradle from jcenter:
```groovy
compile 'li.vin:android-net:1.0.0-beta.5'
```

Conventions
-----------
### [RxJava](https://github.com/ReactiveX/RxJava/wiki)
The developer interfaces with the SDK using reactive Observables and Subscriptions.
All data from the device is streamed via Observable, and the data stream is stopped by unsubscribing from the subscription.
All models are immutable.

Docs
----

### [JavaDocs](http://vinli.github.io/android-net/)
