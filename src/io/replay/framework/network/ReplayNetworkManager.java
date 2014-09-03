package io.replay.framework.network;

import android.util.Pair;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import io.replay.framework.ReplayConfig;
import io.replay.framework.model.ReplayRequest;

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
        byte[] jsonBody = request.getBody();

        HttpURLConnection connection = null;
        URL url;
        url = new URL(REPLAY_URL + request.getType());
        connection = (HttpURLConnection) url.openConnection();
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

        return new Pair<Integer, String>(connection.getResponseCode(), connection.getResponseMessage());
    }
}
