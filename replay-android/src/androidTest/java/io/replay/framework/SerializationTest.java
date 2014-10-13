package io.replay.framework;

import android.test.AndroidTestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

/**
 * Created by parthpadgaonkar on 9/22/14.
 */
public class SerializationTest extends AndroidTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSerializeEmptyReplayJsonObject() throws Exception {
        ReplayJsonObject json = new ReplayJsonObject();

        byte[] bytes = serialize(json);

        assertNotNull(bytes);

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
        ReplayJsonObject deser = deserialize(bytes, ReplayJsonObject.class);
        in.close();

        assertNotNull(deser);
        assertEquals(deser, json);
    }

    public void testSerializeFullReplayJsonObject() throws Exception {
        ReplayJsonObject json = new ReplayJsonObject();
        json.put("event_name", "test");
        json.put("event_name2", "test");
        //json.put("json", json); <-- this line causes a stackoverflow

        byte[] bytes = serialize(json);

        assertNotNull(bytes);

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
        ReplayJsonObject deser = deserialize(bytes, ReplayJsonObject.class);
        in.close();

        assertNotNull(deser);
        assertEquals(deser, json);
    }

    public void testSerializeReplayRequest() throws Exception {
        ReplayRequest request = new ReplayRequest(ReplayRequest.RequestType.EVENTS, new ReplayJsonObject());

        byte[] bytes = serialize(request);
        assertNotNull(bytes);

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
        ReplayRequest deser = deserialize(bytes, ReplayRequest.class);
        in.close();

        assertNotNull(deser);
        assertEquals(deser, request);
    }
    public void testSerializeReplayJob() throws Exception {
        TestReplayJob job = new TestReplayJob(new ReplayRequest(ReplayRequest.RequestType.EVENTS, new ReplayJsonObject()));

        byte[] bytes = serialize(job);

        assertNotNull(bytes);

        TestReplayJob deser = deserialize(bytes, TestReplayJob.class);

        assertNotNull(deser);
        assertEquals(deser, job);
    }


    private static byte[] serialize(Object o) throws IOException {
        ObjectOutput out = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        out = new ObjectOutputStream(bos);
        out.writeObject(o);
        byte[] bytes = bos.toByteArray();
        bos.close();
        return bytes;
    }

    private <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
        T deser = (T) in.readObject();
        in.close();
        return deser;
    }
}
