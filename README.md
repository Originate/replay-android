# Replay.IO Android Framework

Building this project will produce `replay-android.jar`, which can then be distributed and integrated into third-party iOS projects.

## Documentation for framework developers

### How to build the framework

The project don't depend on other projects, but you will use it as a library project, so after import ReplayIO to your workspace, open the properties window, Android tab, check "Is Library".

### ReplayIO

ReplayIO is the client. Initialize it with ReplayIO.init(Context context, String apiKey). The context object passed in should be ApplicationContext to avoid memory leak.

### ReplaySessionManager

ReplaySessionManager is in charge of the sessions. A new should be created at the moment the app comes up to foreground, and ended when the app entered background.

### ReplayAPIManager

ReplayAPIManager help dealing with the event requests sending to server.

### ReplayQueue

This class queues the URL requests created by ReplayAPIManager. All requests to be sent out are enqueued by ReplayQueue and dequeued according to the `dispatchInterval`.

| t < 0           | t = 0              | t > 0          |
|-----------------|--------------------|----------------|
| Manual dispatch | Immediate dispatch | Timer dispatch |

When dispatch is triggered, it will attempt to send off all requests in the queue synchronously. When connectivity problems prevent a request from succeeding, the dequeueing will still going,

### Tests

We have tests, write and use them!

## Documentation for framework users

### Installation

1. Import ReplayIO to your workspace, in the properties window, Android tab, check "Is Library".
2. In your project's properties window, Android tab, add this ReplayIO as library.
3. Make sure your AndroidManifest.xml has the INTERNET permission:
```xml
<uses-permission android:name="android.permission.INTERNET"/>
```
4. Initialized the tracker
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
ReplayIO.setDispatchInterval(0);   // dispatch immedially (default)
```

