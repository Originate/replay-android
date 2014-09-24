package io.replay.framework;

import android.content.Context;

import java.util.UUID;

import io.replay.framework.util.ReplayLogger;
import io.replay.framework.util.ReplayPrefs;

public class ReplaySessionManager implements ReplayConfig {

    /**
     * Get or generate a session UUID.  Generated session UUID will be saved.
     *
     * @param context The {@link android.content.Context#getApplicationContext()}
     * @return The session UUID.
     */
    public static String getOrCreateSessionUUID(Context context) {
        ReplayPrefs prefs = ReplayPrefs.get(context);
        if (prefs.getSessionID().length() == 0) {
            prefs.setSessionID(UUID.randomUUID().toString());
            ReplayLogger.d("Generated new session uuid");
        }
        return prefs.getSessionID();
    }

    /**
     * End the current session, delete the session UUID.
     *
     * @param context The {@link android.content.Context#getApplicationContext()}
     */
    public static void endSession(Context context) {
        ReplayLogger.d("Session ended");
        ReplayPrefs.get(context).setSessionID("");
    }
}
