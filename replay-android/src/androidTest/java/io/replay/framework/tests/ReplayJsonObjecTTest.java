package io.replay.framework.tests;

import android.test.AndroidTestCase;

import org.json.JSONArray;

import io.replay.framework.model.ReplayJsonObject;

/**
 * Created by parthpadgaonkar on 9/23/14.
 */
public class ReplayJsonObjecTTest extends AndroidTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testShallowObjectEquals() throws Exception {
        ReplayJsonObject one = new ReplayJsonObject();
        ReplayJsonObject two = new ReplayJsonObject();


        assertEquals(one, two);
        assertTrue(one.equals(two) && two.equals(one));

        for (int i = 0; i < 3; i++) {
            one.put("event" + i, i + "," + i * i);
            two.put("event" + i, i + "," + i * i);
        }

        assertEquals(one, two);
        assertTrue(one.equals(two) && two.equals(one));

        two.put("one more", "event");

        assertFalse(one.equals(two));
    }

    public void testDeepObjectEquals() throws Exception {
        ReplayJsonObject one = new ReplayJsonObject();
        ReplayJsonObject two = new ReplayJsonObject();

        assertEquals(one, two);
        assertTrue(one.equals(two) && two.equals(one));

        ReplayJsonObject base = new ReplayJsonObject();

        for (int i = 0; i < 3; i++) {
            one.put("event" + i, i + "," + i * i);
            two.put("event" + i, i + "," + i * i);
            base.put("event", i);
        }
        one.put("base", base);
        two.put("base", base);

        assertEquals(one, two);
        assertTrue(one.equals(two) && two.equals(one));

        two.put("one more", "event");

        assertFalse(one.equals(two));
    }

    public void testDeepObjectNotEquals() throws Exception {
        ReplayJsonObject one = new ReplayJsonObject();
        ReplayJsonObject two = new ReplayJsonObject();

        assertEquals(one, two);
        assertTrue(one.equals(two) && two.equals(one));

        ReplayJsonObject base = new ReplayJsonObject();
        JSONArray base2 = new JSONArray();

        one.put("base", base);
        two.put("base", base2);

        assertFalse(one.equals(two));
    }
}
