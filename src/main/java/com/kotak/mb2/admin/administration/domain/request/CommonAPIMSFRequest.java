package com.kotak.mb2.admin.administration.domain.request;

import org.hibernate.id.uuid.Helper;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;
import java.util.UUID;
import java.util.random.RandomGenerator;

public class CommonAPIMSFRequest extends JSONObject {

	private static String MSG_ID = "msgID";

	private static String APP_ID = "app_id";

	private static String REQUEST = "request";

	private static String DATA = "data";

	private JSONObject reqObj;

	private JSONObject dataObj;

	private String msgID;

	private String appId;

	private JSONObject echoObj = null;

	// For masking data
	private String request = null;

	private JSONObject forLogging;

	public CommonAPIMSFRequest(String request) throws JSONException {
		super(request);

		this.request = request;

		this.reqObj = this.getJSONObject(REQUEST);

		this.msgID = String.valueOf(UUID.randomUUID());  //Helper.generateMsgID();

		this.reqObj.put(MSG_ID, this.msgID);

		if (this.reqObj.has(APP_ID)) {
			this.appId = this.reqObj.getString(APP_ID);
		}

		if (this.has("echo")) {
			this.echoObj = this.getJSONObject("echo");
		}

	}

	public JSONObject getData() throws JSONException {
		// Lazy Loading
		if (this.dataObj == null)
			this.dataObj = this.reqObj.getJSONObject(DATA);

		return this.dataObj;
	}

	public JSONObject getRequest() {
		return this.reqObj;
	}

	public String getMsgID() {
		return this.msgID;
	}

	public String getAppID() {
		return this.appId;
	}

	public JSONObject getEchoObj() {
		return this.echoObj;
	}

	private void createLoggingObj() throws JSONException {
		if (this.forLogging == null) {
			this.forLogging = new JSONObject(this.request);
			this.forLogging.getJSONObject(REQUEST).put(MSG_ID, this.msgID);
		}
	}

	public void maskValueInData(String key) throws JSONException {
		createLoggingObj();

		JSONObject dataObject = this.forLogging.getJSONObject(REQUEST).getJSONObject(DATA);
		dataObject.put(key, "****");
	}

	public void maskValueInData(String[] keys) throws JSONException {
		if (keys == null)
			return;

		createLoggingObj();

		JSONObject dataObject = this.forLogging.getJSONObject(REQUEST).getJSONObject(DATA);
		for (String k : keys)
			dataObject.put(k, "****");
	}

	public String toS() {
		return getObjForLogging().toString();
	}

	public JSONObject getObjForLogging() {
		if (this.forLogging != null)
			return this.forLogging;

		return this;
	}

}
