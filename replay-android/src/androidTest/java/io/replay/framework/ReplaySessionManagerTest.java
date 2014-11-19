package io.replay.framework;

import android.test.AndroidTestCase;

import java.util.UUID;

public class ReplaySessionManagerTest extends AndroidTestCase {
    private ReplayPrefs mPrefs;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mPrefs = ReplayPrefs.get(getContext());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mPrefs.setSessionID("");

    }

    public void testSessionUUID() {
        assertEquals(mPrefs.getSessionID(), "");

        String uuid = ReplaySessionManager.startSession(getContext());

        // should get UUID string
        assertNotNull(uuid);
        assertEquals(UUID.fromString(uuid).toString(), uuid); //I would be concerned if this fails

        // should save to preferences
        assertNotNull(mPrefs.getSessionID());
        assertEquals(uuid, mPrefs.getSessionID());

        // should not regenerate the UUID if it hasn't been unset
        assertEquals(ReplaySessionManager.startSession(getContext()), uuid);
    }

    public void testEndSession() {
        assertEquals(mPrefs.getSessionID(), "");

        String uuid = ReplaySessionManager.startSession(getContext());
        ReplaySessionManager.endSession(getContext());

        assertEquals(mPrefs.getSessionID(), "");
    }
}
