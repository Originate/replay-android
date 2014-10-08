package io.replay.framework;

import android.util.Pair;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**The ReplayNetworkManager is a simple wrapper around the Android/Java {@link java.net.HttpURLConnection}.
 * appropriate endpoint.
 *
 */
class ReplayNetworkManager {

    /**
     * Post the request to the server. This operation occurs in the thread that called it.
     *
     * @param request The request to be sent.
     * @return a {@link android.util.Pair}, where the first parameter is an Integer representing
     * {@link java.net.HttpURLConnection#getResponseCode()}, and the
     * second being {@link java.net.HttpURLConnection#getResponseMessage()}
     */
    static Pair<Integer, String> doPost(ReplayRequest request) throws IOException {
        final byte[] jsonBody = request.getBytes();

        URL url = new URL(BuildConfig.REPLAY_URL + request.getType().toString());

        ReplayLogger.d("","POSTing to ", url.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setFixedLengthStreamingMode(jsonBody.length);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("charset", "utf-8");
        connection.setRequestProperty("Content-Length", Integer.toString(jsonBody.length));
        connection.setUseCaches(false);

        BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
        bos.write(jsonBody);
        bos.flush();
        bos.close();
        return Pair.create(connection.getResponseCode(), connection.getResponseMessage());
    }
}
