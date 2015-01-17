package erandoo.app.utilities;



public class Constants {
	public static final String LOGIN_SUCCESS = "0";
	// Command names
	public static final String CMD_SIGNIN = "user_signin";
	public static final String CMD_SIGNUP = "user_signup";
	public static final String CMD_SYNC_MASTER_DATA = "sync_master_data";
	public static final String CMD_GET_COUNTRY = "get_country";
	public static final String CMD_GET_CATEGORY = "get_category";
	public static final String CMD_GET_SKILL = "get_skill";
	public static final String CMD_POST_VIRTUAL_PROJECT = "post_virtual_project";
	public static final String CMD_EDIT_VIRTUAL_PROJECT = "edit_virtual_project";
	public static final String CMD_POST_IN_PERSON_PROJECT = "post_inperson_project";
	public static final String CMD_POST_INSTANT_PROJECT = "post_instant_project";
	public static final String CMD_GET_PROJECT_LIST = "search_projects";
	public static final String CMD_GET_CATEGORY_SKILL_QUESTION = "get_category_skill_question";
	public static final String CMD_SEARCH_DOERS = "search_doers";
	public static final String CMD_GET_MESSAGE_LIST = "get_inbox_messages";
	public static final String CMD_GET_SEND_MESSAGE = "send_message";
	public static final String CMD_GET_MESSAGE_OPERATIONS = "manage_message";
	public static final String CMD_GET_MY_PROJECT_LIST = "list_my_projects";
	public static final String CMD_GET_NOTIFICATION_LIST = "get_notifications";
	public static final String CMD_GET_PROPOSAL = "get_proposal";
	public static final String CMD_VIEW_PROPOSAL = "view_proposal";
	public static final String CMD_GET_CANCEL_PROJECT_BEFORE_AWARD = "cancel_project";
	public static final String CMD_GET_CANCEL_PROJECT_AFTER_AWARD = "cancel_project_after_award";
	public static final String CMD_GET_CANCEL_PROJECT_AFTER_AWARD_RESPONSE = "cancel_project_after_award_response";
	public static final String CMD_HIRE_DOER = "hire_doer";
	public static final String CMD_INVITE_PROJECT_DOER = "invite_project_doer";
	public static final String CMD_DASHBOARD_SETTING = "dashboard_setting";
	public static final String CMD_GET_PROJECT_MESSAGE = "get_project_message";
	public static final String CMD_GET_RATINGS = "get_ratings";
	public static final String CMD_PROJECT_COMPLETE_DOER = "project_complete_doer";
	public static final String CMD_PROJECT_COMPLETE_POSTER = "project_complete_poster";
	public static final String CMD_FORGOT_PASSWORD = "forgot_password";
	public static final String CMD_SEND_NETWORK_INVITATION = "send_network_invitation";
	public static final String CMD_ACCEPT_NETWORK_INVITATION = "accept_network_invitation";
	
	// Registration API's & Constants
	public static final String CMD_REG_VERIFICATION = "verify_user";
	public static final String CMD_RESEND_VERIF_CODE = "resend_verification_code";
	public static final String CMD_CREATE_PROPOSAL = "create_proposal";
	public static final String CMD_EDIT_PROPOSAL = "edit_proposal";
	public static final String CMD_GET_VIEW_USER_PROFILE = "view_user_profile";
	public static final String CMD_GET_PROFILE_MANAGE_RATING = "manage_rating";
	public static final String CMD_GET_PROFILE_MANAGE_RECOMMENDATION = "manage_recommendation";
	public static final String CMD_VALIDATION_ACTION = "validate_action";
	
	//------------------Settings Module-----------------------------------
	public static final String CMD_CHANGE_PASSWORD = "change_password";
	public static final String CMD_MANAGE_USER_LOCATION = "manage_user_location";
	public static final String CMD_MANAGE_SETTING= "manage_setting";
	public static final String CMD_PAYMENT_HISTORY = "payment_history";
	public static final String CMD_REG_UPDATE_PROFILE = "update_user_profile";
	public static final String CMD_UPDATE_USER_PROFILE = "profile_setting";
	//-- book-mark API
	public static final String CMD_SAVE_BOOKMARK= "save_bookmark";
	public static final String CMD_DELETE_BOOKMARK = "delete_bookmark";

	// Field names
	public static final String FLD_CMD = "cmd";
	public static final String FLD_DATA = "data";
	public static final String FLD_STATUS = "status";
	public static final String FLD_LANG_CODE = "language_code";
	public static final String FLD_SOURCE_APP = "source_app";
	public static final String FLD_USER_DEVICE_ID = "user_device_id";
	public static final String FLD_ERR_CODE = "err_code";
	public static final String FLD_ERR_MSG = "err_msg";
	public static final String FLD_MSG = "msg";
	public static final String FLD_TRNO = "trno";
	public static final String FLD_RECORDS_PER_PAGE = "records_per_page";
	public static final String FLD_IS_MORE = "is_more";
	public static final String FLD_OPERATION = "operation";

	public static final String VAL_OK = "ok";
	public static final String VAL_UNKNOWN = "Server not responding";

	// User detail field names
	public static final String FLD_APP_TOKEN = "app_token";
	public static final String FLD_USER_TOKEN = "user_token";
	public static final String FLD_EMAIL_ID = "email";// Email ID
	public static final String FLD_USER_ID = "user_id";
	public static final String FLD_PASSWORD = "password";
	public static final String FLD_LOGIN_WITH = "loginwith";
	public static final String FLD_FIRST_NAME = "firstname";
	public static final String FLD_LAST_NAME = "lastname";
	public static final String FLD_ACCOUNT_TYPE = "account_type";
	public static final String FLD_USER = "user";
	
	//registration field names
	public static String V_CODE = null;
	public static String NEW_USER_ID = null;
	public static String REG_ID = null;
	public static String CONTACT_ID = null;
	public static final String FLD_CONTACT_ID = "contact_id";
	public static final String FLD_REG_ID = "reg_id";
	public static final String FLD_ADDRESS = "address";
	public static final String FLD_COUNTRY = "country";
	public static final String FLD_STATE = "state";
	public static final String FLD_REGION = "region";
	public static final String FLD_CITY = "city";
	public static final String FLD_ZIPCODE = "zipcode";
	public static final String FLD_CLOUD_KEY = "cloud_key";
	public static final String FLD_REG_VERIF_CODE = "verification_code";
	
	//------------------------------------------------------------------------------
	
	// Project detail field name
	public static final String MESSAGE_KEY = "message";
	// ---------------------------------------------------------

	public static final String PARENT_VIEW = "parent";
	public static final String VIEW_MAIN = "main";
	public static final String SERIALIZABLE_DATA = "Serializable";
	public static final String VIEW_NOTIFICATION = "notification";
	public static final String VIEW_PROPOSAL_DETAIL = "proposal_detail";
	public static final String VIEW_PROJECT_DETAIL = "project_detail";
	public static final String VIEW_MESSAGE = "message";
	public static final String VIEW_VP_CREATION = "vp_creation";
	public static final String VIEW_IP_CREATION = "ip_creation";
	public static final String VIEW_I_CREATION = "i_creation";
	public static final String VIEW_SEARCH_PROJECTS = "search_projects";
	public static final String VIEW_SEARCH_MEMBERS = "search_doers";
	public static final String VIEW_MY_PROJECTS = "my_projects";
	public static final String VIEW_MY_PROFILE = "my_profile";
	public static final String VIEW_SETTINGS = "settings";
	
	public static final String VIEW_MODE = "viewMode";
	public static final String VIEW_MODE_EDIT = "edit";
	public static final String VIEW_MODE_CREATE = "create";
	
	public static final Long INVALID_ID = Long.valueOf(-1);

	// -----------------------TRNO-----------------------

	public static final String TRNO_CATEGORY = "category_trno";
	public static final String TRNO_COUNTRY = "country_trno";
	public static final String TRNO_SKILL = "skill_trno";
	public static final String TRNO_QUESTION = "question_trno";

	// -------------------PROJECT TYPES---------------
	public static final String PROJECT_TYPE = "project_type";
	public static final String VIRTUAL = "v";
	public static final String INPERSON = "p";
	public static final String INSTANT = "i";
	
	public static final String  LICENSE_IS_VIRTUAL_DOER = "is_virtualdoer_license";
	public static final String  LICENSE_IS_INPERSON_DOER = "is_inpersondoer_license";
	public static final String  LICENSE_IS_INSTANT_DOER = "is_instantdoer_license";
	public static final String  LICENSE_IS_PREMIUM_DOER = "is_premiumdoer_license";
	public static final String  LICENSE_IS_POSTER = "is_poster_license";

	public static final String PROJECT_VIRTUAL = "Virtual";
	public static final String PROJECT_INPERSON = "Inperson";
	public static final String PROJECT_INSTANT = "Instant";
	
	public static final String STATUS_VALID = "valid";

	// ---------------PAYMENT MODES-------------------
	public static final String PAYMENT_MODE_HOURLY = "h";
	public static final String PAYMENT_MODE_FIXED = "f";

	public static final String DELIVERY_STATUS_PENDING = "p";
	public static final String DELIVERY_STATUS_SENT = "s";
	public static final String DELIVERY_STATUS_ERROR = "e";
	public static final String DELIVERY_STATUS_ALL = "a";

	public static final String LOCATION_REGION_COUNTRY = "c";
	public static final String LOCATION_REGION_SAVED = "s";
	public static final String LOCATION_REGION_OTHER = "o";

	public static final String DOER_SELECTION_TYPE_AUTO = "auto";
	public static final String DOER_SELECTION_TYPE_MANUAL = "inv";

	public static final String MY_PROJECT_AS_POSTER = "poster";
	public static final String MY_PROJECT_AS_DOER = "doer";

	public static final String MESSAGE_OPERATION_DELETE = "delete";
	public static final String MESSAGE_OPERATION_MARK_READ = "mark_read_message";
	public static final String MESSAGE_OPERATION_MARK_UNREAD = "mark_unread_message";
	public static final String MESSAGE_OPERATION_MARK_PUBLIC= "mark_public_message";

	// temporary code
	public static final String msg = "Under construction";

	// MQTT Server details
	public static final String MQTT_BROKERNAME = "chat.erandoo.com";//"54.84.193.236";// 107.182.163.219";//54.84.193.236//122.176.45.15//local
																	// 192.168.1.114
	public static final int MQTT_PORT = 1883;
	public static final String MQTT_USER = "admin";
	public static final String MQTT_PASSWORD = "admin";
	public static final short MQTT_CONNECTION_KEEP_ALIVE_IN_SECS = 20 * 60;
	// public static final String BROADCAST_TYPE = "type";
	public static final String MQTT_EVENT_TYPE_CHAT = "chat";
	public static final String MQTT_EVENT_TYPE_PUSH = "push";
	public static final String MQTT_EVENT_TYPE_INVITATION = "inv";

	public static final String LOCAL_BROADCAST_FOR_MAIN = "main_activity";
	public static final String LOCAL_BROADCAST_FOR_CHAT = "chat_activity";
	public static final String LOCAL_BROADCAST_FOR_IRESULT = "iresult_activity";

	public static final Boolean VAL_TRUE = Boolean.valueOf(true);
	public static final Boolean VAL_FALSE = Boolean.valueOf(false);
	
	//------------------ Settings Module Fields -----------------------------------
	// ChangePassword Constants
	public static final String PWD = "pwd";
	public static final String NEW_PWD = "password_new";
	// Get State, City, Region Constants
	public static final String GET_STATE = "get_state";
	public static final String GET_CITY = "get_city";
	public static final String GET_REGION = "get_region";
	public static final String COUNTRY_CODE = "country_code";
	public static final String STATE_ID = "state_id";
	public static final String REGION_ID = "region_id";
	public static final String setState = "Select State";
	public static final String setRegion = "Select Region";
	public static final String setCity = "Select City";
	public static final String setContry = "Select Country";
	// Add new Location (ANL) Constants
	public static final String FLD_SETTING_OPERATION = "operation";
	
	// Money

	public static final String FLD_SETTING_PAGE = "page";
	public static final String FLD_SETTING_START_DATE = "start_date";
	public static final String FLD_SETTING_END_DATE = "end_date";
	public static final String FLD_CONF_PASS = "confirm_pass";
	
	// fields name for update user profile
	
	public static final String FLD_UPDATE_PROFILE_IMAGE="profile_image";
	public static final String FLD_UPDATE_PROFILE_MAIN_PHONE="main_phone";
	public static final String FLD_UPDATE_PROFILE_PROFILE_IMAGE="profile_image";
	public static final String FLD_UPDATE_PROFILE_ALTERNATE_PHONE="alternate_phone";
	public static final String FLD_UPDATE_PROFILE_ABOUTME="about_me";
	public static final String FLD_UPDATE_PROFILE_SKILLS="skills";
		
	public static final int PICK_PROFILE_IMAGE = 1;	
	public static final int CROP_PROFILE_IMAGE = 2;	
	
	public static final String FLD_FIRSTNAME = "firstname";
	public static final String FLD_LASTNAME = "lastname";
}
