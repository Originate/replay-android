package io.replay.framework.model;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import io.replay.framework.util.ReplayLogger;

/**
 * Created by parthpadgaonkar on 8/27/14.
 */
public class ReplayJob extends Job implements Serializable {
    private final static String TAG = ReplayJob.class.getSimpleName();
    private static final Params params = new Params(1).persist().groupBy("replayJob").requireNetwork();
    private ReplayRequest request;

    public ReplayJob(ReplayRequest request) {
        super(params);
        this.request = request;
    }

    @Override
    public void onAdded() {
        ReplayLogger.d(TAG, "Added %s to queue", request.toString());
    }

    @Override
    public void onRun() throws Throwable {
        //TODO network request
    }

    @Override
    public void onCancel() {
        ReplayLogger.d(TAG, "Cancelled %s", request.toString());
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return true;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeObject(request);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        request = (ReplayRequest) ois.readObject();
    }
}
