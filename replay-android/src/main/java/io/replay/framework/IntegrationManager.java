package io.replay.framework;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Pair;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by parthpadgaonkar on 11/4/14.
 */
class IntegrationManager extends LooperThreadWithHandler {

    Set<AbstractIntegrationAdapter> integrations = new HashSet<AbstractIntegrationAdapter>();

    private boolean initialized = false;

    //lifecycle actions
    private static final int MSG_LIFECYCLE_CREATE = 0x0501;
    private static final int MSG_LIFECYCLE_START =  0x0502;
    private static final int MSG_LIFECYCLE_RESUME = 0x0503;
    private static final int MSG_LIFECYCLE_PAUSE =  0x0504;
    private static final int MSG_LIFECYCLE_STOP =   0x0505;

    //library actions
    private static final int MSG_TRACK =    0x0601;
    private static final int MSG_IDENTIFY = 0x0602;
    private static final int MSG_TRAITS =   0x0603;
    private static final int MSG_FLUSH =    0x0604;

    Callback CALLBACK;

    private IntegrationManager(Context context, boolean debugEnabled) {
        super(IntegrationManager.class.getSimpleName(), );

/*
        if(classPathExists("test.test.test")){
            integrations.add(new TestIntegration(debugEnabled));
        }
*/
        if (!initialized) {
            for (Iterator<AbstractIntegrationAdapter> iterator = integrations.iterator(); iterator.hasNext(); ) {
                AbstractIntegrationAdapter adapter = iterator.next();

                if (!adapter.init(context)) {
                    iterator.remove();
                }
            }
        }

        this.start();
    }

    void track(String eventName, Map<String, ?> data){
        Handler handler = handler();
        if(handler!= null){
            handler.sendMessage(handler.obtainMessage(MSG_TRACK, Pair.create(eventName, data)));
        }
    }

    void track(String eventName, Object[] data){
        track(eventName, Util.varArgsToMap(data));
    }




    private boolean classPathExists(String fullName){
        try {
            Class.forName(fullName);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }


}
