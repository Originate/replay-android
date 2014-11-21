package io.replay.framework;

import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

import io.replay.framework.testClasses.DummyLifecycleActivity1;
import io.replay.framework.testClasses.DummyLifecycleActivity2;

public class ReplayIOLifecycleTest extends ActivityInstrumentationTestCase2<DummyLifecycleActivity1> {

    private DummyLifecycleActivity1 dummyActivity1;
    private AtomicInteger activityCount;
    private Instrumentation instrumentation;

    static final Object LOCK = new Object();

    public ReplayIOLifecycleTest() {
        super(DummyLifecycleActivity1.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);

        dummyActivity1 = getActivity();
        activityCount = getReplayStaticFieldByReflection("activityCount", AtomicInteger.class);
        instrumentation = getInstrumentation();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOneActivityLifecycle() throws Exception {
        new Thread() { //hackish, but it's the only way for QueueLayer to be instantiated on its own thread
            @Override
            public void run() {
                super.run();
                assertNotNull(dummyActivity1);
                assertNotNull(activityCount);
                assertNotNull(instrumentation);

                assertEquals(1, activityCount.get()); //because DummyActivity1 called ReplayIO

                instrumentation.callActivityOnPause(dummyActivity1);
                assertEquals(1, activityCount.get()); //should still be 1


                instrumentation.callActivityOnStop(dummyActivity1);
                assertEquals(0, activityCount.get());

                boolean watchdog = false;
                try {
                    QueueLayer queueLayer = getReplayStaticFieldByReflection("queueLayer", QueueLayer.class);
                    assertNotNull(queueLayer);
                    final Field running = QueueLayer.class.getDeclaredField("running");
                    running.setAccessible(true);
                    assertFalse(running.getBoolean(queueLayer)); //final Activity.onStop should stop the QueueLayer

                    watchdog = getReplayStaticFieldByReflection("watchdogEnabled", boolean.class);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                assertTrue(watchdog);
            }
        };

    }

    public void testTwoActivityLifecycle() throws Exception {
        assertEquals(1, activityCount.get());
        dummyActivity1 = getActivity();
        assertEquals(1, activityCount.get());

        View top = dummyActivity1.findViewById(io.replay.framework.tests.R.id.ll_top);
        assertNotNull(top);
        Button button = (Button) ((LinearLayout) top).getChildAt(0);
        assertNotNull(button);

        final ActivityMonitor activityMonitor = instrumentation.addMonitor(DummyLifecycleActivity2.class
                                                                                 .getName(), null, false);

        TouchUtils.clickView(ReplayIOLifecycleTest.this, button);


        assertEquals(1, activityCount.get());
        final DummyLifecycleActivity2 dummyActivity2 = (DummyLifecycleActivity2) instrumentation.waitForMonitor(activityMonitor);
        assertNotNull(dummyActivity2);
        assertEquals(1, activityCount.get());
        //annoying, but the wait/notify pattern is necessary to ensure that the UI thread's actions are synchronized with the JUnit thread - at least for a few lines
       /* dummyActivity1.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                instrumentation.callActivityOnCreate(dummyActivity2, null);
                synchronized (LOCK) {
                    LOCK.notifyAll();
                }
            }
        });

        synchronized (LOCK) {
            LOCK.wait();*/
        activityCount = getReplayStaticFieldByReflection("activityCount", AtomicInteger.class);
        assertEquals(2, activityCount.get());
        // }


        //TODO add onStop functionality with back presses?

    }

    private <T> T getReplayStaticFieldByReflection(String fieldName, Class<T> type) throws NoSuchFieldException, IllegalAccessException {
        final Field field = ReplayIO.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        assertNotNull(field);
        return (T) field.get(null);
    }

}
