package io.replay.framework;


import android.content.Context;
import android.test.AndroidTestCase;

import org.json.JSONException;

import java.util.HashMap;
import java.util.UUID;

import io.replay.framework.ReplayRequest.RequestType;

public class ReplayRequestFactoryTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testRequestCreation() throws JSONException {
        Context context = getContext();
        ReplayIO.init(getContext(), "key");

        final Context appContext = context.getApplicationContext();
        final String distinct = "distinct";
        final String event = "event";
        final String deviceInfo = "passiveEvents";
        final String properties = "properties";

        // load the default settings
        ReplayPrefs prefs = ReplayPrefs.get(appContext);
        String uuid = UUID.randomUUID().toString();
        prefs.setClientID(uuid);
        prefs.setDistinctID(distinct);

        //create new SessionID
        ReplaySessionManager.getOrCreateSessionUUID(appContext);

        ReplayRequest request = ReplayRequestFactory.createRequest(context, RequestType.EVENTS, event, new ReplayJsonObject(deviceInfo, "test"), new HashMap());

        final ReplayJsonObject jsonBody = request.getJsonBody();

        assertEquals(RequestType.EVENTS, request.getType());
        assertEquals(uuid, jsonBody.get("client_id"));
        assertEquals(event, jsonBody.get("event_name"));
        assertEquals("key", jsonBody.get("replay_key"));
        assertEquals("test", jsonBody.getJsonObject(properties).getString(deviceInfo));

        assertEquals((System.nanoTime() - request.getCreatedAt()) / 10000000L,
                          jsonBody.getJsonObject(properties).getLong("timestamp") / 10L);
    }
}
