package io.replay.framework;

import org.json.JSONObject;

public class ReplayRequest {

	private String type;
	private JSONObject json;
	private ReplayRequestQueue queue;
	
	public ReplayRequest(String type, JSONObject json) {
		this.type = type;
		this.json = json;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public JSONObject getJson() {
		return json;
	}
	public void setJson(JSONObject json) {
		this.json = json;
	}
	public ReplayRequestQueue getRequestQueue() {
		return queue;
	}
	public void setRequestQueue(ReplayRequestQueue queue) {
		this.queue = queue;
	}
	public byte[] getBody() {
		return json.toString().getBytes();
	}
	
	public void finish() {
		if (queue != null) {
			queue.finish(this);
		}
	}
}
