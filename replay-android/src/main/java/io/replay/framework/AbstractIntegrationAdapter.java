package io.replay.framework;

import android.content.Context;
import android.os.Bundle;

import java.util.Map;

public abstract class AbstractIntegrationAdapter implements IntegrationOperation {

    final protected boolean enableLogging;

    public AbstractIntegrationAdapter(boolean enableLogging) {
        this.enableLogging = enableLogging;
    }

    /** Initializes the integration. This is where the API key should be provided,
     * either programmatically or using XML, á la the replayio.xml.
     *
     * @param context
     * @return true if the Integration Adapter was properly initialized, or false if the integration
     * should be removed.
     */
    public abstract boolean init(Context context);
    public abstract void track(String event, Map<String, ?> data);
    public abstract void traits(Object... data);
    public abstract void identify(String id);

    public abstract void onActivityCreate(Context context, Bundle savedInstanceState);
    public abstract void onActivityStart(Context context);
    public abstract void onActivityResume(Context context);
    public abstract void onActivityPause(Context context);
    public abstract void onActivityStop(Context context);

    public abstract void flush();


    /**allows integration to be enabled/disabled
     *
     * @param enable
     */
    public abstract void enable(boolean enable);
}
