package io.replay.framework;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;
import android.util.Log;

@SuppressLint("NewApi")
public class ReplayLifecycleHandler implements ActivityLifecycleCallbacks {

	private static int started;
	private static int resumed;
	private static int paused;
	private static int stopped;
	
	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onActivityStarted(Activity activity) {
		// TODO Auto-generated method stub
		started ++;
		
		checkAppVisibility();
	}

	@Override
	public void onActivityResumed(Activity activity) {
		resumed ++;
		
		checkAppVisibility();
	}

	@Override
	public void onActivityPaused(Activity activity) {
		paused ++;
	}

	@Override
	public void onActivityStopped(Activity activity) {
		// TODO Auto-generated method stub
		stopped ++;
		
		checkAppVisibility();
	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
		// TODO Auto-generated method stub

	}

	@Override 
	public void onActivityDestroyed(Activity activity) {
		// TODO Auto-generated method stub

	}
	
	public static boolean isApplicationVisible() {
        return started > stopped;
    }

    public static boolean isApplicationInForeground() {
        return resumed > stopped;
    }
    
    private void checkAppVisibility() {
    	if (!isApplicationVisible()) {
    		if (ReplayIO.isRunning()) {
    			Log.d("REPLAY_IO", "App goes to background. Stop!");
    			ReplayIO.stop();
    		}
    	} else {
    		if (!ReplayIO.isRunning()) {
    			Log.d("REPLAY_IO", "App goes to foreground. Run!");
    			ReplayIO.run();
    		}
    	}
    }
}