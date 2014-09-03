package io.replay.framework;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;

import io.replay.framework.model.ReplayRequest;

public class ReplayAPIManagerTest extends AndroidTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testReplayAPIManager() throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
		ReplayAPIManager manager = new ReplayAPIManager("api_key", "client_uuid", "session_uuid", "distinct_id");
		
		Field apiKey = ReplayAPIManager.class.getDeclaredField("apiKey");
		apiKey.setAccessible(true);
		String apiKeyValue = (String) apiKey.get(manager);
		assertEquals("api_key", apiKeyValue);
		
		Field clientUUID = ReplayAPIManager.class.getDeclaredField("clientUUID");
		clientUUID.setAccessible(true);
		String clientUUIDValue = (String) clientUUID.get(manager);
		assertEquals("client_uuid", clientUUIDValue);
		
		Field sessionUUID = ReplayAPIManager.class.getDeclaredField("sessionUUID");
		sessionUUID.setAccessible(true);
		String sessionUUIDValue = (String) sessionUUID.get(manager);
		assertEquals("session_uuid", sessionUUIDValue);

        Field distinctId = ReplayAPIManager.class.getDeclaredField("distinctId");
        distinctId.setAccessible(true);
        String distinctIdValue = (String) distinctId.get(manager);
        assertEquals("distinct_id", distinctIdValue);
	}
	
	public void testUpdateSessionUUID() throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
		ReplayAPIManager manager = new ReplayAPIManager("api_key", "client_uuid", "session_uuid", "distinct_id");
		
		manager.updateSessionUUID("new_session_uuid");
		
		Field sessionUUID = ReplayAPIManager.class.getDeclaredField("sessionUUID");
		sessionUUID.setAccessible(true);
		String sessionUUIDValue = (String) sessionUUID.get(manager);
		assertEquals("new_session_uuid", sessionUUIDValue);
	}
	
	public void testRequestForEvent() throws JSONException {
		ReplayAPIManager manager = new ReplayAPIManager("api_key", "client_uuid", "session_uuid", "distinct_id");
		HashMap<String, String> map = new HashMap<String,String>();
		map.put("color", "green");
		
		ReplayRequest request = manager.requestForEvent("color_selected", map);
		assertNotNull(request);
		assertNotNull(request.getBody());
		assertNotNull(new String(request.getBody()));
		
		JSONObject json = new JSONObject(new String(request.getBody())); 
		assertNotNull(json);
		
		assertTrue(json.has(ReplayConfig.KEY_REPLAY_KEY));
		assertEquals("api_key", json.getString(ReplayConfig.KEY_REPLAY_KEY));
		
		assertTrue(json.has(ReplayConfig.KEY_CLIENT_ID));
		assertEquals("client_uuid", json.getString(ReplayConfig.KEY_CLIENT_ID));
		
		assertTrue(json.has(ReplayConfig.KEY_SESSION_ID));
		assertEquals("session_uuid", json.getString(ReplayConfig.KEY_SESSION_ID));

        assertTrue(json.has(ReplayConfig.KEY_DISTINCT_ID));
        assertEquals("distinct_id", json.getString(ReplayConfig.KEY_DISTINCT_ID));
		
		assertTrue(json.has(ReplayConfig.KEY_DATA));
		
		JSONObject data = json.getJSONObject(ReplayConfig.KEY_DATA);
		assertTrue(data.has("event"));
		assertEquals("color_selected", data.getString("event"));
		
		assertTrue(data.has("color"));
		assertEquals("green", data.getString("color"));
	}
	
	public void testRequestForAlias() throws JSONException {
		ReplayAPIManager manager = new ReplayAPIManager("api_key", "client_uuid", "session_uuid", "distinct_id");
		
		ReplayRequest request = manager.requestForAlias("new_alias");
		assertNotNull(request);
		assertNotNull(request.getBody());
		
		JSONObject json = new JSONObject(new String(request.getBody())); 
		assertNotNull(json);
		
		assertTrue(json.has(ReplayConfig.KEY_REPLAY_KEY));
		assertEquals("api_key", json.getString(ReplayConfig.KEY_REPLAY_KEY));
		
		assertTrue(json.has(ReplayConfig.KEY_CLIENT_ID));
		assertEquals("client_uuid", json.getString(ReplayConfig.KEY_CLIENT_ID));
		
		assertFalse(json.has(ReplayConfig.KEY_SESSION_ID));
		assertFalse(json.has(ReplayConfig.KEY_DATA));
		
		assertTrue(json.has("alias"));
		assertEquals("new_alias", json.getString("alias"));
	}
}
