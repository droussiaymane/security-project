package com.kotak.mb2.admin.administration.domain.response;

import com.kotak.mb2.admin.administration.domain.request.CommonAPIMSFRequest;
import org.json.JSONException;
import org.json.JSONObject;

public class CommonAPIMSFResponse extends JSONObject {


	protected JSONObject respObj;
	protected JSONObject dataObj;
	private String infoID = "0";
	private String infoMsg;
	private String svcGroup;
	private String svcName;
	private String appID;
	private String requestID;


	public CommonAPIMSFResponse() throws JSONException {
		this.respObj = new JSONObject();
		this.put("response", this.respObj);

		this.dataObj = new JSONObject();

	}

	public CommonAPIMSFResponse(CommonAPIMSFRequest msfRequest) throws JSONException {
		this();
		this.respObj.put("msgID", msfRequest.getMsgID());
		this.put("echo", msfRequest.getEchoObj());
	}

	public void addToData(String key, Object value) throws JSONException {
		this.dataObj.put(key, value);
	}

	public String getMsgID() {
		try {
			return this.getString("msgID");
		} catch (JSONException e) {
		}
		return null;
	}

	public String getInfoID() {
		return infoID;
	}

	public String getInfoMsg() {
		return infoMsg;
	}

	public String getSvcGroup() {
		return svcGroup;
	}

	public String getSvcName() {
		return svcName;
	}

	public String getAppID() {
		return appID;
	}

	public void setInfoID(String infoID) {
		this.infoID = infoID;
	}

	public void setInfoMsg(String infoMsg) {
		this.infoMsg = infoMsg;

	}

	public void setSvcGroup(String svcGroup) {
		this.svcGroup = svcGroup;
	}

	public void setSvcName(String svcName) {
		this.svcName = svcName;
	}

	public void setAppID(String appID) {
		this.appID = appID;
	}
	
	public String getRequestID() {
		return requestID;
	}

	public void setRequestID(String requestID) {
		this.requestID = requestID;
		try {
			this.respObj.put("request_id", requestID);
		} catch (JSONException e) {
		}

	}

	public void setServerTime(String serverTime) {
		try {
			this.respObj.put("serverTime", serverTime);
		} catch (JSONException e) {
		}
	}

	public void clearData() {
		if (this.dataObj.length() > 0)
			this.dataObj = new JSONObject();
	}

	@Override
	public String toString() {
		try {
			this.respObj.put("infoID", infoID);
			this.respObj.put("infoMsg", infoMsg);
			this.respObj.put("svcName", svcName);
			this.respObj.put("svcGroup", svcGroup);
			this.respObj.put("data", dataObj);
		} catch (JSONException e) {
		}

		return super.toString();
	}


}
