package io.replay.framework;

import android.app.Activity;
import android.os.Bundle;

/**
 * This is a base activity for automatically track the status of your application.
 * ReplayIO provides three methods for tracking lifecycle status:
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
 * You may only choose one of these methods.
 */
class ReplayActivity extends Activity {

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
