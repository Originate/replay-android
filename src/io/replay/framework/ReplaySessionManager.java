package io.replay.framework;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;

public class ReplaySessionManager implements ReplayConfig {

	/**
	 * Get or generate a session UUID.  Generated session UUID will be saved.
	 * @param context The {@link ApplicationContext}
	 * @return The session UUID.
	 */
	public static String sessionUUID(Context context) {
		SharedPreferences mPrefs = context.getSharedPreferences("ReplayIOPreferences", Context.MODE_PRIVATE);
		if(!mPrefs.contains(KEY_SESSION_ID)) {
			SharedPreferences.Editor editor = mPrefs.edit();
			editor.putString(KEY_SESSION_ID, UUID.randomUUID().toString());
			editor.commit();
			ReplayIO.debugLog("Generated new session uuid");
		}
		return mPrefs.getString(KEY_SESSION_ID, "");
	}
	
	/**
	 * Delete the current session UUID.
	 * @param context The {@link ApplicationContext}
	 */
	public static void endSession(Context context) {
		ReplayIO.debugLog("Session ended");
		SharedPreferences mPrefs = context.getSharedPreferences("ReplayIOPreferences", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.remove(KEY_SESSION_ID);
		editor.commit();
	}
}
