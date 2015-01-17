package erandoo.app.database;

import java.io.Serializable;
import java.util.ArrayList;

import android.provider.BaseColumns;

public class Project implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "task";
	
	public static final String FLD_ID = BaseColumns._ID;
	public static final String FLD_TASK_ID = "task_id";
	public static final String FLD_CATEGORY_ID = "category_id";
	public static final String FLD_LOCATION_REGION = "is_location_region";
	public static final String FLD_PROJECT_TYPE = "task_kind";
	public static final String FLD_TITLE = "title";
	public static final String FLD_DESCRIPTION = "description";
	public static final String FLD_END_DATE = "end_date";
	public static final String FLD_BID_DURATION = "bid_duration";
	public static final String FLD_MIN_PRICE = "min_price";
	public static final String FLD_MAX_PRICE = "max_price";
	public static final String FLD_EXPENSES = "cash_required";
	public static final String FLD_IS_PUBLIC = "is_public";
	public static final String FLD_PAYMENT_MODE = "payment_mode";
	public static final String FLD_PRICE = "price";
	public static final String FLD_WORK_HRS = "work_hrs";
	public static final String FLD_IS_HIGH_LIGHTED = "is_highlighted";
	public static final String FLD_IS_PREMIUM = "is_premium";
	public static final String FLD_INVITED_DOERS = "invitedtaskers";
	public static final String FLD_MULTI_CAT_QUESTIONS = "multicatquestion";
	public static final String FLD_MULTI_LOCATIONS = "multilocations";
	public static final String FLD_MULTI_SKILLS = "multiskills";
	public static final String FLD_TASK_LOCATION = "tasklocation";
	public static final String FLD_DELIVERRY_STATUS = "delivery_status";
	public static final String FLD_TRNO = "trno";
	public static final String FLD_MOBILE_REC_ID = "mobile_rec_id"; 
	public static final String FLD_PROPOSALS_COUNT = "proposals_cnt";
	public static final String FLD_PROPOSALS_AVG_PRICE = "proposals_avg_price";
	public static final String FLD_CREATOR_ROLE = "creator_role";
	public static final String FLD_IS_EXTERNAL = "is_external";
	public static final String FLD_HIRING_CLOSED = "hiring_closed";
	public static final String FLD_STATE = "state";
	public static final String FLD_PRICE_CURRENCY = "price_currency";
	public static final String FLD_TASK_ASSIGNED_ON = "task_assigned_on";
	public static final String FLD_END_TIME = "end_time";
	public static final String FLD_BID_START_DATE = "bid_start_dt";
	public static final String FLD_BID_CLOSE_DATE = "bid_close_dt";
	public static final String FLD_CREATE_AT = "created_at";
	public static final String FLD_CREATE_BY = "created_by";
	public static final String FLD_UPDATE_AT = "updated_at";
	public static final String FLD_UPDATED_BY = "updated_by";
	public static final String FLD_WORK_LOCATION_ID = "work_location_id";
	public static final String FLD_LATITUDE = "latitude";
	public static final String FLD_LONGITUDE = "longitude";
	public static final String FLD_LOCATION_GEO_AREA = "location_geo_area";
	public static final String FLD_SELECTION_TYPE = "selection_type";
	public static final String FLD_TASKER_IN_RANGE = "tasker_in_range";
	public static final String FLD_STATUS = "status";
	
	public static final String FLD_IS_INVITED = "is_invited";
	public static final String FLD_CREATOR_USER_ID = "creator_user_id";
	public static final String FLD_NAME = "name";
	
	public static final String FLD_SEARCH_KEY = "search_key";
    public static final String FLD_PAGE = "page";
    public static final String FLD_SORT = "sort";
    public static final String FLD_TASK_IMAGE = "task_image";
    public static final String FLD_PROJECT_START_DATE = "project_start_date";
    public static final String FLD_SEAL_ALL_PROPOSAL = "seal_all_proposal";
    public static final String FLD_USERIMAGE = "userimage";
    public static final String FLD_AVERAGE_RATING = "average_rating";
    
    public String userimage;
    public Long _Id;
	public Long task_id;
	public Long category_id;
	public String is_location_region;
	public String task_kind;
	public String title;
	public String description;
	public String end_date;
	public String bid_duration;
	public String min_price;
	public String max_price;
	public String cash_required;
	public String is_public;
	public String payment_mode;
	public String price;
	public String work_hrs;
	public String is_highlighted;
	public String is_premium;
	public String delivery_status;
	public Long trno;
	public String proposals_cnt;
	public String proposals_avg_price;
	public String creator_role;
	public String is_external;
	public String hiring_closed;
	public String state;
	public String price_currency;
	public String task_assigned_on;
	public String end_time;
	public String bid_start_dt;
	public String bid_close_dt;
	public String created_at;
	public String created_by;
	public String updated_at;
	public String updated_by;
	public String work_location_id;
	public String latitude;
	public String longitude;
	public String location_geo_area;
	public String selection_type;
	public String tasker_in_range;
	public String project_start_date;
	public String status;
	public String is_invited;
	public Long creator_user_id;
	public String mobile_rec_id; //Use as mobile record token
	public String name;
	public String task_image;
	public String bookmark_project;
	public String read_project;
	public String project_detail_url;
    public String seal_all_proposal;
	public String doer;
	public String poster;
	public String total_project;
	public Long task_tasker_id;
	public String average_rating;
	
	public ArrayList<WorkLocation> tasklocation; 
	public ArrayList<TaskDoer> invitedtaskers;
	public ArrayList<Question> multicatquestion;
	public ArrayList<Country> multilocations;
	public ArrayList<Skill> multiskills;
	public ArrayList<Attachment> attachments;
	
	public Category category = null;
	public WorkLocation workLocation = null;
	
	public String projectSkillNames;
	public String projectLocationNames;
	
	public boolean isSelected;
	public Long tasker_id;
	
	public Project(){
		category = new Category();
	}
}
