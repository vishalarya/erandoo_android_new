package erandoo.app.database;

import android.provider.BaseColumns;

public class Payment {
	public static final String TABLE_NAME = "payment";

	public static final String FLD_ID = BaseColumns._ID;
	
	public static final String FLD_PAYMENT_ID = "payment_id";
	public static final String FLD_USER_ID = "user_id";
	public static final String FLD_AMOUNT = "amount";
	public static final String FLD_PAYMENT_TYPE = "payment_type";
	public static final String FLD_PAYMENT_DESC = "payment_desc";
	public static final String FLD_PAYMENT_REQUEST_ID = "payment_request_id";
	public static final String FLD_PAYMENT_REF_ID = "payment_refid";
	public static final String FLD_CREATED_AT = "created_at";
	public static final String FLD_CREATED_BY = "created_by";
	public static final String FLD_UPDATED_AT = "updated_at";
	public static final String FLD_UPDATED_BY = "updated_by";
	public static final String FLD_PAYMENT_SERVICE_PROVIDER = "payment_service_provider";
	public static final String FLD_SOURCE_APP = "source_app";
	public static final String FLD_TRNO = "trno";
	
	public Integer _id;
	public Long payment_id;
	public Long user_id;
	public String amount;
	public String payment_type;
	public String payment_desc;
	public Long payment_request_id;
	public String payment_refid;
	public String created_at;
	public String created_by;
	public String updated_at;
	public String updated_by;
	public String payment_service_provider;
	public String source_app;
	public Long trno;
	
	public Payment(){
		
	}
}
