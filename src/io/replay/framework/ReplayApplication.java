package io.replay.framework;

import android.annotation.SuppressLint;
import android.app.Application;

/**
 * Help to get awareness of application's foreground/background status, in order to persist queuing
 * requests do disk when it goes to background and loading persisted requests into queue when it
 * come back to foreground.
 * <br>
 * In order to make this work, third party applications must reference the ReplayApplication
 * in the android:name attribute of the application tag in AndroidManifest.xml.
 * <p/>
 * <pre>
 * {@code
 *   <application
 *        android:icon="@drawable/ic_launcher"
 *        android:label="@string/app_name"
 *        android:name="io.replay.framework.ReplayApplication">
 * }
 * </pre>
 * <p/>
 * <p>Note: This feature rely on {@link android.app.Application#registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks)},
 * it is available since Android API level 14 (Ice Cream Sandwich). So, devices earlier than this will have to
 * use the following ways.</p>
 * <p/>
 * 1, Implement your Activitys by extends {@link ReplayActivity}, it enables ReplayIO tracking the status of your application. <br>
 * 2. Manually track the status of your application, by overriding {@link android.app.Activity#onStart()}, {@link android.app.Activity#onResume()},
 * {@link android.app.Activity#onPause()} and  {@link android.app.Activity#onStop()}:
 *
 * <pre>
 * @Override public void onStart(){
 *     super.onStart();
 *     ReplayIO.activityStart();
 * }
 *
 * @Override public void onResume() {
 *     super.onResume();
 *     ReplayIO.activityResume();
 * }
 *
 * @Override public void onPause() {
 *     ReplayIO.activityPause();
 *     super.onPause();
 * }
 *
 * @Override public void onStop() {
 *     ReplayIO.activityStop();
 *     super.onStop();
 * }
 * </pre>
 * @see ReplayActivity
 * @see ReplayIO#activityStart
 * @see ReplayIO#activityResume
 * @see ReplayIO#activityPause
 * @see ReplayIO#activityStop
 */
public class ReplayApplication extends Application {

    /**
     * Overridden {@link android.app.Application#onCreate()} to register ActivityLifecycleCallbacks at start.
     */
    @SuppressLint("NewApi")
    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ReplayLifecycleHandler());
    }
}
