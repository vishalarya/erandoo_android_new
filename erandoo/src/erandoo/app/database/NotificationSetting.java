package erandoo.app.database;

import java.util.ArrayList;

public class NotificationSetting {

	public static final String FLD_NOTIFICATION_SETTING = "notification_setting";
	public static final String FLD_CATEGORY_NOTIFICATION_SETTING = "category_notification_setting";
	public static final String FLD_SKILL_NOTIFICATION_SETTING = "skill_notification_setting";

	public static final String FLD_NOTIFICATION_ID = "notification_id";
	public static final String FLD_SEND_EMAIL = "send_email";
	public static final String FLD_SEND_SMS = "send_sms";

	public String notification_id;
	public String send_email;
	public String send_sms;
	public String applicable_for;
	// public String applicable_for_user_type;
	// public String email_default;
	// public String sms_default;
	public String description;
	// public String status;
	public ArrayList<Category> category;
	public ArrayList<Skill> skill;

	public NotificationSetting() {

	}

}
