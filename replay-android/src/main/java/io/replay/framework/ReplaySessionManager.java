package io.replay.framework;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

import io.replay.framework.util.ReplayPrefs;

public class ReplaySessionManager implements ReplayConfig {

    /**
     * Get or generate a session UUID.  Generated session UUID will be saved.
     *
     * @param context The {@link android.content.Context#getApplicationContext()}
     * @return The session UUID.
     */
    public static String sessionUUID(Context context) {
        ReplayPrefs mPrefs = ReplayPrefs.get(context);
        if (!(mPrefs.getSessionID().length() == 0)) {
            mPrefs.setSessionID(UUID.randomUUID().toString());
            ReplayIO.debugLog("Generated new session uuid");
        }
        return mPrefs.getSessionID();
    }

    /**
     * End the current session, delete the session UUID.
     *
     * @param context The {@link android.content.Context#getApplicationContext()}
     */
    public static void endSession(Context context) {
        ReplayIO.debugLog("Session ended");
        ReplayPrefs.get(context).setSessionID("");
    }
}
