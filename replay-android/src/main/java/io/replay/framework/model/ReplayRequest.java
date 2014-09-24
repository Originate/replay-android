package io.replay.framework.model;

import org.json.JSONException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import io.replay.framework.ReplayConfig.RequestType;

public class ReplayRequest implements Serializable {

    private RequestType type;
    private ReplayJsonObject json;
    private long createdAt;

    /**
     * ReplayRequest represent a request that's ready to be sent to Replay.io server.
     * @param type The data type of the request, it can either be {@link RequestType#ALIAS} or {@link RequestType#EVENTS}.
     * @param json The JSON data to be sent.
     */
    public ReplayRequest(RequestType type, ReplayJsonObject json) {
        this.type = type;
        this.json = json != null ? json : new ReplayJsonObject();
        createdAt = System.nanoTime();
    }

    public ReplayJsonObject getJsonBody(){
        return json;
    }

    public void setJsonBody(ReplayJsonObject json){
        if(json == null) {
            json = new ReplayJsonObject();
        }
        this.json = json;
    }

    /** @return The type of request data. */
    public RequestType getType() {
        return type;
    }

    /**
     * @return The json data to be sent, in byte array.
     */
    public byte[] getBytes() {
        return json.toString().getBytes();
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeObject(type);
        oos.writeObject(json);
        oos.writeLong(createdAt);

    }
    private void readObject(ObjectInputStream ois) throws IOException, JSONException, ClassNotFoundException {
        type = (RequestType) ois.readObject();
        json = (ReplayJsonObject) ois.readObject();
        createdAt = ois.readLong();
    }

    @Override
    public String toString() {
        return "ReplayRequest{" +"type=" + type +", json=" + json +'}';
    }

    public long getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReplayRequest)) return false;

        ReplayRequest that = (ReplayRequest) o;

        if (json != null ? !json.equals(that.json) : that.json != null) return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (json != null ? json.hashCode() : 0);
        return result;
    }
}
