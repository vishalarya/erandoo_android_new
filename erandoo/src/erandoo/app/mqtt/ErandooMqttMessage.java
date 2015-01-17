package erandoo.app.mqtt;

import java.io.Serializable;
import java.util.ArrayList;

import erandoo.app.main.AppGlobals;

public class ErandooMqttMessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String FLD_EVENT_TYPE = "event_type";
	public static final String FLD_TASK_ID = "task_id";
	public static final String FLD_USER_ID = "user_id";
	public static final String FLD_FROM_ID = "from_id";
	public static final String FLD_FROM_DISPLAY_ID = "from_display_id";
	public static final String FLD_TO_ID = "to_id";
	public static final String FLD_TO_DISPLAY_ID = "to_display_id";
	public static final String FLD_DATE = "date";
	public static final String FLD_SENT_FROM_CLIENT_ID = "sent_from_client_id";
	public static final String FLD_MSG = "msg";
	public static final String FLD_IS_SENDER_MSG = "is_sender_msg";
	public static final String FLD_CMD = "cmd";
	public static final String FLD_USER_IMAGE = "user_image";
    public static final String FLD_TIME = "time";
    public static final String FLD_DOER_IDS = "doer_ids";
    public static final String FLD_TIMESTAMP = "timestamp";
    
	
	public Long task_id;
	public Long user_id;	
	public Long from_id;
	public String from_display_id;
	public Long to_id;
	public String to_display_id;
	public String date;
	public String sent_from_client_id;
	public String msg;
	public String event_type;
	public Boolean is_sender_msg;
	public String user_image;
	public String time;
	public String doer_ids;
	public String timestamp;
	public String cmd;
	public String mobile_rec_id;
	public String pic;
	public String nm;
	
	public ArrayList<DoerInfo> doers_info;

	public ErandooMqttMessage(){
		
	}
	
	public Long getFriendUserId(){
		Long userId ;
		if(AppGlobals.userDetail.user_id.equals(from_id)){
			userId = to_id;
		}else{
			userId = from_id;
		}
		return userId;
	}
}
