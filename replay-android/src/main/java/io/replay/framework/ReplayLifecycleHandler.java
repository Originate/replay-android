package io.replay.framework;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.os.Bundle;

/**
 * In an application with a minimum API level of 14 (Ice Cream Sandwich), ReplayIO can
 * monitor application lifecycle using Android-provided lifecycle hooks in lieu of the
 * user manually providing them (usually in the form of subclassing {@link io.replay.framework.ReplayActivity}
 * or manually calling {@link ReplayIO#onActivityCreate(Context)}, <i>etc.</i>)
 * <p>ReplayIO provides three methods for tracking lifecycle status:
 * <ol>
 * <li>In applications with <code>mininumSDKVersion >= 14</code>  (ICS+), don't do anything, and ReplayIO will
 * use {@link io.replay.framework.ReplayLifecycleHandler} to automagically track lifecycle! Easy!</li>
 * <li>In applications of any SDK level, you may extend this class from all of your Activities that you wish to track.
 * ReplayIO will then handle lifecycle events normally.
 * </li>
 * <li> If your application's <code>mininumSDKVersion</code> is less than 14 AND you already inherit from another base class (e.g., ActionBarSherlock),
 * you may manually override {@link android.app.Activity#onCreate(android.os.Bundle)} {@link android.app.Activity#onStart()}, {@link android.app.Activity#onResume()},
 * {@link android.app.Activity#onPause()} and {@link android.app.Activity#onStop()}, and then call the equivalent ReplayIO lifecycle methods.
 * </li>
 * </ol>
 * Please only choose one of these methods.
 * <p>ReplayIO will detect the lack of subclass and will enable this class for lifecycle hooks. In any other case,
 * this class will be disabled so as to not duplicate lifecycle-managing events.
 */
@SuppressLint("NewApi")
public class ReplayLifecycleHandler implements ActivityLifecycleCallbacks {

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        ReplayIO.onActivityCreate(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        ReplayIO.onActivityStart(activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        ReplayIO.onActivityResume(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ReplayIO.onActivityPause(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        ReplayIO.onActivityStop(activity);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        /*do nothing*/
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        /*do nothing*/
    }
}
