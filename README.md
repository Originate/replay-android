[![Circle CI](https://circleci.com/gh/Originate/replay-android.png?style=badge)](https://circleci.com/gh/Originate/replay-android)

# Replay.IO Android Framework

Building this project will produce `replay-android.jar`, which can then be distributed and integrated into third-party Android projects.

### How to build the framework

Replay.io-Android depends on [path/android-priority-jobqueue](https://github.com/path/android-priority-jobqueue). However, currently, we are using a custom-modified version. 
*//TODO: add diff of path/jobqueue-master and our master*

- If you are using ADT Bundle or Eclipse, import ReplayIO to your workspace, open the properties window, Android tab, check "Is Library".<br>
- If you are using Android Studio, import the project by selecting build.gradle.
In your app project, in file `settings.gradle`:

```gradle
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

###Setup

You can get up and running with the Replay Android SDK in just a few quick steps:

####Step 1 - Android Manifest
Ensure your `AndroidManifest.xml` has the following items:
    ```xml
     <!-- Required for internet. -->
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    ```

####Step 2 - Configuration XML
Add the configuration XML, named `replay_io.xml` to your application/s `res/values` folder:
    ```xml
    <?xml version="1.0" encoding="utf-8"?>
    <resources>
        //the interval between when events are dispatched to the server - OPTIONAL
        <integer name="dispatch_interval">0</integer>
    
        //set true to enable event tracking
        <string name="enabled">false</string> //OPTIONAL
    
        //set true to print debug messages
        <string name="debug_mode_enabled">false</string> //OPTIONAL
    
        //If the number of events stored (not yet dispatched) reaches this value, no more events will be received
        <integer name="max_queue">1200</integer> //OPTIONAL
    
        //Normally events are only sent to the server when the dispatch_interval is met
        //but if the number of events reaches flush_at, they will be automatically sent
        <integer name="flush_at">50</integer> //OPTIONAL
    
        //A string to identify app user (this isn't required, because we'll assign users a random id either way)
        <string name="distinct_id">""</string> //OPTIONAL
    
        //replay api key
        <string name="api_key">API_KEY_HERE</string> //*NOT* OPTIONAL
    </resources>
    ```

####Step 3 - Hook into Android App Lifecycle
You have 3 choices for this:

1. If your app's `minSDKVersion` is 14 or greater (i.e., if your app supports a *minimum* of Ice Cream Sandwich), you have to do **nothing**. <br>
    That's right! Replay.io will handle lifecycle events for you!

2. If you support a minimum SDK version *lower* than 14, you can extend `ReplayActivity` like so:
    ```java
    import io.replay.framework.ReplayActivity;
    
    public class ExampleActivity0 extends ReplayActivity { /*...*/ }
    ```

3. If you already have a parent activity that you inherit from (e.g., you use ActionBarSherlock), you can manually add the lifecycle tracking events to any Activity that you'd like us to track:
    ```java
    import io.replay.framework.ReplayIO;
    
    public class ExampleActivity1 extends Activity {
    
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            ReplayIO.onActivityCreate(this);
        }
    
        @Override
        public void onStart() {
            super.onStart();
            ReplayIO.onActivityStart(this);
        }
    
        @Override
        public void onResume() {
            super.onResume();
            ReplayIO.onActivityResume(this);
        }
    
        @Override
        public void onPause() {
            ReplayIO.onActivityPause(this);
            super.onPause();
        }
    
        @Override
        public void onStop() {
            ReplayIO.onActivityStop(this);
            super.onStop();
        }
    }
    ```
<br>(In case you were curious, this is exactly what happens in `ReplayActivity`.)

####Step 4 - Track Away!
You're ready! Kick things off with `ReplayIO.trackEvent(eventNameString


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

### Set Traits
```java
ReplayIO.updateTraits(String traits)
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

