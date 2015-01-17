package erandoo.app.database;

import java.io.Serializable;

import android.provider.BaseColumns;
public class TaskDoer implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "task_doer";
	public static final String ID = BaseColumns._ID;
	public static final String FLD_USER_ID = "user_id";
	public static final String FLD_FULL_NAME = "fullname";
	public static final String FLD_USER_IMAGE = "userimage";
	public static final String FLD_WORK_LOCATION = "work_location";
	public static final String FLD_ABOUT_ME = "about_me";
	public static final String FLD_TASK_POST_CNT = "task_post_cnt";
	public static final String FLD_TASK_DONE_CNT = "task_done_cnt";
	public static final String FLD_NETWORK = "network";
	public static final String FLD_LAST_ACCESSED_AT = "last_accessed_at";
	public static final String FLD_DISTANCE = "distance";
	public static final String FLD_HIRED = "hired";
	public static final String FLD_RATING_TASKER = "rating_avg_as_tasker";
	public static final String FLD_RATING_POSTER = "rating_avg_as_poster";
	public static final String FLD_ONLINE_STATUS = "online_status";
	public static final String FLD_IS_VIRTUAL_DOER_LICENSE = "is_virtualdoer_license";
	public static final String FLD_IS_INPERSON_DOER_LICENSE = "is_inpersondoer_license";
	public static final String FLD_IS_INSTANT_DOER_LICENSE = "is_instantdoer_license";
	public static final String FLD_IS_PREMIUM_DOER_LICENSE = "is_premiumdoer_license";
	public static final String FLD_IS_POSTER_LICENSE = "is_poster_license";
	public static final String FLD_MULTI_SKILLS = "multiskills";
	public static final String FLD_SEARCH_KEY = "search_key";
	public static final String FLD_QUICK_FILTER = "quick_filter";
	public static final String FLD_PAGE = "page";
	public static final String FLD_TRNO = "trno";
	public static final String FLD_MOBILE_REC_ID = "mobile_rec_id"; 
	public static final String FLD_LONGITUDE = "longitude";
	public static final String FLD_LATITUDE = "latitude";
	public static final String FLD_PROJECT_TYPE = "task_kind";
	
	public static final String FLD_SELECTION_TYPE = "selection_type";
	public static final String FLD_IS_INVITED = "is_invited";
	
	public static final String FLD_STATUS = "status";
	public static final String FLD_PROJECT_STATUS = "project_status";
	public static final String FLD_TASK_TASKER_ID = "task_tasker_id";
	
	public static final String FLD_CANCEL_REQ_BY = "cancel_req_by";
	public static final String FLD_CANCEL_REQ_DATE = "cancel_req_date";
	public static final String FLD_CANCEL_REASON_BY_POSTER = "cancel_reason_by_poster";
	public static final String FLD_CANCEL_REASON_BY_DOER = "cancel_reason_by_doer";
	public static final String FLD_TASK_COMPLETE_MARKED = "task_complete_marked";
	public static final String FLD_TASK_COMPLETE_MARK_BY = "task_complete_mark_by";
	public static final String FLD_TASK_COMPLETE_CONFIRM_BY = "task_complete_confirm_by";
	public static final String FLD_CANCEL_REFUND_OFFER_BY_DOER = "cancel_refund_offer_by_doer";
	public static final String FLD_CANCEL_REFUND_DEMAND_BY_POSTER = "cancel_refund_demand_by_poster";
	public static final String FLD_SEAL_MY_PROPOSAL = "seal_my_proposal";
	
	public Long user_id;
	public String fullname;
	public String userimage;
	public String work_location;
	public String about_me;
	public String task_post_cnt;
	public String task_done_cnt;
	public String network;
	public String last_accessed_at;
	public String distance;
	public String hired;
	public String rating_avg_as_tasker;
	public String rating_avg_as_poster;
	public String online_status;
	public String is_virtualdoer_license; 
	public String is_inpersondoer_license;
	public String is_instantdoer_license;
	public String is_premiumdoer_license;
	public String is_poster_license;
	public String longitude;
	public String latitude;
	
	public String selection_type;
	public String is_invited;
	public String status;
	public String project_status;
	public Long task_tasker_id;
	public Long cancel_req_by;
	public String cancel_req_date;
	public String cancel_reason_by_poster;
	public String cancel_reason_by_doer;
	public Integer task_complete_marked;
	public Long task_complete_mark_by;
	public Long task_complete_confirm_by;
	public Long cancel_refund_offer_by_doer;
	public Long cancel_refund_demand_by_poster;
	public String seal_my_proposal;
	
	public boolean isSelected = false;
	public String bookmark_user;
	
	public NetworkConnection network_connection = null;
	
	public TaskDoer() {

	}
}