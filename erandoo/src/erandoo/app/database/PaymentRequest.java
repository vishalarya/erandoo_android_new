package erandoo.app.database;

import android.provider.BaseColumns;

public class PaymentRequest {
	public static final String TABLE_NAME = "payment_request";

	public static final String FLD_ID = BaseColumns._ID;
	
	public static final String FLD_PAYMENT_REQUEST_ID = "payment_request_id";
	public static final String FLD_PAYMENT_TYPE = "payment_type";
	public static final String FLD_PAYMENT_SUB_TYPE = "payment_subtype";
	public static final String FLD_INVOICE_ID = "invoice_id";
	public static final String FLD_NET_BILL_AMT = "net_bill_amt";
	public static final String FLD_BONUS_AMT = "bonus_amt";
	public static final String FLD_PENALITY_AMT = "penality_amt";
	public static final String FLD_FEE_TYPE = "fee_type";
	public static final String FLD_FEE_PERCENT = "fee_percent";
	public static final String FLD_FEE_AMT = "fee_amt";
	public static final String FLD_RELEASED_AMT = "released_amt";
	public static final String FLD_RELEASED_AMT_DETAIL = "released_amt_detail";
	public static final String FLD_PAYMENT_CURRENCY = "payment_currency";
	public static final String FLD_BY_USER_ID = "by_user_id";
	public static final String FLD_BY_TEAM_ID = "by_team_id";
	public static final String FLD_BY_USER_ID_TYPE = "by_user_id_type";
	public static final String FLD_TO_USER_ID = "to_user_id";
	public static final String FLD_TO_TEAM_ID = "to_team_id";
    public static final String FLD_TO_USER_ID_TYPE = "to_user_id_type";
    public static final String FLD_PAYMENT_GATEWAY_RC = "payment_gateway_rc";
    public static final String FLD_PAYMENT_GATEWAY_MSG = "payment_gateway_msg";
    public static final String FLD_PAYMENT_GATEWAY_REF_ID = "payment_gateway_refid";
	public static final String FLD_CREATED_AT = "created_at";
	public static final String FLD_CREATED_BY = "created_by";
	public static final String FLD_UPDATED_AT = "updated_at";
	public static final String FLD_UPDATED_BY = "updated_by";
	public static final String FLD_SOURCE_APP = "source_app";
	public static final String FLD_STATUS = "status";
	public static final String FLD_TRNO = "trno";

	public Integer _id;
	public Long payment_request_id;
	public String payment_type;
	public String payment_subtype;
	public Long invoice_id;
	public String net_bill_amt;
	public String bonus_amt;
	public String penality_amt;
	public String fee_type;
	public String fee_percent;
	public String fee_amt;
	public String released_amt;
	public String released_amt_detail;
	public String payment_currency;
	public Long by_user_id;
	public Long by_team_id;
	public String by_user_id_type;
	public Long to_user_id;
	public Long to_team_id;
	public String to_user_id_type;
	public String payment_gateway_rc;
	public String payment_gateway_msg;
	public String payment_gateway_refid;
	public String created_at;
	public String created_by;
	public String updated_at;
	public String updated_by;
	public String source_app;
	public String status;
	public Long trno;

	public PaymentRequest() {

	}
}
