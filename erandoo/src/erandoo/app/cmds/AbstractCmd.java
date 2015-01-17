package erandoo.app.cmds;


import java.util.LinkedHashMap;

import org.json.JSONObject;
import erandoo.app.config.Config;
import erandoo.app.main.AppGlobals;
import erandoo.app.utilities.Constants;

public abstract class AbstractCmd implements Cmd {

	protected LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();

	public LinkedHashMap<String, Object> getData() {
		return data;
	}

	public void setData(LinkedHashMap<String, Object> data) {
		this.data = data;
	}

	public AbstractCmd() {
		this.data = new LinkedHashMap<String, Object>();
		this.data.put(Constants.FLD_CMD, getCmd());
		this.data.put(Constants.FLD_USER_TOKEN,  AppGlobals.userDetail.user_token);
		this.data.put(Constants.FLD_USER_ID,  AppGlobals.userDetail.user_id);
		this.data.put(Constants.FLD_LANG_CODE,  AppGlobals.userDetail.lang_code);
		this.data.put(Constants.FLD_SOURCE_APP, Config.SOURCE_APP);
		this.data.put(Constants.FLD_USER_DEVICE_ID,  AppGlobals.userDetail.user_device_id);
	}

	abstract String getCmd();

	public void addData(String key, Object value) {
		data.put(key, value);
	}

	@Override
	public String getJsonData() {
		return (getJsonData(data));
	}

	public static String getJsonData(LinkedHashMap<String, Object> data) {
		JSONObject jsonObject = new JSONObject(data);
		return jsonObject.toString();
	}
}
