package erandoo.app.database;

import java.io.Serializable;
import java.util.ArrayList;

import erandoo.app.tempclasses.ProfileRatingData;

public class Profile implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String FLD_USER_ID = "user_id";
	public static final String FLD_FULLNAME = "fullname";
	public static final String FLD_PROFILE_FOLDER_NAME = "profile_folder_name";
	public static final String FLD_USER_IMAGE = "userimage";
	public static final String FLD_ABOUT_ME = "about_me";
	public static final String FLD_IS_INVITED_BY_ME = "is_invited_by_me";
	public static final String FLD_TASK_POST_CNT = "task_post_cnt";
	public static final String FLD_TASK_DONE_CNT = "task_done_cnt";
	public static final String FLD_NETWORK = "network";
	public static final String FLD_NETWORK_COUNT = "network_count";
	public static final String FLD_LAST_ACCESSED_AT = "last_accessed_at";
	public static final String FLD_LATITUDE = "latitude";
	public static final String FLD_LONGITUDE = "longitude";
	public static final String FLD_DISTANCE = "distance";
	public static final String FLD_HIRED_BY_ME_COUNT = "hired_by_me_count";
	public static final String FLD_RATING_AVG_AS_TASKER = "rating_avg_as_tasker";
	public static final String FLD_RATING_AVG_AS_POSTER = "rating_avg_as_poster";
	public static final String FLD_ONLINE_STATUS = "online_status";
	public static final String FLD_IS_VIRTUALDOER_LICENSE = "is_virtualdoer_license";
	public static final String FLD_IS_INPERSONDOER_LICENSE = "is_inpersondoer_license";
	public static final String FLD_IS_INSTANTDOER_LICENSE = "is_instantdoer_license";
	public static final String FLD_IS_PREMIUMDOER_LICENSE = "is_premiumdoer_license";
	public static final String FLD_IS_POSTER_LICENSE = "is_poster_license";
	public static final String FLD_GENDER = "gender";
	public static final String FLD_TRNO = "trno";
	public static final String FLD_WORK_LOCATION_NAME = "work_location_name";
	
	public static final String ALL_CATEGORY = "All Category";
	public static final String RECOMMANDATION_FLAG = "More";
	public static final String RECOMMANDATION_REQUEST_FLAG = "Request";
	
	public static final String FLD_VIEW_USER_ID = "view_user_id";
	public static final String FLD_CATEGORY_ID = "category_id";
	public static final String FLD_RATING_OF_USER_ID = "rating_of_user_id";
	public static final String FLD_RECOMMENDATION = "recommendation";
	public static final String FLD_RECOMMENDATION_VIEW_USER_ID = "recommendation_view_of_user_id";
	public static final String FLD_REQUEST_TO_USER = "request_to_user";
	
	public static final String OPERATION = "operation";
	public static final String OPERATION_LIST_CATEGORY = "list_category_rating";
	public static final String OPERATION_REQUEST_RECOMMENDATION = "request_recommendation";
	
	public static final String FLD_FIRSTNAME = "firstname";
	public static final String FLD_LASTNAME = "lastname";
	
	
	public String user_id;
	public String fullname;
	public String profile_folder_name;
	public String userimage;
	public String about_me;
	public String is_invited_by_me;
	public String task_post_cnt;
	public String task_done_cnt;
	public String network;
	public String network_count;
	public String last_accessed_at;
	public String latitude;
	public String longitude;
	public String distance;
	public String hired_by_me_count;
	public String rating_avg_as_tasker;
	public String rating_avg_as_poster;
	public String online_status;
	public String is_virtualdoer_license;
	public String is_inpersondoer_license;
	public String is_instantdoer_license;
	public String is_premiumdoer_license; 
	public String is_poster_license;
	public String gender;
	public String trno;
	public String primary_phone;
	public String alternate_phone;
	public String work_location_name;
	public String profile_completion;
	
	public String firstname;
	public String lastname;

	public ProfileRatingData rating = null;
	public Portfolio portfolio = null;

	public ArrayList<WorkHistory> work_history;
	public ArrayList<WorkLocation> work_location;
	public ArrayList<Skill> multiskills;
	public ArrayList<TaskDoer> network_members;
	public ArrayList<Recommandation> recommendation;

	public Profile() {

	}
}
