package com.kotak.mb2.admin.administration.domain.response;

import com.kotak.mb2.admin.administration.constants.AppConstants;
import com.kotak.mb2.admin.administration.domain.request.CommonAPIKMBRequest;
import com.kotak.mb2.admin.administration.util.EncryptDecrypt;
import org.json.JSONException;
import org.json.JSONObject;

public class CommonAPIKMBResponse extends CommonAPIMSFResponse {

	public CommonAPIKMBResponse() throws JSONException {
		super();
	}

	public CommonAPIKMBResponse(CommonAPIKMBRequest kmbRequest) throws JSONException {
		super(kmbRequest);
		encrypt = false;
	}

	public boolean infoIDEquals(String infoID) {
		return getInfoID().equals(infoID);
	}

	public Object getFromData(String key) throws JSONException {
		return this.dataObj.get(key);
	}

	public JSONObject getDataObj() {
		return this.dataObj;
	}

	public JSONObject getResponseObj() {
		try {
			return this.getJSONObject("response");
		} catch (Exception e) {
			return (new JSONObject());// This Scnerio will not come
		}

	}

	public String toEncryptedString() {
		try {

			String dataString = dataObj.toString();
			String encryptedString = EncryptDecrypt.encrypt(AppConstants.COMM0N_API_AES_ENC_KEY, dataString);
			clearData();
			this.dataObj.put("encryptedData", encryptedString);
			this.respObj.put("data", encryptedString);

		} catch (JSONException e) {
		}

		return super.toString();
	}

	public void enableEncryption() {
		encrypt = true;
	}

	public boolean isEncrypt() {
		return encrypt;
	}

	boolean encrypt;

	// public void setStatus(boolean status) {
	// try {
	// this.respObj.put("status", status);
	// } catch (JSONException e) {
	// }
	// }

}
