package io.replay.framework;

import java.lang.reflect.Field;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.AndroidTestCase;

import io.replay.framework.error.ReplayIONotInitializedException;

public class ReplayIOTest extends AndroidTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        Field initialized = ReplayIO.class.getDeclaredField("initialized");
        initialized.setAccessible(true);
        initialized.setBoolean(null, false);

        super.tearDown();
    }

    public void testInit() throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException, ReplayIONotInitializedException {
        ReplayIO.init(getContext(), "api_key", );

        Field initialized = ReplayIO.class.getDeclaredField("initialized");
        initialized.setAccessible(true);
        assertTrue(initialized.getBoolean(null));

        assertTrue(ReplayIO.isEnabled());
        assertFalse(ReplayIO.isDebugMode());
        assertTrue(ReplayIO.isRunning());
    }

    public void testGetClientUUID() throws ReplayIONotInitializedException {
        // make sure no KEY_CLIENT_ID exist
        SharedPreferences mPrefs = getContext().getSharedPreferences(ReplayConfig.PREFERENCES, Context.MODE_PRIVATE);
        mPrefs.contains(ReplayConfig.KEY_CLIENT_ID);
        mPrefs.edit().remove(ReplayConfig.KEY_CLIENT_ID);
        mPrefs.edit().commit();

        // get/generate client UUID
        String clientUUID = ReplayIO.getClientUUID();
        assertNotNull(clientUUID);
        assertEquals(UUID.fromString(clientUUID).toString(), clientUUID);


        // should save to preferences
        assertTrue(mPrefs.contains(ReplayConfig.KEY_CLIENT_ID));
        assertEquals(mPrefs.getString(ReplayConfig.KEY_CLIENT_ID, ""), clientUUID);

        // should not regenerate the UUID
        assertEquals(ReplayIO.getClientUUID(), clientUUID);
    }

    public void testDisable() throws ReplayIONotInitializedException {
        ReplayIO.init(mContext, "api_key", );
        ReplayIO.disable();
        assertFalse(ReplayIO.isEnabled());
    }

    public void testEnable() throws ReplayIONotInitializedException {
        ReplayIO.init(mContext, "api_key", );
        ReplayIO.enable();
        assertTrue(ReplayIO.isEnabled());
    }

    public void testSetDebugMode() throws ReplayIONotInitializedException {
        ReplayIO.init(mContext, "api_key", );
        ReplayIO.setDebugMode(true);
        assertTrue(ReplayIO.isDebugMode());

        ReplayIO.setDebugMode(false);
        assertFalse(ReplayIO.isDebugMode());
    }

    public void testIdentify() throws ReplayIONotInitializedException, NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
        SharedPreferences mPrefs = getContext().getSharedPreferences(ReplayConfig.PREFERENCES, Context.MODE_PRIVATE);
        // make sure DISTINCT_ID is not set at the beginning
        if (mPrefs.contains(ReplayConfig.PREF_DISTINCT_ID)) {
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.remove(ReplayConfig.PREF_DISTINCT_ID);
            editor.commit();
        }

        // should not be able to set identity before  initialized
        try {
            ReplayIO.identify("new_identity");
            fail("Should have thrown exception.");
        } catch (ReplayIONotInitializedException e) {
            assertTrue(true);
        }

        assertEquals("", mPrefs.getString(ReplayConfig.PREF_DISTINCT_ID, ""));

        ReplayIO.init(mContext, "api_key", );
        ReplayIO.identify("new_identity");
        assertEquals("new_identity", mPrefs.getString(ReplayConfig.PREF_DISTINCT_ID, ""));
    }
}
