package io.replay.framework;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import io.replay.framework.ReplayAPIManager.Result;

public class ReplayQueue {

    /**
     * Directory name persisted requests will be saved to
     */
    private static final String PERSIST_DIR = "persist";
    /**
     * Indicates if the queue is running
     */
    private volatile boolean isRunning;
    /**
     * All requests that current in queue
     */
    private final List<ReplayRequest> mRequests = new LinkedList<ReplayRequest>();
    /**
     * API helper
     */
    private ReplayAPIManager mManager;
    /**
     * Handler for delayed actions
     */
    private Handler mHandler;
    /**
     * Runnable for the next dispatch
     */
    private Runnable runnable;
    /**
     * Dispatch interval in seconds
     */
    private volatile int dispatchInterval;
    /**
     * Indicating if timer is running
     */
    private boolean timing;
    /**
     * Indicating if a request is getting processed
     */
    private boolean dequeueing;

    /**
     * Constructs the queue and gets the dispatcher ready.
     *
     * @param manager The API manager dealing with requests.
     */
    public ReplayQueue(ReplayAPIManager manager) {
        mManager = manager;
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Get the a dispatch pending.
     */
    public void start() {
        startTimerIfNeeded();

        isRunning = true;
    }

    /**
     * Stop the queue and stop processing the requests in the queue.
     */
    public void stop() {
        stopTimer();

        isRunning = false;
    }


    /**
     * Set the dispatch interval.
     *
     * @param interval The dispatch interval in seconds.
     */
    public void setDispatchInterval(int interval) {
        dispatchInterval = interval;
    }

    /**
     * Tell if the {@link ReplayQueue} is running.
     *
     * @return True if running, false otherwise.
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Add the request to the queue.
     *
     * @param request The request to be enqueued.
     */
    public void enqueue(ReplayRequest request) {
        synchronized (mRequests) {
            mRequests.add(request);
        }

        startTimerIfNeeded();

        ReplayIO.debugLog("Enqueue request (" + mRequests.size() + " requests in queue)");
    }

    /**
     * Manually dispatch the requests in queue.
     */
    public void dispatch() {
        ReplayIO.debugLog("Manual dispatch");
        dequeue();
    }

    /**
     * Process the requests in queue, send the requests asynchronously.
     */
    private void dequeue() {
        ReplayIO.debugLog("Dequeueing requests...");

        if (!dequeueing && mRequests.size() > 0) {
            dequeueing = true;

            sendAsynchronousRequest(mRequests.get(0));
        } else {
            stopTimerIfUnneeded();

            if (dequeueing) {
                ReplayIO.debugLog("  ├── Can't dequeue - request already in progress");
            }
            if (mRequests.size() == 0) {
                ReplayIO.debugLog("  ├── Empty queue");
            }
        }
    }

    private void sendNextRequest() {
        if (mRequests.size() > 0) {
            sendAsynchronousRequest(mRequests.get(0));
        }
    }

    /**
     * Send the request in a separate thread.
     * @param request The request to be sent.
     */
    private void sendAsynchronousRequest(ReplayRequest request) {
        ReplayIO.debugLog("  ├── Sending request...");
        new PostRequestTask().execute(request);

    }

    private void startTimerIfNeeded() {
        if (dispatchInterval > 0 && mRequests.size() > 0) {
            if (!timing) {
                if (null == runnable) {
                    runnable = new Runnable() {

                        @Override
                        public void run() {
                            dequeue();
                        }

                    };
                }
                mHandler.postDelayed(runnable, dispatchInterval * 1000);
                timing = true;
            }
        } else if (dispatchInterval == 0) {
            stopTimer();
            dequeue();
        }
    }

    /**
     * Stop timer, by remove delayed runnable from Handler.
     */
    private void stopTimer() {
        mHandler.removeCallbacks(runnable);
        timing = false;
    }

    /**
     * Stop timer if dispatchInterval is negative or requests queue is empty.
     */
    private void stopTimerIfUnneeded() {
        if (dispatchInterval <= 0 || mRequests.size() == 0) {
            stopTimer();
        }
    }


    /**
     * Store the request in queue to disk.
     *
     * @throws IOException
     */
    public void saveQueueToDisk(Context context) throws IOException {
        File cacheDir = new File(context.getCacheDir(), PERSIST_DIR);
        if (!cacheDir.exists()) {
            if (!cacheDir.mkdirs()) {
                ReplayIO.debugLog("Unable to create persist dir " + cacheDir.getAbsolutePath());
            }
        }

        synchronized (mRequests) {
            int count = 0;
            int totalCount = 0;
            int fileCount = 0;
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(cacheDir, "requests" + fileCount)));
            for (ReplayRequest request : mRequests) {
                byte[] body = request.getBody();
                bw.write(new String(body));
                bw.newLine();

                count++;
                totalCount++;
                if (count >= 99) {
                    count = 0;
                    fileCount++;
                    bw.flush();
                    bw.close();
                    bw = new BufferedWriter(new FileWriter(new File(cacheDir, "requests" + fileCount)));
                }
            }
            bw.flush();
            bw.close();
            ReplayIO.debugLog("Persisted " + totalCount + " requests");
        }
    }

    /**
     * Load the stored events into queue.
     *
     * @throws IOException
     * @throws JSONException
     */
    public void loadQueueFromDisk(Context context) throws IOException, JSONException {
        File cacheDir = new File(context.getCacheDir(), PERSIST_DIR);
        File[] files = cacheDir.listFiles();
        if (files == null) {
            return;
        }

        int count = 0;
        for (File file : files) {
            BufferedReader br = new BufferedReader(new FileReader(file));
            for (String line; (line = br.readLine()) != null; file.delete()) {
                JSONObject json = new JSONObject(line);
                ReplayRequest request = null;
                if (json.has(ReplayConfig.KEY_DATA)) {
                    request = new ReplayRequest(ReplayConfig.REQUEST_TYPE_EVENTS, json);
                } else if (json.has(ReplayConfig.REQUEST_TYPE_ALIAS)) {
                    request = new ReplayRequest(ReplayConfig.REQUEST_TYPE_ALIAS, json);
                }
                if (request != null) {
                    mRequests.add(request);
                    count++;
                }
            }
            br.close();
        }
        ReplayIO.debugLog("Loaded " + count + " requests");
    }


    /**
     * Sent the request in a AsyncTask,process the next request if this one is success.
     */
    private class PostRequestTask extends AsyncTask<ReplayRequest, Void, Result> {

        private ReplayRequest request;

        @Override
        protected Result doInBackground(ReplayRequest... params) {
            request = params[0];
            return mManager.doPost(request);
        }

        @Override
        protected void onPostExecute(Result result) {
            // success - remove request from queue and process next item
            if (result == ReplayAPIManager.Result.SUCCESS) {
                boolean success = mRequests.remove(request);
                ReplayIO.debugLog("Remove success? " + success);
                stopTimerIfUnneeded();
                ReplayIO.debugLog("  │    └── Sent successfully (" + mRequests.size() + " left)");

                if (mRequests.size() > 0) {
                    sendNextRequest();
                    return;
                }
            }
            // failure - wait for Reachability notification to call dequeue
            else {
                ReplayIO.debugLog("  │    └── Sent failure " + result + " (" + mRequests.size() + " left)");
            }

            // stop dequeueing when the queue is empty or the request is failed.
            dequeueing = false;
            timing = false;
        }

    }
}
