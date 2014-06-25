package io.replay.framework;

import org.json.JSONObject;

public class ReplayRequest {

    private String type;
    private JSONObject json;

    /**
     * ReplayRequest represent a request that's ready to be sent to replay.io server.
     * @param type The data type of the request, it can either be {@value io.replay.framework.ReplayConfig#REQUEST_TYPE_ALIAS} or {@value io.replay.framework.ReplayConfig#REQUEST_TYPE_EVENTS}.
     * @param json The JSON data to be sent.
     */
    public ReplayRequest(String type, JSONObject json) {
        this.type = type;
        this.json = json;
    }

    /**
     *
     * @return The type of request data.
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type The type of request data.  {@value io.replay.framework.ReplayConfig#REQUEST_TYPE_ALIAS} or {@value io.replay.framework.ReplayConfig#REQUEST_TYPE_EVENTS}.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return The json data to be sent.
     */
    public JSONObject getJson() {
        return json;
    }

    /**
     *
     * @param json The json data to be sent.
     */
    public void setJson(JSONObject json) {
        this.json = json;
    }

    /**
     *
     * @return The json data to be sent, in byte array.
     */
    public byte[] getBody() {
        return json.toString().getBytes();
    }
}
