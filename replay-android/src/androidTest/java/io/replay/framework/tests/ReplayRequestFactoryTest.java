package io.replay.framework.tests;


import android.content.Context;
import android.test.AndroidTestCase;

import org.json.JSONException;

import java.util.UUID;

import io.replay.framework.ReplayConfig;
import io.replay.framework.ReplayIO;
import io.replay.framework.ReplaySessionManager;
import io.replay.framework.model.ReplayRequestFactory;
import io.replay.framework.util.ReplayPrefs;
import io.replay.framework.model.ReplayRequest;
import io.replay.framework.model.ReplayJsonObject;
import org.json.JSONObject;

public class ReplayRequestFactoryTest extends AndroidTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testRequestForEvent() throws InterruptedException, JSONException{
        Context context = getContext();
        ReplayIO.init(getContext(), "key");

        final Context appContext = context.getApplicationContext(); //cache locally for performance reasons

        // load the default settings
        ReplayPrefs prefs = ReplayPrefs.get(appContext);
        String uuid = UUID.randomUUID().toString();
        prefs.setClientID(uuid);
        prefs.setDistinctID("distinct");

        //create new SessionID
        ReplaySessionManager.getOrCreateSessionUUID(appContext);

        // initialize RequestFactory
        ReplayRequestFactory.init(appContext);
        ReplayRequest rq = ReplayRequestFactory.requestForEvent("Event",null);
        Thread.sleep(1000);
        ReplayRequestFactory.mergePassiveData(rq);

        assertEquals(ReplayConfig.RequestType.EVENTS,rq.getType());
        assertEquals("distinct",rq.getJsonBody().get("distinct_id"));
        assertEquals(uuid,rq.getJsonBody().get("client_id"));
        assertEquals("Event",rq.getJsonBody().get("event_name"));
        assertEquals("key",rq.getJsonBody().get("replay_key"));

        assertEquals((System.nanoTime()-rq.getCreatedAt())/10000000L,rq.getJsonBody().getJsonObject("properties").getLong("timestamp")/10L);
    }

    public void testPassiveData() throws InterruptedException, JSONException{
        Context context = getContext();
        ReplayIO.init(getContext(), "key");

        final Context appContext = context.getApplicationContext(); //cache locally for performance reasons

        // load the default settings
        ReplayPrefs prefs = ReplayPrefs.get(appContext);
        String uuid = UUID.randomUUID().toString();
        prefs.setClientID(uuid);
        prefs.setDistinctID("distinct");

        //create new SessionID
        ReplaySessionManager.getOrCreateSessionUUID(appContext);

        // initialize RequestFactory
        ReplayRequestFactory.init(appContext);
        ReplayRequest rq = ReplayRequestFactory.requestForEvent("Event",null);
        Thread.sleep(1000);
        ReplayRequestFactory.mergePassiveData(rq);

        JSONObject properties = rq.getJsonBody().getJsonObject("properties");

        assertNotNull(properties.get("device_model"));
        assertNotNull(properties.get("device_manufacturer"));
        assertNotNull(properties.get("client_os"));
        assertNotNull(properties.get("client_sdk"));
        assertNotNull(properties.get("display"));

        JSONObject network = properties.getJSONObject("network");
        assertNotNull(network.get("mobile"));
        assertNotNull(network.get("wifi"));
        assertNotNull(network.get("carrier"));

    }

}
