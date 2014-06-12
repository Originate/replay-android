package io.replay.framework;

import android.annotation.SuppressLint;
import android.app.Application;

/**
 * Help to get awareness of application's foreground/background status.
 * <br>
 * In order to make this work, third party applications must reference the ReplayApplication
 * in the android:name attribute of the application tag in AndroidManifest.xml.
 * 
 * <pre>
 * {@code
 *  <application
        android:icon="@drawable/ic_launcher"
        android:label="@string/app_name"
        android:name="io.replay.framework.ReplayApplication">
   }
 * </pre>
 * 
 * <p>Note: This feature rely on {@link Application#registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks)},
 * it is available since Android API level 14 (Ice Cream Sandwich). So, devices earlier than this will not benefit 
 * from this, the feature of persisting queuing request do disk and loading persisted requests into queue will not work.</p>
 * 
 * @author richard
 *
 */
public class ReplayApplication extends Application {

	/**
	 * Overridden {@link Application#onCreate()} to register ActivityLifecycleCallbacks at start. 
	 */
	@SuppressLint("NewApi")
	@Override
	public void onCreate() {
		super.onCreate();
		registerActivityLifecycleCallbacks(new ReplayLifecycleHandler());
	}
}
