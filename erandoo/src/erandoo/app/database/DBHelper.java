package erandoo.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public static final int DB_VERSION = 1;
	public Context ctx = null;

	public DBHelper(Context context) {
		super(context, DatabaseMgr.DB_NAME, null, DB_VERSION);
		this.ctx = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createSkillTable(db);
		createCategoryTable(db);
		createCategorySkillTable(db);
		createCategoryQuestionTable(db);
		createCountryTable(db);
		createTaskTable(db);
		createTaskSkillTable(db);
		createTaskQuestionTable(db);
		createTaskLocationTable(db);
		createTaskDoerTable(db);
		createWorkLocationTable(db);
		createInboxUserTable(db);
		createPaymentRequestTable(db);
		createPaymentTable(db);
		createTaskAttachmentTable(db);
	}
	
	private void createSkillTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "+ TableType.SkillTable.getTableName() + "("
				+ Skill.FLD_SKILL_ID + " INTEGER PRIMARY KEY  NOT NULL ON CONFLICT REPLACE,"
				+ Skill.FLD_SKILL_DESC + " VARCHAR ,"
				+ Skill.FLD_SKILL_STATUS + " INTEGER);");
	}
	
	private void createCategoryTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "+ TableType.CategoryTable.getTableName() + "("
				+ Category.FLD_CATEGORY_ID + " LONG PRIMARY KEY  NOT NULL ON CONFLICT REPLACE,"
				+ Category.FLD_CATEGORY_NAME + " VARCHAR,"
				+ Category.FLD_CATEGORY_IMAGE + " VARCHAR,"
				+ Category.FLD_TASK_TEMPLATE + " VARCHAR,"
				+ Category.FLD_PARENT_ID + " LONG,"
				+ Category.FLD_CATEGORY_STATUS + " INTEGER,"
				+ Category.FLD_IS_VIRTUAL + " BOOL,"
				+ Category.FLD_IS_INPERSON + " BOOL,"
				+ Category.FLD_IS_INSTANT + " BOOL,"
				+ Category.FLD_SUB_CATEGORY_CNT + " INTEGER);");
	}
	
	private void createCategorySkillTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "+ TableType.CategorySkillTable.getTableName() + "("
				+ CategorySkill.FLD_SKILL_ID + " INTEGER PRIMARY KEY  NOT NULL ON CONFLICT REPLACE,"
				+ CategorySkill.FLD_TRNO + " LONG,"
				+ Category.FLD_CATEGORY_ID + " INTEGER);");
	}
	
	private void createCategoryQuestionTable(SQLiteDatabase db){
		db.execSQL("CREATE TABLE IF NOT EXISTS "+ TableType.CategoryQuestionTable.getTableName() + "("
				+ Question.FLD_CATEGORY_ID + " INTEGER NOT NULL,"
				+ Question.FLD_QUESTION_ID + " INTEGER NOT NULL,"
				+ Question.FLD_QUESTION_DESC + " VARCHAR,"
				+ Question.FLD_TRNO + " LONG,"
				+ "PRIMARY KEY ("+Question.FLD_CATEGORY_ID+", "+Question.FLD_QUESTION_ID+") ON CONFLICT REPLACE);");
	}
	
	private void createCountryTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "+ TableType.CountryTable.getTableName() + "("
				+ Country.FLD_COUNTRY_CODE + " VARCHAR PRIMARY KEY  NOT NULL ON CONFLICT REPLACE,"
				+ Country.FLD_COUNTRY_NAME + " VARCHAR,"
				+ Country.FLD_COUNTRY_STATUS + " INTEGER);");
	}
	
	private void createTaskTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "+ TableType.TaskTable.getTableName() + "("
				+ Project.FLD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ON CONFLICT REPLACE,"
				+ Project.FLD_TASK_ID + " LONG,"
				+ Project.FLD_CATEGORY_ID + " LONG ,"
				+ Project.FLD_LOCATION_REGION + " CHAR,"
				+ Project.FLD_PROJECT_TYPE + " VARCHAR,"
				+ Project.FLD_TITLE + " VARCHAR,"
				+ Project.FLD_DESCRIPTION + " VARCHAR,"
				+ Project.FLD_END_DATE + " DATETIME,"
				+ Project.FLD_BID_DURATION + " VARCHAR,"
				+ Project.FLD_MIN_PRICE + " VARCHAR,"
				+ Project.FLD_MAX_PRICE + " VARCHAR,"
				+ Project.FLD_EXPENSES + " VARCHAR,"
				+ Project.FLD_PRICE + " VARCHAR,"
				+ Project.FLD_IS_PUBLIC + " INTEGER,"
				+ Project.FLD_PAYMENT_MODE + " CHAR,"
				+ Project.FLD_WORK_HRS + " INTEGER,"
				+ Project.FLD_SEAL_ALL_PROPOSAL + " INTEGER,"
				+ Project.FLD_IS_HIGH_LIGHTED + " INTEGER,"
				+ Project.FLD_DELIVERRY_STATUS + " CHAR,"
				+ Project.FLD_TRNO + " LONG,"
				+ Project.FLD_PROPOSALS_COUNT + " VARCHAR,"
				+ Project.FLD_PROPOSALS_AVG_PRICE +" VARCHAR,"
				+ Project.FLD_CREATOR_ROLE + " CHAR,"
				+ Project.FLD_IS_EXTERNAL + " INTEGER,"
				+ Project.FLD_HIRING_CLOSED + " INTEGER,"
				+ Project.FLD_STATE + " CHAR,"
				+ Project.FLD_END_TIME + " VARCHAR,"
				+ Project.FLD_PRICE_CURRENCY + " VARCHAR,"
				+ Project.FLD_TASK_ASSIGNED_ON + " DATETIME,"
				+ Project.FLD_BID_START_DATE + " DATETIME,"
				+ Project.FLD_BID_CLOSE_DATE + " DATETIME,"
				+ Project.FLD_CREATE_AT + " DATETIME,"
				+ Project.FLD_CREATE_BY + " LONG,"
				+ Project.FLD_SELECTION_TYPE + " VARCHAR,"
				+ Project.FLD_AVERAGE_RATING + " VARCHAR,"
				+ Project.FLD_TASKER_IN_RANGE + " INTEGER,"
				+ Project.FLD_UPDATE_AT + " DATETIME,"
				+ Project.FLD_UPDATED_BY + " LONG," 
				+ Project.FLD_IS_INVITED + " INTEGER,"
				+ Project.FLD_CREATOR_USER_ID + " LONG,"
				+ Project.FLD_PROJECT_START_DATE + " DATETIME,"
				+ Project.FLD_NAME + " VARCHAR," 
				+ Project.FLD_USERIMAGE + " VARCHAR,"
				+ Project.FLD_STATUS + " CHAR,"
				+ Project.FLD_IS_PREMIUM + " INTEGER);");
	}
	
	private void createTaskSkillTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "+ TableType.TaskSkillTable.getTableName() + "("
				+ TaskSkill.ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ON CONFLICT REPLACE,"
				+ TaskSkill.FLD_SKILL_ID + " INTEGER,"
				+ TaskSkill.FLD_SKILL_DESC + " VARCHAR,"
				+ TaskSkill.FLD_MOBILE_REC_ID + " INTEGER);");
	}
	
	private void createTaskQuestionTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "+ TableType.TaskQuestionTable.getTableName() + "("
				+ TaskQuestion.ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ON CONFLICT REPLACE,"
				+ TaskQuestion.FLD_QUESTION_ID + " LONG,"
				+ TaskQuestion.FLD_QUESTION_DESC + " VARCHAR,"
				+ TaskQuestion.FLD_TASK_QUESTION_ID + " LONG,"
				+ TaskQuestion.FLD_MOBILE_REC_ID + " INTEGER);");
	}

	private void createTaskLocationTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "+ TableType.TaskLocationTable.getTableName() + "("
				+ TaskLocation.ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ON CONFLICT REPLACE,"
				+ TaskLocation.FLD_COUNTRY_CODE + " VARCHAR,"
				+ TaskLocation.FLD_COUNTRY_NAME + " VARCHAR,"
				+ TaskLocation.FLD_LOCATION_REGION + " CHAR,"
				+ TaskLocation.FLD_WORK_LOCATION_ID + " INTEGER,"
				+ TaskLocation.FLD_LONGITUDE + " VARCHAR,"
				+ TaskLocation.FLD_LATITUDE + " VARCHAR,"
				+ TaskLocation.FLD_LOC_GEO_AREA + " VARCHAR,"
				+ TaskLocation.FLD_MOBILE_REC_ID + " INTEGER);");
	}
	
	private void createTaskDoerTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "+ TableType.TaskDoerTable.getTableName() + "("
				+ TaskDoer.ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ON CONFLICT REPLACE,"
				+ TaskDoer.FLD_USER_ID + " LONG,"
				+ TaskDoer.FLD_TASK_TASKER_ID + " LONG,"
				+ TaskDoer.FLD_FULL_NAME + " VARCHAR,"
				+ TaskDoer.FLD_USER_IMAGE + " VARCHAR,"
				+ TaskDoer.FLD_IS_INVITED + " INTEGER,"
				+ TaskDoer.FLD_SEAL_MY_PROPOSAL + " INTEGER,"
				+ TaskDoer.FLD_SELECTION_TYPE + " VARCHAR,"
				+ TaskDoer.FLD_STATUS + " VARCHAR,"
				+ TaskDoer.FLD_PROJECT_STATUS + " VARCHAR,"
				+ TaskDoer.FLD_CANCEL_REQ_BY + " LONG,"
				+ TaskDoer.FLD_CANCEL_REQ_DATE + " VARCHAR,"
				+ TaskDoer.FLD_CANCEL_REASON_BY_POSTER + " VARCHAR,"
				+ TaskDoer.FLD_CANCEL_REASON_BY_DOER + " VARCHAR,"
				+ TaskDoer.FLD_TASK_COMPLETE_MARKED + " INTEGER,"
				+ TaskDoer.FLD_TASK_COMPLETE_MARK_BY + " LONG,"
				+ TaskDoer.FLD_TASK_COMPLETE_CONFIRM_BY + " LONG,"
				+ TaskDoer.FLD_CANCEL_REFUND_DEMAND_BY_POSTER + " LONG,"
				+ TaskDoer.FLD_CANCEL_REFUND_OFFER_BY_DOER + " LONG,"
				+ TaskDoer.FLD_MOBILE_REC_ID + " INTEGER);");
	}
	
	private void createTaskAttachmentTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "+ TableType.TaskAttachmentTable.getTableName() + "("
				+ Attachment.ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ON CONFLICT REPLACE,"
				+ Attachment.FLD_FILE_URL + " VARCHAR,"
				+ Attachment.FLD_FILE_NAME + " VARCHAR,"
				+ Attachment.FLD_MOBILE_REC_ID + " INTEGER);");
	}
	//{"user_id":"394","is_invited":"1","selection_type":"bid"}
	private void createWorkLocationTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "+ TableType.WorkLocationTable.getTableName() + "("
				+ WorkLocation.FLD_WORK_LOCATION_ID + " INTEGER PRIMARY KEY  NOT NULL ON CONFLICT REPLACE,"
				+ WorkLocation.FLD_COUNTRY_CODE + " VARCHAR,"
				+ WorkLocation.FLD_COUNTRY_NAME + " VARCHAR,"
				+ WorkLocation.FLD_STATE_ID + " INTEGER,"
				+ WorkLocation.FLD_STATE_NAME + " VARCHAR,"
				+ WorkLocation.FLD_REGION_ID + " INTEGER,"
				+ WorkLocation.FLD_REGION_NAME + " VARCHAR,"
				+ WorkLocation.FLD_CITY_ID + " INTEGER,"
				+ WorkLocation.FLD_CITY_NAME + " VARCHAR,"
				+ WorkLocation.FLD_ADDRESS + " VARCHAR,"
				+ WorkLocation.FLD_ZIP_CODE + " VARCHAR,"
				+ WorkLocation.FLD_LOCATION_NAME + " VARCHAR,"
				+ WorkLocation.FLD_IS_DEFAULT_LOCATION + " INTEGER,"
				+ WorkLocation.FLD_LATITUDE + " VARCHAR,"
				+ WorkLocation.FLD_LONGITUDE + " VARCHAR,"
				+ WorkLocation.FLD_IS_BILLING + " VARCHAR,"
				+ WorkLocation.FLD_IS_SHIPPING + " VARCHAR,"
				+ WorkLocation.FLD_STATUS + " CHAR);");
		
	}
	
	private void createInboxUserTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "+ TableType.InboxUserTable.getTableName() + "("
				+ MessageDetail.ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ON CONFLICT REPLACE,"
				+ MessageDetail.FLD_MESSAGE_ID + " LONG,"
				+ MessageDetail.FLD_MESSAGE_TYPE + " VARCHAR,"
				+ MessageDetail.FLD_MESSAGE_FLOW + " VARCHAR,"
				+ MessageDetail.FLD_THREAD_KEY + " VARCHAR,"
				+ MessageDetail.FLD_TASK_ID + " LONG,"
				+ MessageDetail.FLD_TASK_TITLE + " VARCHAR,"
				+ MessageDetail.FLD_FROM_USER_ID + " LONG,"
				+ MessageDetail.FLD_MESSAGE_FROM_NAME + " VARCHAR,"
				+ MessageDetail.FLD_MESSAGE_TO_NAME + " VARCHAR,"
				+ MessageDetail.FLD_TO_USER_ID + " LONG,"
				+ MessageDetail.FLD_SUBJECT + " VARCHAR,"
				+ MessageDetail.FLD_BODY + " VARCHAR,"
				+ MessageDetail.FLD_ATTACHMENT + " VARCHAR,"
				+ MessageDetail.FLD_IS_PUBLIC + " VARCHAR,"
				+ MessageDetail.FLD_IS_READ + " VARCHAR,"
				+ MessageDetail.FLD_IS_DELETE + " VARCHAR,"
				+ MessageDetail.FLD_CREATED_AT + " VARCHAR,"
				+ MessageDetail.FLD_CREATED_BY + " VARCHAR,"
				+ MessageDetail.FLD_UPDATED_AT + " VARCHAR,"
				+ MessageDetail.FLD_UPDATED_BY + " VARCHAR,"
				+ MessageDetail.FLD_SOURCE_APP + " VARCHAR,"
				+ MessageDetail.FLD_STATUS + " VARCHAR,"
				+ MessageDetail.FLD_TRNO + " LONG,"
				+ MessageDetail.FLD_DELIVERRY_STATUS + " VARCHAR);");
	}
	
	private void createPaymentRequestTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "+ TableType.PaymentRequestTable.getTableName() + "("
				+ PaymentRequest.FLD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ON CONFLICT REPLACE,"
				+ PaymentRequest.FLD_PAYMENT_REQUEST_ID + " LONG,"
				+ PaymentRequest.FLD_PAYMENT_TYPE + " CHAR,"
				+ PaymentRequest.FLD_PAYMENT_SUB_TYPE + " VARCHAR,"
				+ PaymentRequest.FLD_INVOICE_ID + " LONG,"
				+ PaymentRequest.FLD_NET_BILL_AMT + " DOUBLE,"
				+ PaymentRequest.FLD_BONUS_AMT + " DOUBLE,"
				+ PaymentRequest.FLD_PENALITY_AMT + " DOUBLE,"
				+ PaymentRequest.FLD_FEE_TYPE + " CHAR,"
				+ PaymentRequest.FLD_FEE_PERCENT + " DOUBLE,"
				+ PaymentRequest.FLD_FEE_AMT + " DOUBLE,"
				+ PaymentRequest.FLD_RELEASED_AMT + " DOUBLE,"
				+ PaymentRequest.FLD_RELEASED_AMT_DETAIL + " VARCHAR,"
				+ PaymentRequest.FLD_PAYMENT_CURRENCY + " VARCHAR,"
				+ PaymentRequest.FLD_BY_USER_ID + " LONG,"
				+ PaymentRequest.FLD_BY_TEAM_ID + " LONG,"
				+ PaymentRequest.FLD_BY_USER_ID_TYPE + " CHAR,"
				+ PaymentRequest.FLD_TO_USER_ID + " LONG,"
				+ PaymentRequest.FLD_TO_TEAM_ID + " LONG,"
				+ PaymentRequest.FLD_TO_USER_ID_TYPE + " CHAR,"
				+ PaymentRequest.FLD_PAYMENT_GATEWAY_RC + " VARCHAR,"
				+ PaymentRequest.FLD_PAYMENT_GATEWAY_MSG + " VARCHAR,"
				+ PaymentRequest.FLD_PAYMENT_GATEWAY_REF_ID + " VARCHAR,"
				+ PaymentRequest.FLD_CREATED_AT + " DATETIME,"
				+ PaymentRequest.FLD_CREATED_BY + " LONG,"
				+ PaymentRequest.FLD_UPDATED_AT + " DATETIME,"
				+ PaymentRequest.FLD_UPDATED_BY + " LONG,"
				+ PaymentRequest.FLD_SOURCE_APP + " VARCHAR,"
				+ PaymentRequest.FLD_STATUS + " CHAR,"
				+ PaymentRequest.FLD_TRNO + " LONG);");
	}
	
	private void createPaymentTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS "+ TableType.PaymentTable.getTableName() + "("
				+ Payment.FLD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ON CONFLICT REPLACE,"
				+ Payment.FLD_PAYMENT_ID + " LONG,"
				+ Payment.FLD_USER_ID + " LONG,"
				+ Payment.FLD_AMOUNT + " DOUBLE,"
				+ Payment.FLD_PAYMENT_TYPE + " VARCHAR,"
				+ Payment.FLD_PAYMENT_DESC + " VARCHAR,"
				+ Payment.FLD_PAYMENT_REQUEST_ID + " LONG,"
				+ Payment.FLD_PAYMENT_REF_ID + " VARCHAR,"
				+ Payment.FLD_PAYMENT_SERVICE_PROVIDER + " VARCHAR,"
				+ Payment.FLD_CREATED_AT + " DATETIME,"
				+ Payment.FLD_CREATED_BY + " LONG,"
				+ Payment.FLD_UPDATED_AT + " DATETIME,"
				+ Payment.FLD_UPDATED_BY + " LONG,"
				+ Payment.FLD_SOURCE_APP + " VARCHAR,"
				+ Payment.FLD_TRNO + " LONG);");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		// DB Migration based on DB_VERSION
		if (oldVersion == 1) {
			/*
			 * db.execSQL(" ALTER TABLE " + TableType.InboxTable +
			 * " MODIFY COLUMN " + Inbox.STATUS + " TEXT DEFAULT 'a'");
			 */
		}

	}

}
