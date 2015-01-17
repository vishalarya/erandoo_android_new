package erandoo.app.database;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import erandoo.app.logger.Logger;
import erandoo.app.main.AppGlobals;
import erandoo.app.main.CreateProjectActivity;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.tempclasses.MessageData;
import erandoo.app.tempclasses.ProjectData;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;

public class DatabaseMgr {
	public static String DB_NAME = "erandoo";

	private Logger logger = null;
	private Context context = null;

	private String TAG = "Database";
	public SQLiteDatabase sqLiteDb = null;
	// public SQLiteDatabase sqLiteDbRO = null;
	private static DatabaseMgr dbObj = null;
	
	private DatabaseMgr() {

	}

	public static DatabaseMgr getInstance(Context context) {
		if (dbObj == null) {
			dbObj = new DatabaseMgr();
			dbObj.init(context, null);
		}
		return dbObj;
	}

	/**
	 * This method is called to initialize database module.
	 */
	private boolean init(Context context, Logger logger) {
		if (context == null) {
			return false;
		}

		if (logger == null)
			this.logger = new Logger(TAG);
		else {
			this.logger = logger;
		}
		this.context = context;
		createDatabase();

		if (Logger.IS_INTERNAL_RELEASE)
			this.logger.debug("init() : initialized successfully ");
		return true;
	}

	/**
	 * This method is called to create new database if not created already.
	 * 
	 * @return boolean
	 */
	private boolean createDatabase() {
		if (Logger.IS_INTERNAL_RELEASE)
			logger.debug("creating db");
		DBHelper dbHelper = new DBHelper(context);
		sqLiteDb = dbHelper.getWritableDatabase();
		// sqLiteDbRO = dbHelper.getReadableDatabase();
		sqLiteDb.setPageSize(4 * 1024);// default is 1 K
		return true;
	}

	/**
	 * This method is used to insert data in the table.
	 * 
	 * @param tableType
	 * @param contentValues
	 * @return
	 */
	public int insertInDB(TableType tableType, ContentValues[] contentValues) {
		int retCode = -1;
		if (contentValues != null) {
			try {
				dbObj.sqLiteDb.beginTransaction();
				for (ContentValues contactValue : contentValues) {
					try {
						if (contactValue == null)
							return 0;
						// retCode = (int)
						// sqLiteDb.insert(tableType.toString(),null,
						// contactValue);
						retCode = (int) sqLiteDb.insertWithOnConflict(
								tableType.getTableName(), null, contactValue,
								SQLiteDatabase.CONFLICT_REPLACE);
					} catch (Exception e) {
						logger.fatal("insertInDB() : Exception is : " + e);
					}
				}
			} catch (Exception e) {
				if (Logger.IS_INTERNAL_RELEASE) {
					logger.info("insertGroupDetailsByGroupContactBean() : insertInDB  "
							+ e);
				}
			} finally {
				dbObj.sqLiteDb.setTransactionSuccessful();
				dbObj.sqLiteDb.endTransaction();
			}
		}
		return retCode;
	}

	// ------------------------------//
	/**
	 * This method is used to insert data in the table.
	 * 
	 * @param tableType
	 * @param contentValues
	 * @return number of insert value
	 */
	public int insertIntoDBForGroup(TableType tableType,
			ContentValues[] contentValues) {
		int numberOfRowInsert = 0;

		if (contentValues != null) {
			try {
				dbObj.sqLiteDb.beginTransaction();
				for (ContentValues contactValue : contentValues) {
					try {
						if (contactValue == null)
							return 0;
						sqLiteDb.insert(tableType.getTableName(), null,
								contactValue);
						numberOfRowInsert++;
					} catch (Exception e) {
						if (Logger.IS_INTERNAL_RELEASE)
							logger.error("Databse::insertInDB:: exception::  "
									+ e);

					}
				}
			} finally {
				dbObj.sqLiteDb.setTransactionSuccessful();
				dbObj.sqLiteDb.endTransaction();
			}
		}
		return numberOfRowInsert;
	}

	/**
	 * This method is used to query data from the table.
	 * 
	 * @param tableType
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return
	 */
	public Cursor queryTable(TableType tableType, String[] columns,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy) {
		Cursor cur = null;
		try {
			cur = sqLiteDb.query(tableType.getTableName(), columns, selection,
					selectionArgs, groupBy, having, orderBy);
		} catch (Exception e) {

			logger.fatal("queryTable() : Exception is : " + e);

			if (cur != null && !cur.isClosed()) {
				cur.close();
			}
			cur = null;
		}
		return cur;
	}

	/**
	 * This method is used to updated data in the table.
	 * 
	 * @param tableType
	 * @param values
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	public int updateTable(TableType tableType, ContentValues values,
			String whereClause, String[] whereArgs) {
		int rsult = -1;
		try {
			rsult = sqLiteDb.update(tableType.getTableName(), values,
					whereClause, whereArgs);
		} catch (Exception e) {
			logger.fatal("updateTable() : Exception is : " + e);
		}
		return rsult;
	}

	/**
	 * This method is used to make raw query in the table.
	 * 
	 * @param query
	 * @param selArgs
	 * @return
	 */
	public Cursor rawQuery(String query, String[] selArgs) {
		Cursor cur = null;
		try {
			cur = sqLiteDb.rawQuery(query, selArgs);
		} catch (Exception e) {
			// e.printStackTrace();
			logger.fatal("rawQuery() : Exception is : " + e);
			if (cur != null && !cur.isClosed()) {
				cur.close();
			}
			cur = null;
		}
		return cur;
	}

	/**
	 * This method is used to delete rows from the table.
	 * 
	 * @param tableType
	 * @param whereClause
	 * @param whereArgs
	 * @return
	 */
	public int deleteTableRow(TableType tableType, String whereClause,
			String[] whereArgs) {
		int retCode = -1;
		try {
			retCode = sqLiteDb.delete(tableType.getTableName(), whereClause,
					whereArgs);
		} catch (Exception e) {
			if (Logger.IS_INTERNAL_RELEASE)// This log is not print now.
				logger.error("deleteTableRow() : Exception is : " + e);
		}
		return retCode;
	}
	
	
	public void clearUserData(){
		try{
			String tableName = TableType.InboxUserTable.getTableName();
			clearDbTable(tableName);
			
			tableName = TableType.TaskTable.getTableName();
			clearDbTable(tableName);
			
			tableName = TableType.TaskDoerTable.getTableName();
			clearDbTable(tableName);
			
			tableName = TableType.TaskLocationTable.getTableName();
			clearDbTable(tableName);
			
			tableName = TableType.TaskQuestionTable.getTableName();
			clearDbTable(tableName);
			
			tableName = TableType.TaskSkillTable.getTableName();
			clearDbTable(tableName);
			
			tableName = TableType.WorkLocationTable.getTableName();
			clearDbTable(tableName);
			
			tableName = TableType.TaskAttachmentTable.getTableName();
			clearDbTable(tableName);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void clearDbTable(String tableName){
		try{
			sqLiteDb.execSQL("DELETE FROM "+ tableName);
			sqLiteDb.execSQL("DELETE FROM sqlite_sequence WHERE  name='"+tableName+"'"); 
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	// --------------------------Specific Functions -----------------------

	public NetworkResponse createTask(Project project) {
		NetworkResponse response = null;
	    try{
	    	CreateProjectActivity.updateMobileRecId(insertUpdateTaskData(project));
	    	response = NetworkMgr.postPendingTasks(getMyTasks(Constants.DELIVERY_STATUS_PENDING,Constants.MY_PROJECT_AS_POSTER,null));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	
	public Long updateTaskData(NetworkResponse response, Project rProject,Long mTrno) {
		Long maxTrno = null;
		try {
			maxTrno = rProject != null ? rProject.trno : mTrno;
			ContentValues[] contentValues;
			if (response.isSuccess()) {
				ProjectData projectData = (ProjectData)Util.getJsonToClassObject(response.getJsonObject().toString(), ProjectData.class);
				if(projectData != null){
				//	int count = 0; // only for test
					if(projectData.data.size() > 0){
						for (Project project: projectData.data) {
							project.category.category_id = Long.valueOf(project.category_id);
							//Log.v("project.task_id-> ", String.valueOf(project.task_id));
							project._Id = Util.getMobileRecId(project.mobile_rec_id);
							project.delivery_status = Constants.DELIVERY_STATUS_SENT;

							if (maxTrno < project.trno) {
								maxTrno = project.trno;
							}
							project.trno = maxTrno;
							
							if(project._Id == null){
								project._Id = checkForTaskData(project.task_id);
							}
							insertUpdateTaskData(project);
						}
					}else{
						return null;
					}
				}
			} else if(rProject != null){
				contentValues = new ContentValues[1];
				rProject.delivery_status = Constants.DELIVERY_STATUS_ERROR;
				contentValues[0] = getConValueForProject(rProject);
				insertInDB(TableType.TaskTable, contentValues);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return maxTrno;
	}
	
	private Long insertUpdateTaskData(Project project){
		ContentValues[] contentValues;
		Long rId = sqLiteDb.insertWithOnConflict(TableType.TaskTable.getTableName(), null, getConValueForProject(project),SQLiteDatabase.CONFLICT_REPLACE);
		//	count ++;
		//	Log.v("project.task_id->"+String.valueOf(project.task_id), "Mobile_rec_token->"+project.mobile_rec_id==null?"null":project.mobile_rec_id+" :: New inserted Mobile Rec Id->"+String.valueOf(rId)+" :: Record Count->"+String.valueOf(count));
			
		if(!rId.equals(Constants.INVALID_ID)){
	    	try{
				String[] recId = new String[]{String.valueOf(rId)}; 
				int index = 0;
				if(project.multiskills.size() > 0){
					deleteTableRow(TableType.TaskSkillTable, TaskSkill.FLD_MOBILE_REC_ID+"=?", recId);
					contentValues = new ContentValues[project.multiskills.size()];
					index = 0;
					for(Skill skill: project.multiskills){
						ContentValues values = new ContentValues();
						values.put(TaskSkill.FLD_MOBILE_REC_ID, rId);
						values.put(TaskSkill.FLD_SKILL_ID,skill.skill_id); 
						values.put(TaskSkill.FLD_SKILL_DESC,skill.skill_desc); 
						contentValues[index] = values;
						index ++;
					}
					insertIntoDBForGroup(TableType.TaskSkillTable, contentValues);
				}
				
				if(project.multicatquestion.size() > 0){
					deleteTableRow(TableType.TaskQuestionTable, TaskQuestion.FLD_MOBILE_REC_ID+"=?", recId);
					contentValues = new ContentValues[project.multicatquestion.size()];
					index = 0;
					for(Question question: project.multicatquestion){
						ContentValues values = new ContentValues();
						values.put(TaskQuestion.FLD_MOBILE_REC_ID, rId);
						values.put(TaskQuestion.FLD_QUESTION_ID, question.question_id); 
						values.put(TaskQuestion.FLD_QUESTION_DESC, question.question_desc);
						values.put(TaskQuestion.FLD_TASK_QUESTION_ID, question.task_question_id);
						contentValues[index] = values;
						index ++;
					}
					insertIntoDBForGroup(TableType.TaskQuestionTable, contentValues);
				}
				
				if(project.multilocations.size() > 0){
					deleteTableRow(TableType.TaskLocationTable, TaskLocation.FLD_MOBILE_REC_ID+"=?", recId);
					contentValues = new ContentValues[project.multilocations.size()];
					index = 0;
					for (Country country : project.multilocations) {
						ContentValues values = new ContentValues();
						values.put(TaskLocation.FLD_MOBILE_REC_ID, rId);
						values.put(TaskLocation.FLD_COUNTRY_CODE, country.country_code);
						values.put(TaskLocation.FLD_COUNTRY_NAME, country.country_name);
						values.put(TaskLocation.FLD_LOCATION_REGION, project.is_location_region);
						contentValues[index] = values;
						index ++;
					}
					insertIntoDBForGroup(TableType.TaskLocationTable, contentValues);
				}
				
				if(project.invitedtaskers.size() > 0){
					deleteTableRow(TableType.TaskDoerTable, TaskDoer.FLD_MOBILE_REC_ID+"=?", recId);
					contentValues = new ContentValues[project.invitedtaskers.size()];
					index = 0;
					for (TaskDoer doer : project.invitedtaskers) {
						ContentValues values = new ContentValues();
						values.put(TaskDoer.FLD_MOBILE_REC_ID, rId);
						values.put(TaskDoer.FLD_USER_ID, doer.user_id);
						values.put(TaskDoer.FLD_TASK_TASKER_ID, doer.task_tasker_id);
						values.put(TaskDoer.FLD_FULL_NAME, doer.fullname);
						values.put(TaskDoer.FLD_USER_IMAGE, doer.userimage);
						values.put(TaskDoer.FLD_IS_INVITED, doer.is_invited);
						values.put(TaskDoer.FLD_SELECTION_TYPE, doer.selection_type);
						values.put(TaskDoer.FLD_STATUS, doer.status);
						values.put(TaskDoer.FLD_PROJECT_STATUS, doer.project_status);
						values.put(TaskDoer.FLD_CANCEL_REQ_BY, doer.cancel_req_by);
						values.put(TaskDoer.FLD_CANCEL_REQ_DATE, doer.cancel_req_date);
						values.put(TaskDoer.FLD_CANCEL_REASON_BY_POSTER, doer.cancel_reason_by_poster);
						values.put(TaskDoer.FLD_CANCEL_REASON_BY_DOER, doer.cancel_reason_by_doer);
						values.put(TaskDoer.FLD_TASK_COMPLETE_MARKED, doer.task_complete_marked);
						values.put(TaskDoer.FLD_TASK_COMPLETE_MARK_BY, doer.task_complete_mark_by);
						values.put(TaskDoer.FLD_TASK_COMPLETE_CONFIRM_BY, doer.task_complete_confirm_by);
						values.put(TaskDoer.FLD_CANCEL_REFUND_DEMAND_BY_POSTER, doer.cancel_refund_demand_by_poster);
						values.put(TaskDoer.FLD_CANCEL_REFUND_OFFER_BY_DOER, doer.cancel_refund_offer_by_doer);
						values.put(TaskDoer.FLD_SEAL_MY_PROPOSAL, doer.seal_my_proposal);
						contentValues[index] = values;
						index ++;
					}
					insertIntoDBForGroup(TableType.TaskDoerTable, contentValues);
				}
				//------for in-person and instant project
				if(project.tasklocation.size() > 0){ 
					deleteTableRow(TableType.TaskLocationTable, TaskLocation.FLD_MOBILE_REC_ID+"=?", recId);
					contentValues = new ContentValues[project.tasklocation.size()];
					index = 0;
					for (WorkLocation workLocation : project.tasklocation) {
						ContentValues values = new ContentValues();
						values.put(TaskLocation.FLD_MOBILE_REC_ID, rId);
						values.put(TaskLocation.FLD_LOCATION_REGION, project.is_location_region);
						values.put(TaskLocation.FLD_WORK_LOCATION_ID, workLocation.work_location_id);
						values.put(TaskLocation.FLD_LONGITUDE, workLocation.longitude);
						values.put(TaskLocation.FLD_LATITUDE, workLocation.latitude);
						values.put(TaskLocation.FLD_LOC_GEO_AREA, workLocation.location_geo_area);
						contentValues[index] = values;
						index ++;
					}
					insertIntoDBForGroup(TableType.TaskLocationTable, contentValues);
				}
				
				if(project.attachments.size() > 0){ 
					deleteTableRow(TableType.TaskAttachmentTable, TaskQuestion.FLD_MOBILE_REC_ID+"=?", recId);
					contentValues = new ContentValues[project.attachments.size()];
					index = 0;
					for (Attachment attachment : project.attachments) {
						ContentValues values = new ContentValues();
						values.put(Attachment.FLD_MOBILE_REC_ID, rId);
						values.put(Attachment.FLD_FILE_URL, attachment.file_url);
						values.put(Attachment.FLD_FILE_NAME, attachment.file_name);
						contentValues[index] = values;
						index ++;
					}
					insertIntoDBForGroup(TableType.TaskAttachmentTable, contentValues);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}else{
			return null;
		}
		return rId;
	}
	

	public ArrayList<Project> getMyTasks(String deliveryStatus,String projectAs,String filterBy) {
		ArrayList<Project> projects = new ArrayList<Project>(0);
		Cursor cursor = null;
		String[] selectionArgValue = null;
		try {
			if(deliveryStatus.equals(Constants.DELIVERY_STATUS_PENDING)){ 
				cursor = queryTable(TableType.TaskTable, null,
									Project.FLD_DELIVERRY_STATUS + "=?",
									new String[] { Constants.DELIVERY_STATUS_PENDING }, null,
									null, Project.FLD_ID + " DESC");
			}else{
				
				String selectionArg = projectAs.equals(Constants.MY_PROJECT_AS_POSTER) ? 
									  Project.FLD_CREATOR_USER_ID+"=?" : Project.FLD_CREATOR_USER_ID+"!=?";
				if(filterBy != null){
					if(filterBy.equals("ap")||filterBy.equals("o")||filterBy.equals("a"))
					{
					selectionArgValue=new String[2];
					selectionArgValue[0]=String.valueOf( AppGlobals.userDetail.user_id);
					selectionArg =selectionArg+" AND "+Project.FLD_STATE +"=?";
					selectionArgValue[1] = filterBy;
					}
				}else{
					selectionArgValue=new String[1];
					selectionArgValue[0]=String.valueOf( AppGlobals.userDetail.user_id);
				}
				
				if(projectAs.equals(Constants.MY_PROJECT_AS_POSTER)){
						cursor = queryTable(TableType.TaskTable, null,selectionArg,selectionArgValue, null,null, null);
				}else{ 
					if(filterBy != null&&filterBy.equals("inv")){
						String query="SELECT t.*,td.* FROM task as t ,task_doer as td where t._id = td.mobile_rec_id and td.user_id="+ AppGlobals.userDetail.user_id+" and td.is_invited=1";
						cursor=dbObj.rawQuery(query, null);
					}
					else if(filterBy != null&&filterBy.equals("f")){
						String query="SELECT t.*,td.* FROM task as t ,task_doer as td where t._id = td.mobile_rec_id and td.user_id="+ AppGlobals.userDetail.user_id+" and td."+ TaskDoer.FLD_TASK_COMPLETE_CONFIRM_BY+" IS NOT NULL";
						cursor=dbObj.rawQuery(query, null);
					}
					else
					{
					cursor = queryTable(TableType.TaskTable, null,selectionArg,selectionArgValue, null,null, null);
					}
					
				}
			}
			
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				while (!cursor.isAfterLast()) {
                  projects.add(getProjectData(deliveryStatus,cursor));
				  cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return projects;
	}
	
	
	private Project getProjectData(String deliveryStatus,Cursor cursor){
		Project project = new Project();
		project._Id = cursor.getLong(cursor.getColumnIndex(Project.FLD_ID));
		Long task_id = cursor.getLong(cursor.getColumnIndex(Project.FLD_TASK_ID));
		project.task_id = task_id == 0 ? null : task_id;
		project.category.category_id = cursor.getLong(cursor.getColumnIndex(Project.FLD_CATEGORY_ID));
		project.category_id = cursor.getLong(cursor.getColumnIndex(Project.FLD_CATEGORY_ID));
		project.is_location_region = cursor.getString(cursor.getColumnIndex(Project.FLD_LOCATION_REGION));
		project.task_kind = cursor.getString(cursor.getColumnIndex(Project.FLD_PROJECT_TYPE));
		project.title = cursor.getString(cursor.getColumnIndex(Project.FLD_TITLE));
		project.description = cursor.getString(cursor.getColumnIndex(Project.FLD_DESCRIPTION));
		project.end_date = cursor.getString(cursor.getColumnIndex(Project.FLD_END_DATE));
		project.end_time = cursor.getString(cursor.getColumnIndex(Project.FLD_END_TIME));
		project.bid_duration = cursor.getString(cursor.getColumnIndex(Project.FLD_BID_DURATION));
		project.min_price = cursor.getString(cursor.getColumnIndex(Project.FLD_MIN_PRICE));
		project.max_price = cursor.getString(cursor.getColumnIndex(Project.FLD_MAX_PRICE));
		project.cash_required = cursor.getString(cursor.getColumnIndex(Project.FLD_EXPENSES));
		project.is_public = cursor.getString(cursor.getColumnIndex(Project.FLD_IS_PUBLIC));
		project.payment_mode = cursor.getString(cursor.getColumnIndex(Project.FLD_PAYMENT_MODE));
		project.price = cursor.getString(cursor.getColumnIndex(Project.FLD_PRICE));
		project.work_hrs = cursor.getString(cursor.getColumnIndex(Project.FLD_WORK_HRS));
		project.is_highlighted = cursor.getString(cursor.getColumnIndex(Project.FLD_IS_HIGH_LIGHTED));
		project.seal_all_proposal = cursor.getString(cursor.getColumnIndex(Project.FLD_SEAL_ALL_PROPOSAL));
		project.is_premium = cursor.getString(cursor.getColumnIndex(Project.FLD_IS_PREMIUM));
		project.delivery_status = cursor.getString(cursor.getColumnIndex(Project.FLD_DELIVERRY_STATUS));
		project.tasker_in_range = cursor.getString(cursor.getColumnIndex(Project.FLD_TASKER_IN_RANGE));
		project.userimage = cursor.getString(cursor.getColumnIndex(Project.FLD_USERIMAGE));
		project.selection_type = cursor.getString(cursor.getColumnIndex(Project.FLD_SELECTION_TYPE));
		project.trno = cursor.getLong(cursor.getColumnIndex(Project.FLD_TRNO));
		if(deliveryStatus.equals(Constants.DELIVERY_STATUS_SENT )){ 
			project.creator_user_id = cursor.getLong(cursor.getColumnIndex(Project.FLD_CREATOR_USER_ID));
			project.name = cursor.getString(cursor.getColumnIndex(Project.FLD_NAME));
			project.proposals_cnt = cursor.getString(cursor.getColumnIndex(Project.FLD_PROPOSALS_COUNT));
			project.proposals_avg_price = cursor.getString(cursor.getColumnIndex(Project.FLD_PROPOSALS_AVG_PRICE));
			project.creator_role = cursor.getString(cursor.getColumnIndex(Project.FLD_CREATOR_ROLE));
			project.is_external = cursor.getString(cursor.getColumnIndex(Project.FLD_IS_EXTERNAL));
			project.hiring_closed = cursor.getString(cursor.getColumnIndex(Project.FLD_HIRING_CLOSED));
			project.state = cursor.getString(cursor.getColumnIndex(Project.FLD_STATE));
			project.price_currency = cursor.getString(cursor.getColumnIndex(Project.FLD_PRICE_CURRENCY));
			project.task_assigned_on = cursor.getString(cursor.getColumnIndex(Project.FLD_TASK_ASSIGNED_ON));
			project.created_at = cursor.getString(cursor.getColumnIndex(Project.FLD_CREATE_AT));
			project.created_by = cursor.getString(cursor.getColumnIndex(Project.FLD_CREATE_BY));
			project.updated_at = cursor.getString(cursor.getColumnIndex(Project.FLD_UPDATE_AT));
			project.updated_by = cursor.getString(cursor.getColumnIndex(Project.FLD_UPDATED_BY));
			project.status = cursor.getString(cursor.getColumnIndex(Project.FLD_STATUS));
			project.bid_start_dt = cursor.getString(cursor.getColumnIndex(Project.FLD_BID_START_DATE));
			project.bid_close_dt = cursor.getString(cursor.getColumnIndex(Project.FLD_BID_CLOSE_DATE));
			project.project_start_date =  cursor.getString(cursor.getColumnIndex(Project.FLD_PROJECT_START_DATE));
			project.average_rating =  cursor.getString(cursor.getColumnIndex(Project.FLD_AVERAGE_RATING));
		}
		
		return project;
	}
	
	public JSONArray getTaskSkillJSONArray(String mTaskId){ 
		JSONArray json = new JSONArray();
		try{
			ArrayList<Skill> skills= getTaskSkill(mTaskId);
			for (Skill skill: skills) {
				json.put(skill.skill_id);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return json;
	}
	
	public ArrayList<Skill> getTaskSkill(String mTaskId){ 
		Cursor cursor = null;
		ArrayList<Skill> skills = new ArrayList<Skill>(0);
		try{
			cursor = queryTable(TableType.TaskSkillTable, new String[] {TaskSkill.FLD_SKILL_ID,TaskSkill.FLD_SKILL_DESC},
								TaskSkill.FLD_MOBILE_REC_ID + "=?",new String[] {mTaskId}, null,null,null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				while (!cursor.isAfterLast()) {
					Skill skill = new Skill();
					skill.skill_id = cursor.getString(cursor.getColumnIndex(TaskSkill.FLD_SKILL_ID));
					skill.skill_desc = cursor.getString(cursor.getColumnIndex(TaskSkill.FLD_SKILL_DESC));
					skills.add(skill);
					cursor.moveToNext();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return skills;
	}
	
	//TODO
	public JSONArray getTaskLocationJSONArray(String mTaskId){ 
		JSONArray json = new JSONArray();
		try{
			ArrayList<Country> locations = getTaskLocation(mTaskId);
			for (Country country: locations) {
				json.put(country.country_code); 
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return json;
	}
	
	public ArrayList<Country> getTaskLocation(String mTaskId){  
		Cursor cursor = null;
		ArrayList<Country> locations = new ArrayList<Country>(0);
		try{
			cursor = queryTable(TableType.TaskLocationTable, new String[] {TaskLocation.FLD_COUNTRY_CODE,TaskLocation.FLD_COUNTRY_NAME},
								TaskLocation.FLD_MOBILE_REC_ID + "=?",new String[] {mTaskId}, null,null,null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				while (!cursor.isAfterLast()) {
					Country country = new Country();
					country.country_code = cursor.getString(cursor.getColumnIndex(TaskLocation.FLD_COUNTRY_CODE));
					country.country_name = cursor.getString(cursor.getColumnIndex(TaskLocation.FLD_COUNTRY_NAME));
					locations.add(country);
					cursor.moveToNext();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return locations;
	}
	
	public JSONArray getTaskQuestion(String mTaskId){ 
		Cursor cursor = null;
		JSONArray questions = new JSONArray();
		try{
			cursor = rawQuery("SELECT cq."+Question.FLD_QUESTION_ID+" , cq."
								+Question.FLD_QUESTION_DESC+" FROM "
								+TableType.CategoryQuestionTable.getTableName()+" AS cq , "
								+TableType.TaskQuestionTable.getTableName()+" AS tq WHERE cq."
								+Question.FLD_QUESTION_ID+" = tq."
								+TaskQuestion.FLD_QUESTION_ID+" AND tq."
								+TaskQuestion.FLD_MOBILE_REC_ID+" = "+mTaskId, null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				while (!cursor.isAfterLast()) {
					questions.put(cursor.getString(cursor.getColumnIndex(TaskQuestion.FLD_QUESTION_ID))+"--"+
							      cursor.getString(cursor.getColumnIndex(Question.FLD_QUESTION_DESC))); 
					cursor.moveToNext();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return questions;
	}
	
	public JSONArray getTaskDoers(String mTaskId){ 
		Cursor cursor = null;
		JSONArray taskDoers = new JSONArray();
		try{
			cursor = queryTable(TableType.TaskDoerTable, new String[] {TaskDoer.FLD_USER_ID},
							TaskDoer.FLD_MOBILE_REC_ID + "=?",new String[] {mTaskId}, null,null,null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				while (!cursor.isAfterLast()) {
					taskDoers.put(cursor.getString(cursor.getColumnIndex(TaskDoer.FLD_USER_ID))); 
					cursor.moveToNext();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return taskDoers;
	}
	
	
	
	public WorkLocation getTaskWLocation(String mTaskId){
		Cursor cursor = null;
		WorkLocation location = new WorkLocation();
		try{
			cursor = queryTable(TableType.TaskLocationTable, new String[] {TaskLocation.FLD_WORK_LOCATION_ID,
																			TaskLocation.FLD_LATITUDE,
																			TaskLocation.FLD_LONGITUDE,
																			TaskLocation.FLD_LOC_GEO_AREA},
							TaskDoer.FLD_MOBILE_REC_ID + "=?",new String[] {mTaskId}, null,null,null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				location.work_location_id = cursor.getString(cursor.getColumnIndex(TaskLocation.FLD_WORK_LOCATION_ID));
				location.latitude = cursor.getString(cursor.getColumnIndex(TaskLocation.FLD_LATITUDE));
				location.longitude = cursor.getString(cursor.getColumnIndex(TaskLocation.FLD_LONGITUDE));
				location.address = cursor.getString(cursor.getColumnIndex(TaskLocation.FLD_LOC_GEO_AREA));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return location;
	}
	
	/**
	 * This function to get skill and location names in comma separated string format
	 * @param tableName
	 * @param columnName
	 * @param mTaskId here->mobile_rec_id;
	 * @return
	 */
	public String getGroupConcatOnTaskTables(TableType table, String columnName, String mTaskId){
		Cursor cursor = null;
		String str = null;
		try{
			cursor = rawQuery("SELECT group_concat("+columnName+",', ') FROM "+table.getTableName()+" where mobile_rec_id ="+mTaskId, null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				str = cursor.getString(0); 
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return str;
	}
	
	public ArrayList<TaskDoer> getDoerList(Long mTaskId){
		ArrayList<TaskDoer> doersList = new ArrayList<TaskDoer>();
		Cursor cursor = null;
		try {
			cursor = queryTable(TableType.TaskDoerTable,null,TaskDoer.FLD_MOBILE_REC_ID + "=?",new String[] { String.valueOf(mTaskId)}, null, null, null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				do {
					TaskDoer doer = new TaskDoer();
					doer.user_id = cursor.getLong(cursor.getColumnIndex(TaskDoer.FLD_USER_ID));
					doer.task_tasker_id = cursor.getLong(cursor.getColumnIndex(TaskDoer.FLD_TASK_TASKER_ID));
					doer.fullname = cursor.getString(cursor.getColumnIndex(TaskDoer.FLD_FULL_NAME));
					doer.userimage = cursor.getString(cursor.getColumnIndex(TaskDoer.FLD_USER_IMAGE));
					doer.project_status = cursor.getString(cursor.getColumnIndex(TaskDoer.FLD_PROJECT_STATUS));
					doer.task_complete_marked = cursor.getInt(cursor.getColumnIndex(TaskDoer.FLD_TASK_COMPLETE_MARKED));
					doer.task_complete_mark_by = cursor.getLong(cursor.getColumnIndex(TaskDoer.FLD_TASK_COMPLETE_MARK_BY));
					doer.task_complete_confirm_by = cursor.getLong(cursor.getColumnIndex(TaskDoer.FLD_TASK_COMPLETE_CONFIRM_BY));
					doer.cancel_req_by = cursor.getLong(cursor.getColumnIndex(TaskDoer.FLD_CANCEL_REQ_BY));
					doer.cancel_req_date = cursor.getString(cursor.getColumnIndex(TaskDoer.FLD_CANCEL_REQ_DATE));
					doer.cancel_reason_by_poster = cursor.getString(cursor.getColumnIndex(TaskDoer.FLD_CANCEL_REASON_BY_POSTER));
					doer.cancel_reason_by_doer = cursor.getString(cursor.getColumnIndex(TaskDoer.FLD_CANCEL_REASON_BY_DOER));
					doer.cancel_refund_demand_by_poster = cursor.getLong(cursor.getColumnIndex(TaskDoer.FLD_CANCEL_REFUND_DEMAND_BY_POSTER));
					doer.cancel_refund_offer_by_doer = cursor.getLong(cursor.getColumnIndex(TaskDoer.FLD_CANCEL_REFUND_OFFER_BY_DOER));
					doersList.add(doer);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return doersList;
	}

	
	
	
	private Long checkForTaskData(Long taskId){
		Cursor cursor = null;
		try {
			cursor = queryTable(TableType.TaskTable,  new String[]{Project.FLD_ID}, Project.FLD_TASK_ID + "=?", new String[]{String.valueOf(taskId)}, null, null, null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				return cursor.getLong(cursor.getColumnIndex(Project.FLD_ID));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return null;		
	}
	
	private ContentValues getConValueForProject(Project project) {
		ContentValues cValues = new ContentValues();
		if (project._Id != null){
			cValues.put(Project.FLD_ID, project._Id);
		}
			
		cValues.put(Project.FLD_TASK_ID, project.task_id);
		cValues.put(Project.FLD_CATEGORY_ID, project.category_id);
		cValues.put(Project.FLD_LOCATION_REGION, project.is_location_region);
		cValues.put(Project.FLD_PROJECT_TYPE, project.task_kind);
		cValues.put(Project.FLD_TITLE, project.title);
		cValues.put(Project.FLD_DESCRIPTION, project.description);
		cValues.put(Project.FLD_END_DATE, project.end_date);
		cValues.put(Project.FLD_BID_DURATION, project.bid_duration);
		cValues.put(Project.FLD_MIN_PRICE, project.min_price);
		cValues.put(Project.FLD_MAX_PRICE, project.max_price);
		cValues.put(Project.FLD_EXPENSES, project.cash_required);
		cValues.put(Project.FLD_IS_PUBLIC, project.is_public);
		cValues.put(Project.FLD_PAYMENT_MODE, project.payment_mode);
		cValues.put(Project.FLD_PRICE, project.price);
		cValues.put(Project.FLD_WORK_HRS, project.work_hrs);
		cValues.put(Project.FLD_IS_HIGH_LIGHTED, project.is_highlighted);
		cValues.put(Project.FLD_SEAL_ALL_PROPOSAL, project.seal_all_proposal); 
		cValues.put(Project.FLD_IS_PREMIUM, project.is_premium);
		cValues.put(Project.FLD_DELIVERRY_STATUS, project.delivery_status);
		cValues.put(Project.FLD_PROPOSALS_COUNT, project.proposals_cnt);
		cValues.put(Project.FLD_PROPOSALS_AVG_PRICE,project.proposals_avg_price);
		cValues.put(Project.FLD_CREATOR_ROLE, project.creator_role);
		cValues.put(Project.FLD_IS_EXTERNAL, project.is_external);
		cValues.put(Project.FLD_HIRING_CLOSED, project.hiring_closed);
		cValues.put(Project.FLD_STATE, project.state);
		cValues.put(Project.FLD_PRICE_CURRENCY, project.price_currency);
		cValues.put(Project.FLD_TASK_ASSIGNED_ON,project.task_assigned_on);
		cValues.put(Project.FLD_END_TIME, project.end_time);
		cValues.put(Project.FLD_BID_START_DATE, project.bid_start_dt);
		cValues.put(Project.FLD_BID_CLOSE_DATE, project.bid_close_dt);
		cValues.put(Project.FLD_CREATE_AT, project.created_at);
		cValues.put(Project.FLD_CREATE_BY, project.created_by);
		cValues.put(Project.FLD_USERIMAGE, project.userimage);
		cValues.put(Project.FLD_UPDATE_AT, project.updated_at);
		cValues.put(Project.FLD_UPDATED_BY, project.updated_by);
		cValues.put(Project.FLD_TASKER_IN_RANGE, project.tasker_in_range);
		cValues.put(Project.FLD_SELECTION_TYPE, project.selection_type);
		cValues.put(Project.FLD_STATUS, project.status);
		cValues.put(Project.FLD_TRNO, project.trno);
		
		cValues.put(Project.FLD_AVERAGE_RATING, project.average_rating);

		cValues.put(Project.FLD_IS_INVITED, project.is_invited);
		cValues.put(Project.FLD_CREATOR_USER_ID, project.creator_user_id);
		cValues.put(Project.FLD_NAME, project.name);
		cValues.put(Project.FLD_PROJECT_START_DATE, project.project_start_date);//poster_pay_percent
		
		return cValues;
	}

	public long getMaxTrno(TableType tableType, String selection,
			String[] selectionArgs) {
		long trno = 0;
		Cursor cursor = null;
		try {
			cursor = queryTable(tableType, new String[] { Constants.FLD_TRNO },
					null, null, null, null, Constants.FLD_TRNO
							+ " DESC LIMIT 0,1");
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				trno = cursor.getLong(cursor.getColumnIndex(Constants.FLD_TRNO));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return trno;
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return trno;
	}

	
	/**
	 * Function to get project category data
	 * 
	 * @param categoryId
	 * **/
	public Category getProjectCategory(long categoryId) {
		Category category = new Category();
		Cursor cursor = null;
		try {
			cursor = queryTable(TableType.CategoryTable, new String[] {
								Category.FLD_CATEGORY_ID, Category.FLD_CATEGORY_NAME,Category.FLD_PARENT_ID,
								Category.FLD_CATEGORY_IMAGE }, Category.FLD_CATEGORY_ID + "=?",
								new String[] { String.valueOf(categoryId) }, null, null,
								null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				category.category_id = cursor.getLong(cursor.getColumnIndex(Category.FLD_CATEGORY_ID));
				category.parent_id = cursor.getLong(cursor.getColumnIndex(Category.FLD_PARENT_ID));
				category.category_name = cursor.getString(cursor.getColumnIndex(Category.FLD_CATEGORY_NAME));
				category.category_image = cursor.getString(cursor.getColumnIndex(Category.FLD_CATEGORY_IMAGE));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return category;
	}
	
	public TaskDoer getTaskTasker(Long userId, Long mTaskId){
		TaskDoer doer = new TaskDoer();
		Cursor cursor = null;
		try {
			cursor = queryTable(
					TableType.TaskDoerTable,
					new String[] { TaskDoer.FLD_USER_ID,
							TaskDoer.FLD_TASK_TASKER_ID,
							TaskDoer.FLD_IS_INVITED,
							TaskDoer.FLD_SELECTION_TYPE, TaskDoer.FLD_STATUS,
							TaskDoer.FLD_PROJECT_STATUS,
							TaskDoer.FLD_CANCEL_REQ_BY,
							TaskDoer.FLD_CANCEL_REQ_DATE,
							TaskDoer.FLD_TASK_COMPLETE_CONFIRM_BY,
							TaskDoer.FLD_TASK_COMPLETE_MARK_BY,
							TaskDoer.FLD_TASK_COMPLETE_MARKED,
							TaskDoer.FLD_CANCEL_REASON_BY_POSTER,
							TaskDoer.FLD_CANCEL_REASON_BY_DOER,
							TaskDoer.FLD_CANCEL_REFUND_DEMAND_BY_POSTER,
							TaskDoer.FLD_CANCEL_REFUND_OFFER_BY_DOER },
					TaskDoer.FLD_USER_ID + "=? AND "
							+ TaskDoer.FLD_MOBILE_REC_ID + "=?",
					new String[] { String.valueOf(userId),
							String.valueOf(mTaskId) }, null, null, null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				doer.user_id = cursor.getLong(cursor.getColumnIndex(TaskDoer.FLD_USER_ID));
				doer.is_invited = cursor.getString(cursor.getColumnIndex(TaskDoer.FLD_IS_INVITED));
				doer.selection_type = cursor.getString(cursor.getColumnIndex(TaskDoer.FLD_SELECTION_TYPE));
				doer.status = cursor.getString(cursor.getColumnIndex(TaskDoer.FLD_STATUS));
				doer.project_status = cursor.getString(cursor.getColumnIndex(TaskDoer.FLD_PROJECT_STATUS));
				doer.task_tasker_id = cursor.getLong(cursor.getColumnIndex(TaskDoer.FLD_TASK_TASKER_ID));
				
				doer.task_complete_marked = cursor.getInt(cursor.getColumnIndex(TaskDoer.FLD_TASK_COMPLETE_MARKED));
				doer.task_complete_mark_by = cursor.getLong(cursor.getColumnIndex(TaskDoer.FLD_TASK_COMPLETE_MARK_BY));
				doer.task_complete_confirm_by = cursor.getLong(cursor.getColumnIndex(TaskDoer.FLD_TASK_COMPLETE_CONFIRM_BY));
				
				doer.cancel_req_by = cursor.getLong(cursor.getColumnIndex(TaskDoer.FLD_CANCEL_REQ_BY));
				doer.cancel_req_date = cursor.getString(cursor.getColumnIndex(TaskDoer.FLD_CANCEL_REQ_DATE));
				doer.cancel_reason_by_poster = cursor.getString(cursor.getColumnIndex(TaskDoer.FLD_CANCEL_REASON_BY_POSTER));
				doer.cancel_reason_by_doer = cursor.getString(cursor.getColumnIndex(TaskDoer.FLD_CANCEL_REASON_BY_DOER));
				doer.cancel_refund_demand_by_poster = cursor.getLong(cursor.getColumnIndex(TaskDoer.FLD_CANCEL_REFUND_DEMAND_BY_POSTER));
				doer.cancel_refund_offer_by_doer = cursor.getLong(cursor.getColumnIndex(TaskDoer.FLD_CANCEL_REFUND_OFFER_BY_DOER));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return doer;
	}

	public NetworkResponse syncCategorySkillAndQuestion(String categoryId) {
		NetworkResponse response = null;
		try {
			Long skillTrno = getMaxTrno(TableType.CategorySkillTable,
										CategorySkill.FLD_CATEGORY_ID + "=?", new String[] { categoryId });

			Long quesTrno = getMaxTrno(TableType.CategoryQuestionTable,
										Question.FLD_CATEGORY_ID + "=?", new String[] { categoryId });

			response = NetworkMgr.getCategorySkillAndQuestion(skillTrno,quesTrno, categoryId);
			if (response != null) {
				if (response.isSuccess()) {
					JSONObject data = response.getJsonObject().getJSONObject(Constants.FLD_DATA);

					JSONArray skillArray = data.getJSONArray(Skill.TABLE_NAME);
					JSONArray QuesArray = data.getJSONArray(Question.FLD_QUESTION);
					ContentValues[] contentValues = null;
					if (skillArray.length() > 0) {
						contentValues = new ContentValues[skillArray.length()];
						for (int i = 0; i < skillArray.length(); i++) {
							ContentValues values = new ContentValues();
							JSONObject object = skillArray.getJSONObject(i);
							values.put(CategorySkill.FLD_CATEGORY_ID,categoryId);
							values.put(CategorySkill.FLD_SKILL_ID, object.getString(CategorySkill.FLD_SKILL_ID));
							values.put(CategorySkill.FLD_TRNO,object.getString(CategorySkill.FLD_TRNO));
							contentValues[i] = values;
						}
						insertInDB(TableType.CategorySkillTable, contentValues);
					}

					if (QuesArray.length() > 0) {
						contentValues = new ContentValues[QuesArray.length()];
						for (int i = 0; i < QuesArray.length(); i++) {
							ContentValues values = new ContentValues();
							JSONObject object = QuesArray.getJSONObject(i);
							values.put(Question.FLD_CATEGORY_ID,categoryId);
							values.put(Question.FLD_QUESTION_ID,object.getString(Question.FLD_QUESTION_ID));
							values.put(Question.FLD_QUESTION_DESC,object.getString(Question.FLD_QUESTION_DESC));
							values.put(Question.FLD_TRNO,object.getString(Question.FLD_TRNO));
							contentValues[i] = values;
						}
						insertInDB(TableType.CategoryQuestionTable,contentValues);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public void getCategorySkill(String categoryId) {
		AppGlobals.categorySkills.clear();
		Cursor cursor = null;
		try {
			cursor = rawQuery(
					"SELECT st." + Skill.FLD_SKILL_ID + " , st."
							+ Skill.FLD_SKILL_DESC + " FROM "
							+ TableType.SkillTable.getTableName() + " as st , "
							+ TableType.CategorySkillTable.getTableName()
							+ " as cst where st." + Skill.FLD_SKILL_ID
							+ " = cst." + CategorySkill.FLD_SKILL_ID
							+ " and cst." + CategorySkill.FLD_CATEGORY_ID
							+ " = " + categoryId + " and st."
							+ Skill.FLD_SKILL_STATUS + " = 1", null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				while (!cursor.isAfterLast()) {
					Skill skill = new Skill();
					skill.skill_id = cursor.getString(cursor.getColumnIndex(Skill.FLD_SKILL_ID));
					skill.skill_desc = cursor.getString(cursor.getColumnIndex(Skill.FLD_SKILL_DESC));
					AppGlobals.categorySkills.add(skill);
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}

	public void getCategoryQuestion(String categoryId) {
		AppGlobals.categoryQuestions.clear();
		Cursor cursor = null;
		try {

			cursor = queryTable(TableType.CategoryQuestionTable, null,
					Question.FLD_CATEGORY_ID + "=?",
					new String[] { categoryId }, null, null, null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				while (!cursor.isAfterLast()) {
					Question question = new Question();
					question.category_id = cursor.getString(cursor.getColumnIndex(Question.FLD_CATEGORY_ID));
					question.question_id = cursor.getLong(cursor.getColumnIndex(Question.FLD_QUESTION_ID));
					question.question_desc = cursor.getString(cursor.getColumnIndex(Question.FLD_QUESTION_DESC));
					AppGlobals.categoryQuestions.add(question);
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}
	
	
	public ArrayList<Attachment> getAttachment(Long rec_id) {
		ArrayList<Attachment> task_attachment = new ArrayList<Attachment>();
		Cursor cursor = null;
		try {

			cursor = queryTable(TableType.TaskAttachmentTable, null,
					Question.FLD_MOBILE_REC_ID + "=?",
					new String[] { String.valueOf(rec_id) }, null, null, null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				while (!cursor.isAfterLast()) {
					Attachment attachment = new Attachment();
					attachment.file_url = cursor.getString(cursor.getColumnIndex(Attachment.FLD_FILE_URL));
					attachment.file_name = cursor.getString(cursor.getColumnIndex(Attachment.FLD_FILE_NAME));
					task_attachment.add(attachment);
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return task_attachment;
	}
	
	
	
	public void saveWorkLocationList(ArrayList<WorkLocation> wLocations){
		if (wLocations.size() > 0) {
			ContentValues[] contentValues = new ContentValues[wLocations.size()];
			int index = 0;
			for (WorkLocation location: wLocations) {
				ContentValues values = new ContentValues();
				values.put(WorkLocation.FLD_WORK_LOCATION_ID,location.work_location_id);
				values.put(WorkLocation.FLD_COUNTRY_CODE,location.country.country_code);
				values.put(WorkLocation.FLD_COUNTRY_NAME,location.country.country_name);
				values.put(WorkLocation.FLD_STATE_ID,location.state.state_id);
				values.put(WorkLocation.FLD_STATE_NAME,location.state.state_name);
				values.put(WorkLocation.FLD_REGION_ID,location.region.region_id);
				values.put(WorkLocation.FLD_REGION_NAME,location.region.region_name);
				values.put(WorkLocation.FLD_CITY_ID,location.city.city_id);
				values.put(WorkLocation.FLD_CITY_NAME,location.city.city_name);
				values.put(WorkLocation.FLD_ADDRESS, location.address);
				values.put(WorkLocation.FLD_ZIP_CODE, location.zipcode);
				values.put(WorkLocation.FLD_IS_DEFAULT_LOCATION,location.is_default_location);
				values.put(WorkLocation.FLD_LOCATION_NAME,location.location_name);
				values.put(WorkLocation.FLD_LATITUDE, location.latitude);
				values.put(WorkLocation.FLD_LONGITUDE,location.longitude);
				values.put(WorkLocation.FLD_STATUS, location.status);
				values.put(WorkLocation.FLD_IS_BILLING, location.is_billing);
				values.put(WorkLocation.FLD_IS_SHIPPING, location.is_shipping);
				contentValues[index] = values;
				index ++;
			}
			insertInDB(TableType.WorkLocationTable,contentValues);
		}
	}
	
	public void getWorkLocationList(){
		AppGlobals.workLocations.clear();
		Cursor cursor = null;
		try {

			cursor = queryTable(TableType.WorkLocationTable, null,
								WorkLocation.FLD_STATUS + "=?",
								new String[] {"a"}, null, null, null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				while (!cursor.isAfterLast()) {
					WorkLocation location = new WorkLocation();
					location.country = new Country();
					location.state = new State();
					location.region = new Region();
					location.city = new City();
					location.work_location_id = cursor.getString(cursor.getColumnIndex(WorkLocation.FLD_WORK_LOCATION_ID));
					location.country.country_code = cursor.getString(cursor.getColumnIndex(WorkLocation.FLD_COUNTRY_CODE));
					location.country.country_name = cursor.getString(cursor.getColumnIndex(WorkLocation.FLD_COUNTRY_NAME));
					location.state.state_id = cursor.getString(cursor.getColumnIndex(WorkLocation.FLD_STATE_ID));
					location.state.state_name = cursor.getString(cursor.getColumnIndex(WorkLocation.FLD_STATE_NAME));
					location.region.region_id = cursor.getString(cursor.getColumnIndex(WorkLocation.FLD_REGION_ID));
					location.region.region_name = cursor.getString(cursor.getColumnIndex(WorkLocation.FLD_REGION_NAME));
					location.city.city_id = cursor.getString(cursor.getColumnIndex(WorkLocation.FLD_CITY_ID));
					location.city.city_name = cursor.getString(cursor.getColumnIndex(WorkLocation.FLD_CITY_NAME));
					location.address = cursor.getString(cursor.getColumnIndex(WorkLocation.FLD_ADDRESS));// need to handle
					location.location_geo_area = cursor.getString(cursor.getColumnIndex(WorkLocation.FLD_ADDRESS));
					location.zipcode = cursor.getString(cursor.getColumnIndex(WorkLocation.FLD_ZIP_CODE));
					location.location_name = cursor.getString(cursor.getColumnIndex(WorkLocation.FLD_LOCATION_NAME));
					location.is_default_location = cursor.getString(cursor.getColumnIndex(WorkLocation.FLD_IS_DEFAULT_LOCATION));
					location.latitude = cursor.getString(cursor.getColumnIndex(WorkLocation.FLD_LATITUDE));
					location.longitude = cursor.getString(cursor.getColumnIndex(WorkLocation.FLD_LONGITUDE));
					location.is_billing=cursor.getString(cursor.getColumnIndex(WorkLocation.FLD_IS_BILLING));
					location.is_shipping=cursor.getString(cursor.getColumnIndex(WorkLocation.FLD_IS_SHIPPING));
					if(location.is_default_location.equals("1")){
						AppGlobals.defaultWLocation = location;
					}
					AppGlobals.workLocations.add(location);
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}
	
	public void getCountryListData(){
		AppGlobals.countries.clear();
		Cursor cursor = null;
		try {
			cursor = queryTable(TableType.CountryTable, new String[] {
					Country.FLD_COUNTRY_CODE, Country.FLD_COUNTRY_NAME, },
					Country.FLD_COUNTRY_STATUS + "=?", new String[] { "1" },
					null, null, Country.FLD_COUNTRY_NAME);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				while (!cursor.isAfterLast()) {
					Country country = new Country();
					country.country_code = cursor.getString(cursor.getColumnIndex(Country.FLD_COUNTRY_CODE));
					country.country_name = cursor.getString(cursor.getColumnIndex(Country.FLD_COUNTRY_NAME));
					AppGlobals.countries.add(country);
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}
	
	
	public ArrayList<MessageDetail> getMessagesThreadList(MessageDetail msg){
		ArrayList<MessageDetail> messageThreadList = new ArrayList<MessageDetail>(0);
		Cursor cursor = null;
		try {
			cursor = sqLiteDb.rawQuery("SELECT t.*  FROM inbox_user as t  WHERE  ((" 
					+" delivery_status = '" + Constants.DELIVERY_STATUS_SENT 
					+"' and task_id = '" + msg.task_id 
					+"' and from_user_id = '"+ msg.from_user_id 
					+"' and to_user_ids = '" + msg.to_user_ids+"') " +
					"OR " +	"(" 
					+" delivery_status = '" + Constants.DELIVERY_STATUS_SENT 
					+"' and task_id = '"+ msg.task_id +"' and from_user_id = '"
					+ msg.to_user_ids +"' and to_user_ids = '"+ msg.from_user_id +"')) order by msg_id desc",null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				while (!cursor.isAfterLast()) {
					MessageDetail message = new MessageDetail();
					message.recId = cursor.getLong(cursor.getColumnIndex(MessageDetail.ID));
					message.msg_id = cursor.getLong(cursor.getColumnIndex(MessageDetail.FLD_MESSAGE_ID));
					message.task_id = cursor.getLong(cursor.getColumnIndex(MessageDetail.FLD_TASK_ID));
					message.msg_from_name = cursor.getString(cursor.getColumnIndex(MessageDetail.FLD_MESSAGE_FROM_NAME));
					message.body = cursor.getString(cursor.getColumnIndex(MessageDetail.FLD_BODY));
					message.created_at = cursor.getString(cursor.getColumnIndex(MessageDetail.FLD_CREATED_AT));
					message.title = cursor.getString(cursor.getColumnIndex(MessageDetail.FLD_TASK_TITLE));
					message.is_read = cursor.getString(cursor.getColumnIndex(MessageDetail.FLD_IS_READ));
					message.is_delete = cursor.getString(cursor.getColumnIndex(MessageDetail.FLD_IS_DELETE));
					messageThreadList.add(message);
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return messageThreadList;
	}
	
	public NetworkResponse createMessage(MessageDetail message) {
		NetworkResponse response = null;
		if (sqLiteDb != null) {
			try {
				sqLiteDb.insert(TableType.InboxUserTable.getTableName(), null, getMessageCValue(message));
				response = NetworkMgr.postPendingMessages(getMessageList(Constants.DELIVERY_STATUS_PENDING, null));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}
	
	public ContentValues getMessageCValue(MessageDetail message){
		ContentValues values = new ContentValues();
		if (message.recId != null){
			values.put(MessageDetail.ID, message.recId);
		}
		values.put(MessageDetail.FLD_MESSAGE_ID, message.msg_id);
		values.put(MessageDetail.FLD_MESSAGE_TYPE, message.msg_type);
		values.put(MessageDetail.FLD_MESSAGE_FLOW, message.msg_flow);
		values.put(MessageDetail.FLD_THREAD_KEY, message.thread_key);
		values.put(MessageDetail.FLD_TASK_ID, message.task_id);
		values.put(MessageDetail.FLD_TASK_TITLE, message.title);
		values.put(MessageDetail.FLD_FROM_USER_ID, message.from_user_id);
		values.put(MessageDetail.FLD_MESSAGE_FROM_NAME, message.msg_from_name);
		values.put(MessageDetail.FLD_MESSAGE_TO_NAME, message.msg_to_name);
		values.put(MessageDetail.FLD_TO_USER_ID, message.to_user_ids);
		values.put(MessageDetail.FLD_SUBJECT, message.subject);
		values.put(MessageDetail.FLD_BODY, message.body);
		values.put(MessageDetail.FLD_ATTACHMENT, message.attachments);
		values.put(MessageDetail.FLD_IS_PUBLIC, message.is_public);
		values.put(MessageDetail.FLD_IS_READ, message.is_read);
		values.put(MessageDetail.FLD_IS_DELETE, message.is_delete); 
		values.put(MessageDetail.FLD_CREATED_AT, message.created_at);
		values.put(MessageDetail.FLD_CREATED_BY, message.created_by);
		values.put(MessageDetail.FLD_UPDATED_AT, message.updated_at);
		values.put(MessageDetail.FLD_UPDATED_BY, message.updated_by);
		values.put(MessageDetail.FLD_SOURCE_APP, message.source_app);
		values.put(MessageDetail.FLD_STATUS, message.status);
		values.put(MessageDetail.FLD_TRNO, message.trno);
		values.put(MessageDetail.FLD_DELIVERRY_STATUS, message.delivery_status);
		return values;
	}
	
	public ArrayList<MessageDetail> getMessageList(String deliveryStatus,String orderBy) {
		ArrayList<MessageDetail> messages = new ArrayList<MessageDetail>(0);
		Cursor cursor = null;
		try {
			if (deliveryStatus.equals(Constants.DELIVERY_STATUS_PENDING)) {
				cursor = queryTable(TableType.InboxUserTable, null,
						MessageDetail.FLD_DELIVERRY_STATUS + "=?",
						new String[] { Constants.DELIVERY_STATUS_PENDING },
						null, null, MessageDetail.ID + " DESC");
			} else {
				if(orderBy == null){
					orderBy = "created_at desc";
				}
				cursor = sqLiteDb
						.rawQuery(
								"select i._id,i.created_at,i.msg_id, i.title, i.msg_flow, i.task_id,i.from_user_id,i.msg_from_name,i.msg_to_name,i.to_user_ids,i.body,i.thread_key, i.msg_flow "
										+ "from inbox_user i, (select distinct task_id, thread_key from inbox_user "
										+ "where from_user_id="
										+ AppGlobals.userDetail.user_id
										+ " and status='a' and task_id > 0 "
										+ "group by task_id, thread_key) threads "
										+ "where i.to_user_ids != '' and i.task_id=threads.task_id and i.thread_key=threads.thread_key and "
										+ "(i.task_id || i.thread_key || i.created_at) in (select i2.task_id || i2.thread_key || max(i2.created_at)  "
										+ "from inbox_user i2 where task_id  > 0 group by i2.task_id, i2.thread_key) order by i."+orderBy,
								null);
			}
			
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				int msg_type_idx = 0;
				int is_public_idx = 0;
				int trno_idx = 0;
				
				int recId_idx = cursor.getColumnIndex(MessageDetail.ID);
				int task_id_idx = cursor.getColumnIndex(MessageDetail.FLD_TASK_ID);
				int to_user_ids_idx = cursor.getColumnIndex(MessageDetail.FLD_TO_USER_ID);
				int body_idx = cursor.getColumnIndex(MessageDetail.FLD_BODY);
				int msg_flow_idx = cursor.getColumnIndex(MessageDetail.FLD_MESSAGE_FLOW);
				int msg_id_idx = cursor.getColumnIndex(MessageDetail.FLD_MESSAGE_ID);
				int title_idx = cursor.getColumnIndex(MessageDetail.FLD_TASK_TITLE);
				int msg_from_name_idx = cursor.getColumnIndex(MessageDetail.FLD_MESSAGE_FROM_NAME);
				int msg_to_name_idx = cursor.getColumnIndex(MessageDetail.FLD_MESSAGE_TO_NAME);
				int created_at_idx = cursor.getColumnIndex(MessageDetail.FLD_CREATED_AT);
				int from_user_id_idx = cursor.getColumnIndex(MessageDetail.FLD_FROM_USER_ID);
				
				if(deliveryStatus.equals(Constants.DELIVERY_STATUS_PENDING)){
					msg_type_idx = cursor.getColumnIndex(MessageDetail.FLD_MESSAGE_TYPE);
					is_public_idx = cursor.getColumnIndex(MessageDetail.FLD_IS_PUBLIC);
					trno_idx = cursor.getColumnIndex(MessageDetail.FLD_TRNO);
				}
				while (!cursor.isAfterLast()) {
					MessageDetail message = new MessageDetail();
					if(deliveryStatus.equals(Constants.DELIVERY_STATUS_PENDING)){ 
						message.msg_type = cursor.getString(msg_type_idx);
						message.is_public = cursor.getString(is_public_idx);
						message.trno = cursor.getLong(trno_idx);
					}
					message.recId = cursor.getLong(recId_idx);
					message.task_id = cursor.getLong(task_id_idx);
					message.to_user_ids = cursor.getLong(to_user_ids_idx);
					message.body = cursor.getString(body_idx);
					message.msg_flow = cursor.getString(msg_flow_idx);
					message.msg_id = cursor.getLong(msg_id_idx);
					message.title = cursor.getString(title_idx);
					message.msg_from_name = cursor.getString(msg_from_name_idx);
					message.msg_to_name = cursor.getString(msg_to_name_idx);
					message.created_at = cursor.getString(created_at_idx);
					message.from_user_id = cursor.getLong(from_user_id_idx);
					messages.add(message);
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return messages;
	}
	
	public Long updateMessageData(NetworkResponse response, MessageDetail message,Long mTrno) {
		Long maxTrno = null;
		try {
			maxTrno = message != null ? message.trno : mTrno;
			ContentValues[] contentValues = null;
			if (response.isSuccess()) {
				MessageData messageData = (MessageData)Util.getJsonToClassObject(response.getJsonObject().toString(), MessageData.class);
				if(messageData.data.size() >0){
					
					int index = 0;
					int aMsgCount = messageData.active_msg_count.equals(null) ? 0 : Integer.parseInt(messageData.active_msg_count);
					
					if(aMsgCount > 0){
						contentValues = new ContentValues[Integer.parseInt(messageData.active_msg_count)]; 
					}
					
					for(MessageDetail msg : messageData.data){
						if (maxTrno < msg.trno) {
							maxTrno = msg.trno;
						}
						msg.trno = maxTrno;
						if(msg.is_delete.equals("0")){ 
							Log.v("msg_id=", String.valueOf(msg.msg_id));
							msg.recId = Util.getMobileRecId(msg.mobile_rec_id);
							msg.delivery_status = Constants.DELIVERY_STATUS_SENT;
							contentValues[index] = getMessageCValue(msg);
							index ++;
						}else{
							//updateMessageTrno(maxTrno); 
							deleteTableRow(TableType.InboxUserTable, MessageDetail.FLD_MESSAGE_ID + " =?", new String[]{String.valueOf(msg.msg_id)});
						}
					}
				
					if(aMsgCount > 0){
						insertInDB(TableType.InboxUserTable, contentValues);
					}
				}else{
					return null;
				}
			} else if(message != null){
				contentValues = new ContentValues[1];
				message.delivery_status = Constants.DELIVERY_STATUS_ERROR;
				contentValues[0] = getMessageCValue(message);
				insertInDB(TableType.InboxUserTable, contentValues);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return maxTrno;
	}

	public void updateMessageTrno(Long trno) {
		ContentValues cValues = new ContentValues();
		cValues.put(MessageDetail.FLD_TRNO, trno);
		sqLiteDb.update(TableType.InboxUserTable.getTableName(),cValues, null,null);
	}
	
	
	public ArrayList<Question> getQuestion(Long rec_id) {
		ArrayList<Question> task_question = new ArrayList<Question>();
		Cursor cursor = null;
		try {

			cursor = queryTable(TableType.TaskQuestionTable, null,
					Question.FLD_MOBILE_REC_ID + "=?",
					new String[] { String.valueOf(rec_id) }, null, null, null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				while (!cursor.isAfterLast()) {
					Question question = new Question();
					question.question_id = cursor.getLong(cursor.getColumnIndex(Question.FLD_QUESTION_ID));
					question.question_desc = cursor.getString(cursor.getColumnIndex(Question.FLD_QUESTION_DESC));
					question.task_question_id = cursor.getLong(cursor.getColumnIndex(Question.FLD_TASK_QUESTION_ID));
					task_question.add(question);
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return task_question;
	}

	public ArrayList<Skill> getSkillSListFromDB(ArrayList<Skill> addSkillListFromDB) {
		Cursor cursor = null;
		try {
			cursor = rawQuery(
					"SELECT * FROM " + TableType.SkillTable.getTableName(),
					null);
			
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					Skill skill = new Skill();
					skill.skill_id = cursor.getString(cursor
							.getColumnIndex(Skill.FLD_SKILL_ID));
					skill.skill_desc = cursor.getString(cursor
							.getColumnIndex(Skill.FLD_SKILL_DESC));
					skill.isSelected = false;
					addSkillListFromDB.add(skill);
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return addSkillListFromDB;
	}

	public Project getProjectDetail(Long task_id) {
		Project project = new Project();
		Cursor cursor = null;
		try {
			cursor = queryTable(TableType.TaskTable, null,Project.FLD_TASK_ID + "=?", new String[] {String.valueOf(task_id)}, null,null,null);
			
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				project = getProjectData(Constants.DELIVERY_STATUS_SENT,cursor);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return project;
	}
}
