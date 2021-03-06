package io.replay.framework;

import android.util.Pair;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;

/**
 * Class that is used to persist information to the database and attempts to POST info to API.
 * If the POST fails, it will retry up to 20 times.
 */
class ReplayJob extends Job implements Serializable {
    private final static String TAG = ReplayJob.class.getSimpleName();
    private static final Params params = new Params(1).persist().groupBy("replayJob").requireNetwork();
    private ReplayRequest request;

    ReplayJob(ReplayRequest request) {
        super(params);
        this.request = request;
    }

    @Override
    public void onAdded() {
        ReplayLogger.d(TAG, "Added %s to queue", request.toString());
    }

    @Override
    public void onRun() throws Throwable {
        ReplayRequestFactory.updateTimestamp(request);
        Pair<Integer, String> result;
        try { //called on JobConsumerExecutor thread, which is NOT the JobQueue thread
            result = ReplayNetworkManager.doPost(request);
        } catch (Exception e) {
            ReplayLogger.e(e, "Error while POSTing job to Replay server: ");
            throw e;
        }

        if (result.first == HttpURLConnection.HTTP_OK) {
            ReplayLogger.d("ReplayIO - successfully sent job to server %s", result.first);
        } else {  //server error; log
            ReplayLogger.e(TAG, "ReplayIO server error code: %s\nmessage: %s", result.first, result.second);
        }
    }

    @Override
    public void onCancel() {
        ReplayLogger.d(TAG, "Cancelled Request: %s", request.toString());
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReplayJob)) return false;

        ReplayJob replayJob = (ReplayJob) o;

        return !(request != null ? !request.equals(replayJob.request) : replayJob.request != null);
    }

    @Override
    public int hashCode() {
        return request != null ? request.hashCode() : 0;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeObject(request);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        request = (ReplayRequest) ois.readObject();
    }
}
