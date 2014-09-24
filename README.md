# Replay.IO Android Framework

Building this project will produce `replay-android.jar`, which can then be distributed and integrated into third-party iOS projects.

## Documentation for framework developers

### How to build the framework

The project don't depend on other projects, but you will use it as a library project. <br>
* If you are using ADT Bundle or eclipse, import ReplayIO to your workspace, open the properties window, Android tab, check "Is Library".<br>
* If you are using Android Studio, import the project by selecting build.gradle.
In your app project, in file `settings.gradle`:

```java
include ':replay-android'
```

In file `build.gradle`:
```java
dependencies {
    compile project(':replay-android')
    ...
}
```
* If you need a JAR file, run `./gradlew makeJar` in project root directory and a `replay-android.jar` will be created under `./build/libs/`

### ReplayIO

ReplayIO is the client. Initialize it with ReplayIO.init(Context context, String apiKey). The Context object passed in should be application's context to avoid memory leak.
Calling any static method that involves context before initializing ReplayIO first will throw ReplayIONotInitializedException.

### ReplaySessionManager

ReplaySessionManager is in charge of the sessions. A new session UUID should be created at the moment the app goes into foreground, and ended when the app entered background.

### ReplayAPIManager

ReplayAPIManager helps generating the requests and sending them to server.

### ReplayQueue

This class queues the URL requests created by ReplayAPIManager. All requests to be sent out are enqueued by ReplayQueue and dequeued according to the `dispatchInterval`.

| t < 0           | t = 0              | t > 0          |
|-----------------|--------------------|----------------|
| Manual dispatch | Immediate dispatch | Timer dispatch |

When dispatch is triggered, it will attempt to send off all requests in the queue synchronously. When connectivity problems prevent a request from succeeding, the dequeueing will stop.

### Tests

We have tests, write and use them!

## Documentation for framework users

### Installation

1. * With ADT:<br>
        1. Import ReplayIO to your workspace, in the properties window, Android tab, check "Is Library".
        2. In your project's properties window, Android tab, add this ReplayIO as library.
   * With Android Studio:<br>
        1. Import the project by selecting build.gradle.
        2. In your app project, set up `settings.gradle` and `build.gradle` properly.
           
2. Make sure your AndroidManifest.xml has the INTERNET permission:
    ```xml
    <uses-permission android:name="android.permission.INTERNET"/>
    ```

3. Initialized the tracker:
    ```java
    ReplayIO.init(Context context, String apiKey)
    ```

### Tracking Events
```java
ReplayIO.trackEvent(String event, Map<String,String> data)
```

### Set Alias
```java
ReplayIO.updateAlias(String alias)
```

### Debugging
```java
ReplayIO.setDebugMode(true)
```

### Enable/disable
```java
ReplayIO.enable()
ReplayIO.disable()
```

### Dispatching
By default, ReplayIO will dispatch event data as soon as `trackEvent` method is called. You can choose to dispatch data periodically as well as by modifying the dispatch interval. Specifying a negative interval will disable periodic dispatch.
```java
ReplayIO.setDispatchInterval(120); //dispatch every 2 minutes
```

```java
ReplayIO.setDispatchInterval(-1);  // disable periodic dispatch
ReplayIO.dispatchNow();            // dispatch manually
```

```java
ReplayIO.setDispatchInterval(0);   // dispatch immediately (default)
```

