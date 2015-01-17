package erandoo.app.utilities;

import erandoo.app.R;
import erandoo.app.cmds.Cmd;
import erandoo.app.cmds.CmdFactory;
import erandoo.app.config.Config;
import erandoo.app.config.DeviceInfo;
import erandoo.app.database.Category;
import erandoo.app.database.Country;
import erandoo.app.database.DatabaseMgr;
import erandoo.app.database.MessageDetail;
import erandoo.app.database.Project;
import erandoo.app.database.Skill;
import erandoo.app.database.TableType;
import erandoo.app.main.MyProjectActivity;
import erandoo.app.mqtt.ErandooMqttMessage;
import erandoo.app.network.NetworkMgr;
import erandoo.app.network.NetworkResponse;
import erandoo.app.tempclasses.MasterSyncData;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class SyncAppData extends AsyncTask<Void, Void, NetworkResponse>{
	public static final String SYNC_PROJECT = "sync_project";
	public static final String SYNC_INBOX = "sync_inbox";
	public static final String SYNC_MASTER = "sync_master";
	
	private Context context;
	private String syncType;
	public static SyncAppData getInstance(Context ctx,String syncType){
		Config.init(ctx); 
		Config.setUserDetails(false); 
		SyncAppData syncAppData = new SyncAppData();
		syncAppData.context = ctx;
		syncAppData.syncType = syncType;
		return syncAppData;
	}
	
	@Override
	protected NetworkResponse doInBackground(Void... params) {
		NetworkResponse response = null;
		try {
			if(Util.isDeviceOnline(context)){ 
				if(syncType.equals(SYNC_MASTER)){
					response = syncMasterData(context);			
				}else if(syncType.equals(SYNC_PROJECT)){
					//response = syncProjectData(context);			
				}else if(syncType.equals(SYNC_INBOX)){
					response = syncInboxData(context);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	
	@Override
	protected void onPostExecute(NetworkResponse response) {
		super.onPostExecute(response);
		if (response != null) {
			if(response.isSuccess()){
				if (response.isDataUpdated) {
					ErandooMqttMessage eMqttMessage = new ErandooMqttMessage();
					eMqttMessage.event_type = Constants.MQTT_EVENT_TYPE_PUSH;
					if(syncType.equals(SYNC_MASTER)){ 
						eMqttMessage.msg = context.getResources().getString(R.string.msg_Application_data_Updated);
						Util.sendNotification(eMqttMessage);
					}else if(syncType.equals(SYNC_PROJECT)){
						eMqttMessage.msg = context.getResources().getString(R.string.msg_Project_data_Updated);
						Util.sendNotification(eMqttMessage);
						if(MyProjectActivity.instance != null){
							MyProjectActivity.instance.handler.sendEmptyMessage(0);
						}
					}else if(syncType.equals(SYNC_INBOX)){
						eMqttMessage.msg = context.getResources().getString(R.string.msg_Message_data_Updated);
						Util.sendNotification(eMqttMessage);
					}
				}
			}
		}
	}
	
	public static NetworkResponse syncInboxData(Context context){
		DatabaseMgr database = DatabaseMgr.getInstance(context);
		Long maxTrno = Long.valueOf(database.getMaxTrno(TableType.InboxUserTable, null, null));
		Cmd cmd = CmdFactory.createGetMessageCmd();
		cmd.addData(MessageDetail.FLD_TRNO, maxTrno);
		NetworkResponse response = NetworkMgr.httpPost(Config.API_URL, cmd.getJsonData());
		if (response != null) {
			if(response.isSuccess()){
				response.isDataUpdated = database.updateMessageData(response, null,maxTrno) != null ? true : false;
			}
		}
		return response;
	}
	
	public static NetworkResponse syncProjectData(Context context){
		DatabaseMgr database = DatabaseMgr.getInstance(context);
		Long maxTrno = Long.valueOf(database.getMaxTrno(TableType.TaskTable, null, null));
		Cmd cmd = CmdFactory.getMyProjectListCmd();
		cmd.addData(Project.FLD_TRNO, maxTrno);
		NetworkResponse response = NetworkMgr.httpPost(Config.API_URL,cmd.getJsonData());
		if (response != null) {
			if (response.isSuccess()) {
				response.isDataUpdated = database.updateTaskData(response, null, maxTrno) != null ? true : false;
			}
		}
		return response;
	}
	
	public static NetworkResponse syncMasterData(Context context){
		Cmd cmd = CmdFactory.createSyncMasterDataCmd();
		String regisId = getGCMRegistrationId(context);
		cmd.addData(DeviceInfo.FLD_CLOUD_KEY_TO_NOTIFY, regisId);
		NetworkResponse response = NetworkMgr.httpPost(Config.API_URL,cmd.getJsonData());
		//Log.v("SYNC", response.getJsonObject().toString());
		if (response != null) {
			if (response.isSuccess()) {
				response.isDataUpdated = syncWithMasterData(response,context);
			}
		} 
		return response;
	}
	
	private static boolean syncWithMasterData(NetworkResponse response, Context context){
		DatabaseMgr database = DatabaseMgr.getInstance(context);
		boolean isDataUpdated = false;
		try {
			
			MasterSyncData syncData = (MasterSyncData)Util.getJsonToClassObject(response.getJsonObject().toString(),MasterSyncData.class);
			ContentValues[] contentValues;
			if(syncData.data != null){
				int index = 0;
				if(syncData.data.category.size() > 0){
					Long oldTrno = Config.getTrno(Constants.TRNO_CATEGORY);
					contentValues = new ContentValues[syncData.data.category.size()];
					for (Category category : syncData.data.category) {
						Long trno = category.trno;
						if (oldTrno < trno) {
							oldTrno = trno;
						}
						ContentValues values = new ContentValues();
						values.put(Category.FLD_CATEGORY_ID,category.category_id);
						values.put(Category.FLD_CATEGORY_NAME,category.category_name);
						values.put(Category.FLD_CATEGORY_IMAGE,category.category_image);
						values.put(Category.FLD_IS_VIRTUAL,category.is_virtual);
						values.put(Category.FLD_IS_INPERSON,category.is_inperson);
						values.put(Category.FLD_IS_INSTANT,category.is_instant);
						values.put(Category.FLD_PARENT_ID,category.parent_id);
						values.put(Category.FLD_SUB_CATEGORY_CNT,category.subcategory_cnt);
						values.put(Category.FLD_TASK_TEMPLATE,category.task_templates);
						values.put(Category.FLD_CATEGORY_STATUS,category.category_status);
						contentValues[index] = values;
						index ++;
					}
					Config.setTrno(Constants.TRNO_CATEGORY, oldTrno);
					Config.savePreferencese();
					database.insertInDB(TableType.CategoryTable, contentValues);
					isDataUpdated = true;
				}
				
				if (syncData.data.country.size() > 0) {
					index = 0;
					Long oldTrno = Config.getTrno(Constants.TRNO_COUNTRY);
					contentValues = new ContentValues[syncData.data.country.size()];
					for (Country country : syncData.data.country) {
						Long trno = country.trno;
						if (oldTrno < trno) {
							oldTrno = trno;
						}
						ContentValues values = new ContentValues();
						values.put(Country.FLD_COUNTRY_CODE,country.country_code);
						values.put(Country.FLD_COUNTRY_NAME,country.country_name);
						values.put(Country.FLD_COUNTRY_STATUS,country.country_status);
						contentValues[index] = values;
						index ++;
					}
					Config.setTrno(Constants.TRNO_COUNTRY, oldTrno);
					Config.savePreferencese();
					database.insertInDB(TableType.CountryTable, contentValues);
					isDataUpdated = true;
				}
				
				if (syncData.data.skill.size() > 0) {
					index = 0;
					Long oldTrno = Config.getTrno(Constants.TRNO_SKILL);
					contentValues = new ContentValues[syncData.data.skill.size()];
					for (Skill skill : syncData.data.skill) {
						Long trno = skill.trno;
						if (oldTrno < trno) {
							oldTrno = trno;
						}
						ContentValues values = new ContentValues();
						values.put(Skill.FLD_SKILL_ID, skill.skill_id);
						values.put(Skill.FLD_SKILL_DESC,skill.skill_desc);
						values.put(Skill.FLD_SKILL_STATUS, skill.status);
						contentValues[index] = values;
						index ++;
					}
					Config.setTrno(Constants.TRNO_SKILL, oldTrno);
					Config.savePreferencese();
					database.insertInDB(TableType.SkillTable, contentValues);
					isDataUpdated = true;
				}
				
				if(syncData.data.work_location.size() > 0){
					database.saveWorkLocationList(syncData.data.work_location); 
					isDataUpdated = true;
				}
				
				if(syncData.data.user_detail != null){
					Config.setUserName(syncData.data.user_detail.fullname);
					Config.setUserImage(syncData.data.user_detail.userimage);
					Config.savePreferencese();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isDataUpdated;
	}

	public static String getGCMRegistrationId(Context context) {
		String registrationId = Config.getGcmRegisteredID();

		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		if (registrationId.isEmpty() || isAppVersionChanged(context)) {
			Log.i("GCM ID->", "GCM Registration not found.");
			return "";
		}
		
		return registrationId;
	}
	
	
	public static boolean isAppVersionChanged(Context context) {
		int registeredVersion = Config.getAppVersion();
		int currentVersion = Util.getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Config.setAppVersion(Integer.valueOf(currentVersion));
			Config.savePreferencese();
			Log.i("APP VERSION->", "App version changed.");
			return true;
		}
		return false;
	}
	
	

}
