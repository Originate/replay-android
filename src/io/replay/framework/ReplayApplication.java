package io.replay.framework;

import android.annotation.SuppressLint;
import android.app.Application;

public class ReplayApplication extends Application {

	@SuppressLint("NewApi")
	@Override
	public void onCreate() {
		super.onCreate();
		registerActivityLifecycleCallbacks(new ReplayLifecycleHandler());
	}
}
