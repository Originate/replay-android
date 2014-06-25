package io.replay.framework;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;

/**
 * Implements ActivityLifecycleCallbacks, help tracking the status of the app.
 */
@SuppressLint("NewApi")
public class ReplayLifecycleHandler implements ActivityLifecycleCallbacks {

    private static int started;
    private static int resumed;
    @SuppressWarnings("unused")
    private static int paused;
    private static int stopped;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        started++;

        checkAppVisibility();
    }

    @Override
    public void onActivityResumed(Activity activity) {
        resumed++;

        checkAppVisibility();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        paused++;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        stopped++;

        checkAppVisibility();
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    /**
     * Check if the app is visible to user.
     * @return True if app is visible, false otherwise.
     */
    private static boolean isApplicationVisible() {
        return started > stopped;
    }

    /**
     * Check if the app is in foreground.
     * @return True if app is is foreground, false otherwise.
     */
    private static boolean isApplicationInForeground() {
        return resumed > stopped;
    }

    private void checkAppVisibility() {
        try {
            if (!isApplicationVisible()) {
                if (ReplayIO.isRunning()) {
                    ReplayIO.debugLog("App goes to background. Stop!");
                    ReplayIO.stop();
                }
            } else {
                if (!ReplayIO.isRunning()) {
                    ReplayIO.debugLog("App goes to foreground. Run!");
                    ReplayIO.run();
                }
            }
        } catch (ReplayIONotInitializedException e) {
            ReplayIO.errorLog(e.getMessage());
        }
    }
}
