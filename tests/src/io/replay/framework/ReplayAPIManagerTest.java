package io.replay.framework;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;

public class ReplayAPIManagerTest extends AndroidTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testRepalyAPIManager() throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
		ReplayAPIManager manager = new ReplayAPIManager("api_key", "client_uuid", "session_uuid");
		
		Field apiKey = ReplayAPIManager.class.getDeclaredField("apiKey");
		apiKey.setAccessible(true);
		String apiKeyValue = (String) apiKey.get(manager);
		assertEquals(apiKeyValue, "api_key");
		
		Field clientUUID = ReplayAPIManager.class.getDeclaredField("clientUUID");
		clientUUID.setAccessible(true);
		String clientUUIDValue = (String) clientUUID.get(manager);
		assertEquals(clientUUIDValue, "client_uuid");
		
		Field sessionUUID = ReplayAPIManager.class.getDeclaredField("sessionUUID");
		sessionUUID.setAccessible(true);
		String sessionUUIDValue = (String) sessionUUID.get(manager);
		assertEquals(sessionUUIDValue, "session_uuid");
	}
	
	public void testUpdateSessionUUID() throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
		ReplayAPIManager manager = new ReplayAPIManager("api_key", "client_uuid", "session_uuid");
		
		manager.updateSessionUUID("new_session_uuid");
		
		Field sessionUUID = ReplayAPIManager.class.getDeclaredField("sessionUUID");
		sessionUUID.setAccessible(true);
		String sessionUUIDValue = (String) sessionUUID.get(manager);
		assertEquals(sessionUUIDValue, "new_session_uuid");
	}
	
	public void testRequestForEvent() throws JSONException, AuthFailureError {
		ReplayAPIManager manager = new ReplayAPIManager("api_key", "client_uuid", "session_uuid");
		HashMap<String, String> map = new HashMap<String,String>();
		map.put("color", "green");
		
		Request<?> request = manager.requestForEvent("color_selected", map);
		assertNotNull(request);
		assertNotNull(request.getBody());
		assertNotNull(new String(request.getBody()));
		
		JSONObject json = new JSONObject(new String(request.getBody())); 
		assertNotNull(json);
		
		assertTrue(json.has(ReplayConfig.KEY_REPLAY_KEY));
		assertEquals(json.getString(ReplayConfig.KEY_REPLAY_KEY), "api_key");
		
		assertTrue(json.has(ReplayConfig.KEY_CLIENT_ID));
		assertEquals(json.getString(ReplayConfig.KEY_CLIENT_ID), "client_uuid");
		
		assertTrue(json.has(ReplayConfig.KEY_SESSION_ID));
		assertEquals(json.getString(ReplayConfig.KEY_SESSION_ID), "session_uuid");
		
		assertTrue(json.has(ReplayConfig.KEY_DATA));
		
		JSONObject data = json.getJSONObject(ReplayConfig.KEY_DATA);
		assertTrue(data.has("event"));
		assertEquals(data.getString("event"), "color_selected");
		
		assertTrue(data.has("color"));
		assertEquals(data.getString("color"), "green");
	}
	
	public void testRequestForAlias() throws JSONException, AuthFailureError {
		ReplayAPIManager manager = new ReplayAPIManager("api_key", "client_uuid", "session_uuid");
		
		Request<?> request = manager.requestForAlias("new_alias");
		assertNotNull(request);
		assertNotNull(request.getBody());
		
		JSONObject json = new JSONObject(new String(request.getBody())); 
		assertNotNull(json);
		
		assertTrue(json.has(ReplayConfig.KEY_REPLAY_KEY));
		assertEquals(json.getString(ReplayConfig.KEY_REPLAY_KEY), "api_key");
		
		assertTrue(json.has(ReplayConfig.KEY_CLIENT_ID));
		assertEquals(json.getString(ReplayConfig.KEY_CLIENT_ID), "client_uuid");
		
		assertFalse(json.has(ReplayConfig.KEY_SESSION_ID));
		assertFalse(json.has(ReplayConfig.KEY_DATA));
		
		assertTrue(json.has("alias"));
		assertEquals(json.getString("alias"), "new_alias");
	}
	
	public void testRequest() throws JSONException, AuthFailureError {
		JSONObject json = new JSONObject();
		json.put("test", true);
		
		Request<?> request = ReplayAPIManager.request("testType", json);
		JSONObject body = new JSONObject(new String(request.getBody()));
		assertNotNull(body);
		// can not compare JSONObject directly
		//assertEquals(body,json);
		
		assertTrue(body.has("test"));
		assertEquals(body.getBoolean("test"), true);
		
		String url = request.getUrl();
		assertEquals(url, ReplayConfig.REPLAY_URL+"testType");
		
		
		
	}
	
}
