package io.replay.framework;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.AndroidTestCase;

public class ReplaySessionManagerTest extends AndroidTestCase {
	private SharedPreferences mPrefs;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mPrefs = getContext().getSharedPreferences("ReplayIOPreferences", Context.MODE_PRIVATE);
	}

	public void testSessionUUID() {
        assertEquals(mPrefs.getString(ReplayPrefs.KEY_SESSION_ID,""),"");
		
		String uuid = ReplaySessionManager.startSession(getContext());
		
		// should get UUID string
		assertNotNull(uuid);
		assertEquals(UUID.fromString(uuid).toString(), uuid);
		
		// should save to preferences
		assertTrue(mPrefs.contains(ReplayPrefs.KEY_SESSION_ID));
		assertEquals(mPrefs.getString(ReplayPrefs.KEY_SESSION_ID, ""), uuid);
		
		// should not regenerate the UUID
		assertEquals(ReplaySessionManager.startSession(getContext()), uuid);
	}
	
	public void testEndSession() {
		if (!mPrefs.contains(ReplayPrefs.KEY_SESSION_ID)) {
			ReplaySessionManager.startSession(getContext());
			assertTrue(mPrefs.contains(ReplayPrefs.KEY_SESSION_ID));
		}
		
		ReplaySessionManager.endSession(getContext());
		assertEquals(mPrefs.getString(ReplayPrefs.KEY_SESSION_ID,""),"");
	}
}
