package erandoo.app.network;

import org.json.JSONObject;
import android.text.TextUtils;
import erandoo.app.utilities.Constants;

/**
 * The NetworkResponse class has network response code information.
 * 
 * @author Arya Dev
 * 
 */
public class NetworkResponse {
    
	public static boolean isDeviceOnline = false;
	public int netRespCode = -1;
	public String respStr = null;
	private JSONObject jsonObject = null;
	public boolean isDataUpdated = false;
    
	public JSONObject getJsonObject() {
		if (jsonObject == null) {
			toJson();
		}
		return jsonObject;
	}

	private void toJson() {
		try {
			if(respStr != null)
				jsonObject = new JSONObject(respStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public NetworkResponse() {
		netRespCode = -1;
		respStr = null;
	}

	public boolean isSuccess() {
		if (TextUtils.isEmpty(respStr)) {
			return false;
		}
		try {
			String status = (String) getJsonObject().get(Constants.FLD_STATUS);
			return status.equals(Constants.VAL_OK);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public String getErrCode() {
		try {
			return ((String) getJsonObject().get(Constants.FLD_ERR_CODE));
		} catch (Exception e) {
			e.printStackTrace();
			return Constants.VAL_UNKNOWN;
		}
		
	}

 /*	public String getErrMsg() {
		try {
			return ((String) getJsonObject().get(Constants.FLD_ERR_MSG));
		} catch (Exception e) {
			e.printStackTrace();
			return Constants.VAL_UNKNOWN;
		}
	}*/
	
 // I have used this function for this version When we will implement 
 // Language support we need use string key's from
 // android string file regarding error code .
	
	public String getErrMsg() {
		try {
			return ((String) getJsonObject().get(Constants.FLD_MSG));
		} catch (Exception e) {
			e.printStackTrace();
			return Constants.VAL_UNKNOWN;
		}
	}
}
