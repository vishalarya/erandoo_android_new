package erandoo.app.database;

public class Notification {
	public static final String TABLE_NAME = "notification";

	public static final String FLD_ALERT_ID = "alert_id";
	public static final String FLD_ALERT_TYPE = "alert_type";
	public static final String FLD_ALERT_DESCRIPTION = "alert_desc";
	public static final String FLD_RECEVICE_ALERT_TYPE = "received_alert_type";
	public static final String FLD_BY_USER_ID = "by_user_id";
	public static final String FLD_FULLNAME = "fullname";
	public static final String FLD_TASK_TASKER_ID = "task_tasker_id";
	public static final String FLD_TASK_ID = "task_id";
	public static final String FLD_TITLE = "title";
	public static final String FLD_IS_SEEN = "is_seen";
	public static final String FLD_SEEN_AT = "seen_at";
	public static final String FLD_CREATED_AT = "created_at";
	public static final String FLD_STATUS = "status";
	
	public String alert_id;
	public String alert_type;
	public String alert_desc;
	public String received_alert_type;
	public String by_user_id;
	public String fullname;
	public String task_tasker_id;
	public String task_id;
	public String title;
	public String is_seen;
	public String seen_at;
	public String created_at;
	public String status;
	
	public Notification() {
		
	}
}
