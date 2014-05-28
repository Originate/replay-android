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
		getContext().getSharedPreferences("ReplayIOPreferences", Context.MODE_PRIVATE);
	}

	public void testSessionUUID() {
		assertFalse(mPrefs.contains(ReplayConfig.KEY_SESSION_ID));
		
		String uuid = ReplaySessionManager.sessionUUID(getContext());
		
		// should get UUID string
		assertNotNull(uuid);
		assertTrue(uuid.length() != 0);
		assertEquals(uuid, UUID.fromString(uuid).toString());
		
		// should save to preferences
		assertTrue(mPrefs.contains(ReplayConfig.KEY_SESSION_ID));
		assertEquals(uuid, mPrefs.getString(ReplayConfig.KEY_SESSION_ID, ""));
		
		// should not regenerate the UUID
		assertEquals(uuid, ReplaySessionManager.sessionUUID(getContext()));
	}
	
	public void testEndSession() {
		if (!mPrefs.contains(ReplayConfig.KEY_SESSION_ID)) {
			ReplaySessionManager.sessionUUID(getContext());
			assertTrue(mPrefs.contains(ReplayConfig.KEY_SESSION_ID));
		}
		
		ReplaySessionManager.endSession(getContext());
		assertFalse(mPrefs.contains(ReplayConfig.KEY_SESSION_ID));
	}
}
