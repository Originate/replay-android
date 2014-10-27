package io.replay.framework;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.AndroidTestCase;

import java.lang.reflect.Field;

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

    public void testInit1() throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException, ReplayIONotInitializedException {
        ReplayIO.init(getContext());

        Field initialized = ReplayIO.class.getDeclaredField("initialized");
        initialized.setAccessible(true);
        assertTrue(initialized.getBoolean(null));
    }

    public void testInit2() throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException, ReplayIONotInitializedException {
        ReplayIO.init(getContext(), "api_key");
        Field initialized = ReplayIO.class.getDeclaredField("initialized");
        initialized.setAccessible(true);
        assertTrue(initialized.getBoolean(null));
    }

    public void testInit3() throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException, ReplayIONotInitializedException {
        ReplayIO.init(getContext(), ReplayParams.getOptions(getContext()));

        Field initialized = ReplayIO.class.getDeclaredField("initialized");
        initialized.setAccessible(true);
        assertTrue(initialized.getBoolean(null));
    }

    public void testGetClientUUID() throws ReplayIONotInitializedException {
        // make sure no KEY_CLIENT_ID exist
        SharedPreferences mPrefs = getContext().getSharedPreferences("ReplayIOPreferences", Context.MODE_PRIVATE);
        mPrefs.contains(ReplayPrefs.KEY_CLIENT_ID);
        mPrefs.edit().remove(ReplayPrefs.KEY_CLIENT_ID);
        mPrefs.edit().apply();

        // get/generate client UUID
        String clientUUID = ReplayIO.getOrGenerateClientUUID();
        assertNotNull(clientUUID);


        // should save to preferences
        assertTrue(mPrefs.contains(ReplayPrefs.KEY_CLIENT_ID));
        assertEquals(mPrefs.getString(ReplayPrefs.KEY_CLIENT_ID, ""), clientUUID);

        // should not regenerate the UUID
        assertEquals(ReplayIO.getOrGenerateClientUUID(), clientUUID);
    }

    public void testIdentify() throws ReplayIONotInitializedException, NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
        SharedPreferences mPrefs = getContext().getSharedPreferences("ReplayIOPreferences", Context.MODE_PRIVATE);
        // make sure DISTINCT_ID is not set at the beginning
        if (mPrefs.contains(ReplayPrefs.KEY_DISTINCT_ID)) {
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.remove(ReplayPrefs.KEY_DISTINCT_ID);
            editor.apply();
        }

        // should not be able to set identity before  initialized
        try {
            ReplayIO.identify("new_identity");
            fail("Should have thrown exception.");
        } catch (ReplayIONotInitializedException e) {
            assertTrue(true);
        }

        assertEquals("", mPrefs.getString(ReplayPrefs.KEY_DISTINCT_ID, ""));

        ReplayIO.init(mContext, "api_key");
        ReplayIO.identify("new_identity");
        assertEquals("new_identity", mPrefs.getString(ReplayPrefs.KEY_DISTINCT_ID, ""));
    }

    public void testDisable() throws ReplayIONotInitializedException {
        ReplayIO.init(mContext, "api_key");
        ReplayIO.disable();
        assertFalse(ReplayIO.isEnabled());
    }

    public void testEnable() throws ReplayIONotInitializedException {
        ReplayIO.init(mContext, "api_key");
        ReplayIO.enable();
        assertTrue(ReplayIO.isEnabled());
    }

    public void testXml() {
        Config mConfig = ReplayParams.getOptions(getContext());

        //check that it uses value in xml file
        assertEquals(60000,mConfig.getDispatchInterval());
        assertEquals(true,mConfig.isDebug());
        assertEquals(mConfig.getMaxQueue(),1200);

        //Check that values can be manually changed
        mConfig.setMaxQueue(1111);
        assertEquals(mConfig.getMaxQueue(),1111);

        //Check that it uses default when no values is entered
        assertEquals(mConfig.getFlushAt(),50);

        //Check that it uses default when the field is completely missing
        assertEquals(mConfig.isEnabled(),true);
    }
}
