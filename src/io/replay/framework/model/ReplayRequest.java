package io.replay.framework.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import io.replay.framework.ReplayConfig.RequestType;

public class ReplayRequest implements Serializable {

    private RequestType type;
    private JSONObject json;

    /**
     * ReplayRequest represent a request that's ready to be sent to replay.io server.
     * @param type The data type of the request, it can either be {@link RequestType#ALIAS} or {@link RequestType#EVENTS}.
     * @param json The JSON data to be sent.
     */
    public ReplayRequest(RequestType type, JSONObject json) {
        this.type = type;
        this.json = json;
    }

    /** @return The type of request data. */
    public RequestType getType() {
        return type;
    }

    /**
     * @return The json data to be sent, in byte array.
     */
    public byte[] getBody() {
        return json.toString().getBytes();
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeObject(type);
        oos.writeUTF(json.toString());

    }
    private void readObject(ObjectInputStream ois) throws IOException, JSONException, ClassNotFoundException {
        type = (RequestType) ois.readObject();
        json = new JSONObject(ois.readUTF());
    }

    @Override
    public String toString() {
        return "ReplayRequest{" +"type=" + type +", json=" + json +'}';
    }
}
