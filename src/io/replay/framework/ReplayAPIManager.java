package io.replay.framework;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import io.replay.framework.model.ReplayRequest;

public class ReplayAPIManager implements ReplayConfig {

    public enum Result {
        CONNECTION_ERROR, FAILED, SUCCESS, UNKNOWN;
    }

    /**
     * Post the request to server.
     *
     * @param request The request to be sent.
     * @return {@link Result#SUCCESS} if request is successfully posted, {@link Result#CONNECTION_ERROR}
     * if there's a connection issue, {@link Result#FAILED} if server returns something other than 200,
     * {@link Result#UNKNOWN} otherwise.
     */
    public Result doPost(ReplayRequest request) {
        Result result = Result.UNKNOWN;
        HttpURLConnection connection = null;
        URL url;
        try {
            url = new URL(REPLAY_URL + request.getType());
            connection = (HttpURLConnection) url.openConnection();
            //connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", "" + Integer.toString(request.getBody().length));
            connection.setUseCaches(false);

            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            dos.write(request.getBody());
            dos.flush();
            dos.close();

            int httpResult = connection.getResponseCode();
            if (httpResult == HttpURLConnection.HTTP_OK) {
                /*StringBuilder sb = new StringBuilder();
				InputStream is = connection.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
				
				ReplayIO.debugLog(""+sb.toString());*/

                result = Result.SUCCESS;
            } else {
                result = Result.FAILED;
                ReplayIO.errorLog(connection.getResponseMessage());
            }

        } catch (MalformedURLException e) {
            result = Result.CONNECTION_ERROR;
            e.printStackTrace();
        } catch (ProtocolException e) {
            result = Result.CONNECTION_ERROR;
            e.printStackTrace();
        } catch (IOException e) {
            result = Result.CONNECTION_ERROR;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return result;
    }
}
