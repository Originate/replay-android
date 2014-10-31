package io.replay.framework;

import org.json.JSONException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

class ReplayRequest implements Serializable {

    static enum RequestType{
        /** The ReplayRequest type: traits. */
        TRAITS,
        /** The ReplayRequest type: events. */
        EVENTS;

        public @Override String toString() {
            return name().toLowerCase();
        }
    }

    private RequestType type;
    private ReplayJsonObject json;
    private long createdAt;

    /**
     *ReplayRequest represent a request that's ready to be sent to Replay.io server.
     * @param type The data type of the request, it can either be {@link RequestType#TRAITS} or {@link RequestType#EVENTS}.
     * @param json The JSON data to be sent.
     */
    ReplayRequest(RequestType type, ReplayJsonObject json) {
        this.type = type;
        this.json = json != null ? json : new ReplayJsonObject();
        createdAt = System.nanoTime(); //TODO potential bug - if the device is rebooted, this time will be invalid.
    }

    ReplayJsonObject getJsonBody(){
        return json;
    }

    void setJsonBody(ReplayJsonObject json){
        if(json == null) {
            json = new ReplayJsonObject();
        }
        this.json = json;
    }

    /** @return The type of request data. */
    RequestType getType() {
        return type;
    }

    /**
     * @return The json data to be sent, in byte array.
     */
    byte[] getBytes() {
        return json.toString().getBytes();
    }

    @Override
    public String toString() {
        return "ReplayRequest{" +"type=" + type +", json=" + json +'}';
    }

    long getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReplayRequest)) return false;

        ReplayRequest that = (ReplayRequest) o;

        return !((json != null ? !json.equals(that.json) : that.json != null) || type != that.type);

    }

    @Override
    public int hashCode() {
        return 31 * (type != null ? type.hashCode() : 0) + (json != null ? json.hashCode() : 0);
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
}
