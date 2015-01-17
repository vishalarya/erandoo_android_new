package erandoo.app.database;

import java.io.Serializable;

import android.provider.BaseColumns;

public class MessageDetail implements Serializable {
	public static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "inbox_user";
	public static final String ID = BaseColumns._ID;
	public static final String FLD_MESSAGE_ID = "msg_id";
	public static final String FLD_MESSAGE_TYPE = "msg_type";
	public static final String FLD_MESSAGE_FLOW = "msg_flow";
	public static final String FLD_THREAD_KEY = "thread_key";
	public static final String FLD_TASK_ID = "task_id";
	public static final String FLD_FROM_USER_ID = "from_user_id";
	public static final String FLD_MESSAGE_FROM_NAME = "msg_from_name";
	public static final String FLD_MESSAGE_TO_NAME = "msg_to_name";
	public static final String FLD_TASK_TITLE = "title";
	public static final String FLD_TO_USER_ID = "to_user_ids";
	public static final String FLD_SUBJECT = "subject";
	public static final String FLD_BODY = "body";
	public static final String FLD_ATTACHMENT = "attachments";
	public static final String FLD_IS_PUBLIC = "is_public";
	public static final String FLD_IS_READ = "is_read";
	public static final String FLD_IS_DELETE = "is_delete";
	public static final String FLD_CREATED_AT = "created_at";
	public static final String FLD_CREATED_BY = "created_by";
	public static final String FLD_UPDATED_AT = "updated_at";
	public static final String FLD_UPDATED_BY = "updated_by";
	public static final String FLD_SOURCE_APP = "source_app";
	public static final String FLD_STATUS = "status";
	public static final String FLD_TRNO = "trno";
	public static final String FLD_MOBILE_REC_ID = "mobile_rec_id";
	public static final String FLD_DELIVERRY_STATUS = "delivery_status";
	public static final String FLD_OPERATION = "operation";
	public static final String FLD_USER_NAME = "user_name";

	public Long recId;
	public Long msg_id;
	public String msg_type;
	public String msg_flow;
	public String thread_key;
	public Long task_id;
	public Long from_user_id;
	public String msg_from_name;
	public String title;
	public Long to_user_ids;
	public String subject;
	public String body;
	public String attachments;
	public String is_public;
	public String is_read;
	public String is_delete;
	public String created_at;
	public String created_by;
	public String updated_at;
	public String updated_by;
	public String source_app;
	public String status;
	public Long trno;
	public String mobile_rec_id;
	public String msg_to_name;
	public String delivery_status;
	public String user_name;
	public String user_device_id;
	
	public boolean isSelected = false;
	public boolean isEditable = false;
	public String operationType;
	public String msgAs = null; // poster or doer

	public MessageDetail() {

	}
}
