package com.kotak.mb2.admin.administration.domain.request;

import com.kotak.mb2.admin.administration.constants.AppConstants;
import com.kotak.mb2.admin.administration.util.EncryptDecrypt;
import com.kotak.mb2.admin.administration.util.RSAEncryptionDecryption;
import org.json.JSONObject;

public class CommonAPIKMBRequest extends CommonAPIMSFRequest {
//	private static Logger log = Logger.getLogger(CommonAPIKMBRequest.class);

	private boolean preLogin = true;
	private String CRN = null;

	private boolean invalidReqData = false;

	public boolean isInvalidReqData() {
		return invalidReqData;
	}

	public void setInvalidReqData(boolean invalidReqData) {
		this.invalidReqData = invalidReqData;
	}

	public String getCRN() {
		return CRN;
	}

	public void setCRN(String cRN) {
		CRN = cRN;
	}

	public CommonAPIKMBRequest(String request) throws Exception {
		super(request);
		System.out.println("test");
		if (getRequest().has("isRSA") && getRequest().getString("isRSA").equals("false")) {
			// Check whether both data & encrypted data present in request
			if (getRequest().has("encryptedData") && getRequest().has("data")) {
				JSONObject data = getRequest().getJSONObject("data");
				if (data.length() > 0) {
					this.invalidReqData = true;
				}
			}

			if (getRequest().has("encryptedData")) {
				String reqData = getRequest().getString("encryptedData");
				reqData = EncryptDecrypt.decryptText(AppConstants.COMM0N_API_AES_ENC_KEY, reqData);
				getRequest().put("data", new JSONObject(reqData));
				maskValueInData("test");// to initilize Logging Object
				getObjForLogging().getJSONObject("request").put("data", new JSONObject(reqData));
			}
			return;
		}

		// Check whether both data & encrypted data present in external request &&
		// request encrypted by RSA
		if (getRequest().has("channel_id")) {
			// if (getRequest().has("encryptedData") && getRequest().has("data")) {
			JSONObject data = getRequest().getJSONObject("data");
			if (data.length() > 0) {
				this.invalidReqData = true;
			}
//			}
			if (getRequest().has("encryptedData")) {
				String reqData = getRequest().getString("encryptedData");
				reqData = RSAEncryptionDecryption.decrypt(reqData, RSAEncryptionDecryption.getPrivateKey());
				getRequest().put("data", new JSONObject(reqData));
				maskValueInData("test");// to initilize Logging Object
				getObjForLogging().getJSONObject("request").put("data", new JSONObject(reqData));
			}
		}

	}

	public boolean isPreLogin() {
		return preLogin;
	}

	public void setPreLogin(boolean preLogin) {
		this.preLogin = preLogin;
	}

	public String getLoginType() {
		return getRequest().optString("loginType", "");
	}
}
