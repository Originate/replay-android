package io.replay.framework.lifecycle;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;

import io.replay.framework.ReplayIO;

/**
 * Implements ActivityLifecycleCallbacks, help tracking the status of the app.
 *
 * This method of tracking Activity state can only be used on API Level 14 (ICS) or higher.
 *
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
        //ignore
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        //ignore
        //TODO set watchdog service here
    }
}
