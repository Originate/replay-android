package io.replay.framework.tests;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.AndroidTestCase;

import io.replay.framework.ReplayConfig;
import io.replay.framework.ReplaySessionManager;

public class ReplaySessionManagerTest extends AndroidTestCase {
	private SharedPreferences mPrefs;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mPrefs = getContext().getSharedPreferences(ReplayConfig.PREFERENCES, Context.MODE_PRIVATE);
	}

	public void testSessionUUID() {
		assertFalse(mPrefs.contains(ReplayConfig.KEY_SESSION_ID));
		
		String uuid = ReplaySessionManager.getOrCreateSessionUUID(getContext());
		
		// should get UUID string
		assertNotNull(uuid);
		assertEquals(UUID.fromString(uuid).toString(), uuid);
		
		// should save to preferences
		assertTrue(mPrefs.contains(ReplayConfig.KEY_SESSION_ID));
		assertEquals(mPrefs.getString(ReplayConfig.KEY_SESSION_ID, ""), uuid);
		
		// should not regenerate the UUID
		assertEquals(ReplaySessionManager.getOrCreateSessionUUID(getContext()), uuid);
	}
	
	public void testEndSession() {
		if (!mPrefs.contains(ReplayConfig.KEY_SESSION_ID)) {
			ReplaySessionManager.getOrCreateSessionUUID(getContext());
			assertTrue(mPrefs.contains(ReplayConfig.KEY_SESSION_ID));
		}
		
		ReplaySessionManager.endSession(getContext());
		assertFalse(mPrefs.contains(ReplayConfig.KEY_SESSION_ID));
	}
}
