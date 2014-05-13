package io.replay.framework;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class ReplaySessionManager implements ReplayConfig {

	public static String sessionUUID(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences("ReplayIOPreferences", Context.MODE_PRIVATE);
		if(!mPrefs.contains(KEY_SESSION_ID)) {
			SharedPreferences.Editor editor = mPrefs.edit();
			editor.putString(KEY_SESSION_ID, UUID.randomUUID().toString());
			editor.commit();
			Log.d("REPLAY_IO", "Generated new session uuid");
		}
		return mPrefs.getString(KEY_SESSION_ID, "");
	}
	
	public static void endSession(Context context) {
		Log.d("REPLAY_IO", "Session ended");
		SharedPreferences mPrefs = context.getSharedPreferences("ReplayIOPreferences", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.remove(KEY_SESSION_ID);
		editor.commit();
	}
}