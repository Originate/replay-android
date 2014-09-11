package io.replay.framework;

import android.app.Activity;

/**
 * This is a base activity for automatically track the status of yours application.
 * Extends all your Activity classes with this or do this manually by overriding
 * {@link android.app.Activity#onStart()}, {@link android.app.Activity#onResume()}, {@link android.app.Activity#onPause()} and
 * {@link android.app.Activity#onStop()}:
 * <pre>
 * {@literal @}Override
 * public void onStart(){
 *     super.onStart();
 *     ReplayIO.activityStart();
 * }
 *
 * {@literal @}Override
 * public void onResume() {
 *     super.onResume();
 *     ReplayIO.activityResume();
 * }
 *
 * {@literal @}Override
 * public void onPause() {
 *     ReplayIO.activityPause();
 *     super.onPause();
 * }
 *
 * {@literal @}Override
 * public void onStop() {
 *     ReplayIO.activityStop();
 *     super.onStop();
 * }
 * </pre>
 */
public class ReplayActivity extends Activity {

    private static int started;
    private static int resumed;
    @SuppressWarnings("unused")
    private static int paused;
    private static int stopped;

    @Override
    public void onStart() {
        super.onStart();
        ReplayIO.activityStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        ReplayIO.activityResume();
    }

    @Override
    public void onPause() {
        ReplayIO.activityPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        ReplayIO.activityStop();
        super.onStop();
    }
}
