package io.replay.framework;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class ReplayAPIManager implements ReplayConfig {

	private String apiKey;
	private String clientUUID;
	private String sessionUUID;

	public ReplayAPIManager(String apiKey, String clientUUID, String sessionUUID) {
		this.apiKey = apiKey;
		this.clientUUID = clientUUID;
		this.sessionUUID = sessionUUID;
		ReplayIO.debugLog("Tracking with { API Key: "+apiKey+", Client UUID: "+clientUUID+", Session UUID: "+sessionUUID);
	}
	
	public void updateSessionUUID(String sessionUUID) {
		this.sessionUUID = sessionUUID;
		ReplayIO.debugLog("Session UUID: "+sessionUUID);
	}
	
	public ReplayRequest requestForEvent(String event, Map<String, String> data) throws JSONException {
		return new ReplayRequest(REQUEST_TYPE_EVENTS, jsonForEvent(event, data));
			
	}
	
	public ReplayRequest requestForAlias(String alias) throws JSONException {
		return new ReplayRequest(REQUEST_TYPE_ALIAS, jsonForAlias(alias));
	}
	
	public boolean doPost(ReplayRequest request) {
		boolean success = false;
		HttpURLConnection connection = null;
		URL url;
		try {
			url = new URL(REPLAY_URL + request.getType());
			connection = (HttpURLConnection) url.openConnection();
			//connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length", "" + Integer.toString(request.getBody().length));
			connection.setUseCaches(false);
			
			DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
			dos.write(request.getBody());
			dos.flush();
			dos.close();
			
			int httpResult = connection.getResponseCode();
			if (httpResult == HttpURLConnection.HTTP_OK) {
				/*StringBuilder sb = new StringBuilder();
				InputStream is = connection.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
				
				ReplayIO.debugLog(""+sb.toString());*/
				
				success = true;
			} else {
				ReplayIO.errorLog(connection.getResponseMessage());
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		
		return success;
	}
	
	private JSONObject jsonForEvent(String event, Map<String, String> data) throws JSONException {
		JSONObject json = new JSONObject();
		json.put(KEY_REPLAY_KEY, apiKey);
		json.put(KEY_CLIENT_ID, clientUUID);
		json.put(KEY_SESSION_ID, sessionUUID);
		if (null == data) {
			data = new HashMap<String, String>();
		}
		data.put("event", event);
		json.put(KEY_DATA, new JSONObject(data));
		return json;
	}
	
	private JSONObject jsonForAlias(String alias) throws JSONException {
		JSONObject json = new JSONObject();
		json.put(KEY_REPLAY_KEY, apiKey);
		json.put(KEY_CLIENT_ID, clientUUID);
		json.put("alias", alias);
		return json;
	}
}
