package io.replay.framework;

import android.content.Context;
import android.test.AndroidTestCase;

import org.json.JSONException;
import org.json.JSONObject;

import io.replay.framework.QueueLayer.InfoManager;

public class ReplayQueueLayerTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testEnqueueJob() throws JSONException, InterruptedException{
        Context context = getContext();

        // load parameters
        Config mConfig = ReplayParams.getOptions(context.getApplicationContext());
        mConfig.setApiKey("testKey");
        ReplayQueue queue = new ReplayQueue(context, mConfig);
        queue.clear();
        QueueLayer ql = new QueueLayer(queue, context);
        ql.start();

        //Queue should be empty
        assertEquals(0,queue.count());

        //check to make sure events can be enqueued
        ReplayJsonObject json = new ReplayJsonObject();
        json.put("event_name", "test");

        ql.enqueueJob(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));
        Thread.sleep(1000);
        assertEquals(1,queue.count());
    }

    public void testFlush() throws InterruptedException{
        Context context = getContext();

        // load parameters
        Config mConfig = ReplayParams.getOptions(context.getApplicationContext());
        mConfig.setApiKey("testKey");        ReplayQueue queue = new ReplayQueue(context, mConfig);
        queue.clear();
        QueueLayer ql = new QueueLayer(queue, context);
        ql.start();

        //Queue should be empty
        assertEquals(0,queue.count());

        //enqueue event
        ReplayJsonObject json = new ReplayJsonObject();
        json.put("event_name", "test");
        ql.enqueueJob(new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, json)));

        //check to make sure events can be flushed
        ql.sendFlush();
        Thread.sleep(2000);

        assertEquals(0,queue.count());
    }


    public void testPassiveData() throws NoSuchFieldException, IllegalAccessException {
        Context context = getContext();
        // load parameters
        Config mConfig = ReplayParams.getOptions(context.getApplicationContext());
        mConfig.setApiKey("testKey");

        final ReplayJsonObject json = InfoManager.buildInfo(context, ReplayPrefs.get(context));
        assertNotNull(json.get(InfoManager.DISPLAY_KEY));
        assertNotNull(json.get(InfoManager.MODEL_KEY));
        assertNotNull(json.get(InfoManager.MANUFACTURER_KEY));
        assertNotNull(json.get(InfoManager.OS_KEY));
        assertNotNull(json.get(InfoManager.SDK_KEY));
        //depending on the test environment, location services might not exist. 
        /*if(context.checkCallingOrSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == PackageManager.PERMISSION_GRANTED) {
            assertNotNull(json.get(InfoManager.LOCATION_LAT));
            assertNotNull(json.get(InfoManager.LOCATION_LONG));
        }*/
        final JSONObject network = (JSONObject) json.get(InfoManager.NETWORK_KEY);
        assertNotNull(network);
        assertTrue(network.length() >0);
    }

}
