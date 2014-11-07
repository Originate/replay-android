package io.replay.framework;

import android.content.Context;
import android.os.Bundle;

import java.util.Map;

interface IntegrationOperation {
    void track(String event, Map<String, ?> data);
    void traits(Object... data);
    void identify(String id);

    void onActivityCreate(Context context, Bundle savedInstanceState);
    void onActivityStart(Context context);
    void onActivityResume(Context context);
    void onActivityPause(Context context);
    void onActivityStop(Context context);

    void flush();
}
