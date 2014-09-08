package io.replay.framework.network;

import android.util.Pair;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import io.replay.framework.ReplayConfig;
import io.replay.framework.model.ReplayRequest;

/**The ReplayNetworkManager is a simple wrapper around the Android/Java {@link java.net.HttpURLConnection}.
 * Given a {@link io.replay.framework.model.ReplayRequest} object, this class will POST the JSON body to the
 * appropriate endpoint.
 *
 */
public class ReplayNetworkManager implements ReplayConfig {

    /**
     * Post the request to the server. This operation occurs in the thread that called it.
     *
     * @param request The request to be sent.
     * @return a {@link android.util.Pair}, where the first parameter is an Integer representing
     * {@link java.net.HttpURLConnection#getResponseCode()}, and the
     * second being {@link java.net.HttpURLConnection#getResponseMessage()}
     */
    public static Pair<Integer, String> doPost(ReplayRequest request) throws IOException {
        final byte[] jsonBody = request.getBody();

        URL url = new URL(REPLAY_URL + request.getType());
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
