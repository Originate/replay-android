package io.replay.framework;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ReplayAPIManager implements ReplayConfig {

    private String apiKey;
    private String clientUUID;
    private String sessionUUID;
    private String distinctId;

    public enum Result {
        CONNECTION_ERROR, FAILED, SUCCESS, UNKNOWN;
    }

    /**
     * ReplayAPIManager is a wrapper of HttpURLConnection, it provides easier way to send requests.
     *
     * @param apiKey      The Replay.IO API key.
     * @param clientUUID  The client UUID.
     * @param sessionUUID The session UUID.
     * @param distinctId  The distinct identity.
     */
    public ReplayAPIManager(String apiKey, String clientUUID, String sessionUUID, String distinctId) {
        this.apiKey = apiKey;
        this.clientUUID = clientUUID;
        this.sessionUUID = sessionUUID;
        this.distinctId = distinctId;
        ReplayIO.debugLog("Tracking with { API Key: " + apiKey + ", Client UUID: " + clientUUID
                + ", Session UUID: " + sessionUUID + ", Distinct ID:" + distinctId);
    }

    /**
     * Update the session UUID.
     *
     * @param sessionUUID The session UUID.
     */
    public void updateSessionUUID(String sessionUUID) {
        this.sessionUUID = sessionUUID;
        ReplayIO.debugLog("Session UUID: " + sessionUUID);
    }

    /**
     * Build the ReplayRequest object for an event request.
     *
     * @param event The event name.
     * @param data  The name-value paired data.
     * @return ReplayRequest object.
     * @throws JSONException
     */
    public ReplayRequest requestForEvent(String event, Map<String, String> data) throws JSONException {
        return new ReplayRequest(REQUEST_TYPE_EVENTS, jsonForEvent(event, data));

    }

    /**
     * Build the ReplayRequest object for an alias request.
     *
     * @param alias The alias.
     * @return ReplayRequest object.
     * @throws JSONException
     */
    public ReplayRequest requestForAlias(String alias) throws JSONException {
        return new ReplayRequest(REQUEST_TYPE_ALIAS, jsonForAlias(alias));
    }

    /**
     * Post the request to server.
     *
     * @param request The request to be sent.
     * @return {@link Result#SUCCESS} if request is successfully posted, {@link Result#CONNECTION_ERROR}
     * if there's a connection issue, {@link Result#FAILED} if server returns something other than 200,
     * {@link Result#UNKNOWN} otherwise.
     */
    public Result doPost(ReplayRequest request) {
        Result result = Result.UNKNOWN;
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

                result = Result.SUCCESS;
            } else {
                result = Result.FAILED;
                ReplayIO.errorLog(connection.getResponseMessage());
            }

        } catch (MalformedURLException e) {
            result = Result.CONNECTION_ERROR;
            e.printStackTrace();
        } catch (ProtocolException e) {
            result = Result.CONNECTION_ERROR;
            e.printStackTrace();
        } catch (IOException e) {
            result = Result.CONNECTION_ERROR;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return result;
    }

    /**
     * Generate the JSONObject for a event request.
     *
     * @param event The event name.
     * @param data  The name-value paired data.
     * @return The JSONObject of data.
     * @throws JSONException
     */
    private JSONObject jsonForEvent(String event, Map<String, String> data) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(KEY_REPLAY_KEY, apiKey);
        json.put(KEY_CLIENT_ID, clientUUID);
        json.put(KEY_SESSION_ID, sessionUUID);
        if (distinctId != null && !distinctId.equals("")) {
            json.put(KEY_DISTINCT_ID, distinctId);
        }
        json.put(KEY_EVENT_NAME, event);
        if (null == data) {
            data = new HashMap<String, String>();
        }
        data.put(KEY_EVENT_NAME, event);
        json.put(KEY_DATA, new JSONObject(data));
        return json;
    }

    /**
     * Generate the JSONObject for a alias request.
     *
     * @param alias The alias.
     * @return The JSONObject of data.
     * @throws JSONException
     */
    private JSONObject jsonForAlias(String alias) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(KEY_REPLAY_KEY, apiKey);
        json.put(KEY_CLIENT_ID, clientUUID);
        json.put(KEY_DISTINCT_ID, distinctId);
        json.put("alias", alias);
        return json;
    }
}
