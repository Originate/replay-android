package io.replay.framework.lifecycle;

import android.app.Activity;
import android.os.Bundle;

import io.replay.framework.ReplayIO;

/**
 * This is a base activity for automatically track the status of yours application.
 * Extends all your Activity classes with this or do this manually by overriding
 * {@link Activity#onStart()}, {@link Activity#onResume()}, {@link Activity#onPause()} and
 * {@link Activity#onStop()}:
 * <pre>
 * {@literal @}Override
 * public void onCreate(Bundle savedInstanceState){
 *     super.onCreate(savedInstanceState;
 *     ReplayIO.onActivityCreate(this);
 * }
 * {@literal @}Override
 * public void onStart(){
 *     super.onStart();
 *     ReplayIO.onActivityStart(this);
 * }
 *
 * {@literal @}Override
 * public void onResume() {
 *     super.onResume();
 *     ReplayIO.onActivityResume(this);
 * }
 *
 * {@literal @}Override
 * public void onPause() {
 *     ReplayIO.onActivityPause();
 *     super.onPause();
 * }
 *
 * {@literal @}Override
 * public void onStop() {
 *     ReplayIO.onActivityStop();
 *     super.onStop();
 * }
 * </pre>
 */
public class ReplayActivity extends Activity {

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
