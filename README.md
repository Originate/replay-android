[![Circle CI](https://circleci.com/gh/Originate/replay-android/tree/develop.png?style=badge&circle-token=d9bbccb7db1bfaa58c304a8ac313aa2338f92423)](https://circleci.com/gh/Originate/replay-android/tree/develop)


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
```gradle
dependencies {
    compile project(':replay-android')
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
Add the configuration XML to your application/s `res/values` folder. The xml file can have any name:
    ```xml
    <?xml version="1.0" encoding="utf-8"?>
    <!--NOTE: All the parameters except api_key have default values which will be used if no value is specified-->
    <resources>

        <!--the interval between when events are dispatched to the server -->
        <!-- Default: 60000 ms  - OPTIONAL -->
        <integer name="dispatch_interval">0</integer>
    
        <!-- set true to enable event tracking-->
        <!-- Default: true - OPTIONAL-->
        <string name="enabled">false</string>
    
        <!-- set true to print debug messages-->
        <!-- default: false - OPTIONAL-->
        <string name="debug_mode_enabled">false</string>
    
        <!-- If the number of events stored (not yet dispatched) reaches this value, no more events will be received-->
        <!-- Default: 1200 - OPTIONAL-->
        <integer name="max_queue">1000</integer>
    
        <!-- Normally events are only sent to the server when the dispatch_interval is met but if the number of events reaches flush_at, they will be automatically sent-->
        <!--Default: 100 - OPTIONAL-->
        <integer name="flush_at">50</integer>
    
        <!--replay api key - REQUIRED -->
        <string name="api_key">API_KEY_HERE</string>
    </resources>
    ```

####Step 3 - Hook into Android App Lifecycle
You have 3 choices for this:

1. If your app's `minSDKVersion` is 14 or greater (i.e., if your app supports a *minimum* of Ice Cream Sandwich), you have to do **nothing**. <br>
    That's right! Replay.io will handle lifecycle events for you!

2. If you support a minimum SDK version *lower* than 14, you can extend `ReplayActivity` like so:
    ```java
    import io.replay.framework.ReplayActivity;
    
    public class ExampleActivity0 extends ReplayActivity {

        @Override public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            //regular Activity setup here
        }

        /*for other lifecycle methods, just call "super"!*/

    }
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
You're ready! Kick things off with `ReplayIO.track(String, optionalVarArgsArrayOfData)`


### Tests

Most of our functionality is covered by tests - if you find something that isn't please let us know :)
The test suite can be run by:
<br/> `gradle assembleEmulator` (or `assembleGenymotion` if you're testing using Genymotion)
<br/> `gradle assembleDebugTest`
<br/> `gradle connectedAndroidTest`<br/>
Please note that these tests require a connected Android device - be it emulator or Genymotion.

## Documentation for framework users

### Installation

1. * With ADT:
        1. Import Replay.io to your workspace, in the Properties window > Android tab > check "Is Library".
        2. In your project's properties window, Android tab, add Replay.io as a library.
   * With Android Studio:
        1. Import the project by selecting the top-level `build.gradle` file. 
           
2. Make sure your AndroidManifest.xml has the INTERNET permission:
    ```xml
    <uses-permission android:name="android.permission.INTERNET"/>
    ```

3. Initialize the tracker:
    ```java
    ReplayIO.init(Context context);     // or
    ReplayIO.init(Context context, String apiKey);  
    ```
*Note: initializing is not the same as enabling tracking. No events will be tracked unless the ReplayIO client is enabled.*
    
### Tracking Events
In order to track an event. You can use either of the following functions:
```java
ReplayIO.track(String event, Map<String,?> properties);  //or
ReplayIO.track(String event, Object... properties);
```
To see a list of properties that can be specified go to:</br>
<br>http://docs.replay.io/rest-api/api-special-properties </br>

Note that properties are passed as either a `Map<String,?>` *or* as a varargs array of Objects - i.e., `"k1", v1, "k2", v2`.

### Set Distinct ID
Once a Distinct ID (or *identity*) is set, all events from the user will be associated with that Distinct ID. In addition, traits are associated with a particular ID, so the Distinct ID provides a way to link users to a specific set of traits.
```java
ReplayIO.identify(String distinctId);
```

### Set Traits
Setting a user's traits allows developers to add additional information about a user, such as gender and age. <br>
In order to associate a set of traits with a particular user, you must first use `identify(String distinctId)` to identify the user.
```java
ReplayIO.updateTraits(Map<String,?> traits);    //or
ReplayIO.updateTraits(Object... traits);
```
<br> To see a list of traits that can be specified go to:</br>
<br>http://docs.replay.io/rest-api/api-special-properties </br>

### Debugging
Logging to Logcat is enabled/disabled intially based on the XML parameters file that you create (see Setup Step 2).
The XML configuration can be overridden programmatically, however:
```java
ReplayIO.setDebugMode(true);
ReplayIO.setDebugMode(false);
```

### Enable/disable
The library will track events and create traits iff it is enabled. The libraryis enabled/disabled intially based on the XML parameters file that you create (see Setup Step 2).
```java
ReplayIO.start();
ReplayIO.stop();
```

### Dispatching
By default, ReplayIO will dispatch event data every minute. However, you may specify the interval (in milliseconds) between when in events are sent in the parameters XML file (see step 2 of setup).
If `dispatchInterval == 0`, then events are dispatched as soon as they are received. If you would like to manually dispatch all the previously enqueued events/traits, you can use the following function:
```java
ReplayIO.dispatch();
```

