package io.replay.framework;

import android.content.Context;

import java.util.UUID;

class ReplaySessionManager {

    /**
     * Get or generate a session UUID.  Generated session UUID will be saved.
     *
     * @param context The {@link android.content.Context#getApplicationContext()}
     * @return The session UUID.
     */
    static String startSession(Context context) {
        ReplayPrefs prefs = ReplayPrefs.get(context.getApplicationContext());
        if (prefs.getSessionID().isEmpty()) {
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
    static void endSession(Context context) {
        ReplayLogger.d("Session ended");
        ReplayPrefs.get(context.getApplicationContext()).setSessionID("");
    }
}
