package io.replay.framework;

import java.lang.reflect.Field;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.AndroidTestCase;

public class ReplayIOTest extends AndroidTestCase {

	private ReplayIO replayIO;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
	}

	public void testGetClientUUID() {
		String clientUUID = ReplayIO.getClientUUID(getContext());
		assertNotNull(clientUUID);
		
		assertNotNull(clientUUID);
		assertEquals(UUID.fromString(clientUUID).toString(), clientUUID);
		
		
		SharedPreferences mPrefs = getContext().getSharedPreferences("ReplayIOPreferences", Context.MODE_PRIVATE);
		// should save to preferences
		assertTrue(mPrefs.contains(ReplayConfig.KEY_SESSION_ID));
		assertEquals(mPrefs.getString(ReplayConfig.KEY_SESSION_ID, ""), clientUUID);
		
		// should not regenerate the UUID
		assertEquals(ReplayIO.getClientUUID(getContext()), clientUUID);
	}
	
	
	public void testInit() throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
		replayIO = ReplayIO.init(getContext(), "api_key");
		
		Field initialized = ReplayIO.class.getDeclaredField("initialized");
		initialized.setAccessible(true);
		assertTrue(initialized.getBoolean(replayIO));
		
		assertTrue(ReplayIO.isEnabled());
		assertFalse(ReplayIO.isDebugMode());
		assertTrue(ReplayIO.isRunning());
	}
	
	public void testDisable() {
		ReplayIO.init(mContext, "api_key");
		ReplayIO.disable();
		assertFalse(ReplayIO.isEnabled());
	}
	
	public void testEnable() {
		ReplayIO.init(mContext, "api_key");
		ReplayIO.enable();
		assertTrue(ReplayIO.isEnabled());
	}
	
	public void testSetDebugMode() {
		ReplayIO.init(mContext, "api_key");
		ReplayIO.setDebugMode(true);
		assertTrue(ReplayIO.isDebugMode());
		
		ReplayIO.setDebugMode(false);
		assertFalse(ReplayIO.isDebugMode());
	}
	
}
