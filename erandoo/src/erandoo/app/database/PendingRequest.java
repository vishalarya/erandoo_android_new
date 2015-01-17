package erandoo.app.database;

import android.provider.BaseColumns;
/*
 * All pending request are stired in this table.
 * 
 * Once network is available then these request are sent to server.
 */
public class PendingRequest {
	public static final String TABLE_NAME = "pending_request";

	// Database table field names
	public static final String ID = BaseColumns._ID;
	//JSON String that needs to be sent to the server
	public static final String REQUEST = "request";
	public static final String CREATED_AT = "created_at";
	public static final String REF_TABLE_NAME = "ref_table_name";
	public static final String REF_TABLE_ID = "ref_table_id";
	public static final String DELIVERY_ATTEMPT_CNT = "deliver_attempt_cnt";
	public static final String DELIVERY_ERROR = "deliver_err";

}
