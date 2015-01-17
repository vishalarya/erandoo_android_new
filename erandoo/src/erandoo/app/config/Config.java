package erandoo.app.config;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import erandoo.app.database.UserDetail;
import erandoo.app.main.AppGlobals;
import erandoo.app.utilities.Constants;
import erandoo.app.utilities.Util;

public class Config {

	public static final String APP_TOKEN = "AIzaSyADYpDFuGdDJxndBiquw8PsvU4NoWdv4xQ"; // From google api account ageless-impulse-617 ,543129283963
	public static final String APP_TOKEN_IN_NUMBERS = "543129283963";
	public static final String GPLACE_API_TOKEN = "AIzaSyCbYuP5mkPeW5RgQGkDshCspbsgS4Fezhk"; //browser key to use google place api
	public static final String APP_VERSION = "vd00-01-023";
	public static final String SOURCE_APP = "android";
	public static final String RECORDS_PER_PAGE = "10";
	public static final String PATH_DOWNLOADED_FILES = Environment.getExternalStorageDirectory().getAbsolutePath();

//	public static final String API_URL = "http://182.237.17.106:8080/greencometdev/api/index/index";
//	public static final String API_URL = "http://aryamobi.com/api/index/index";
    public static final String API_URL = "http://54.84.193.236/api/index/index";// Production url for api
	
	public static final String APP_FOLDER_NAME = "gc";
	public static final String LOG_FILE_NAME = "gc.log";
	public static final boolean DEBUG_BUILD = false;

	private static String CONFIG_FILE = "gc.db";
	private static SharedPreferences settings = null;
	private static SharedPreferences.Editor editor;
	private static Context context;
	private static String deviceType;
	private static String screenType;

	// SharedPreferences file keys
	public static final String USER_TOKEN = "user_token";
	public static final String USER_ID = "user_id";
	public static final String LANG_CODE = "lang_code";
	public static final String USER_DEVICE_ID = "user_device_id";
	public static final String USER_NAME = "user_name";
	public static final String USER_EMAIL_ID = "user_email_id";
	public static final String USER_IMAGE = "user_image";
	public static final String LAST_APP_VERSION = "l_app_ver";
	public static final String GCM_REGISTERED_ID = "gcm_reg_id";

	public static Integer width = null;
	public static Integer height = null;
	public static DeviceInfo deviceInfo;

	public static void init(Context context) {
		Config.settings = context.getSharedPreferences(CONFIG_FILE, 0);
		Config.editor = settings.edit();
		Config.context = context;
		try{
			deviceInfo = getDeviceInfo();
		}catch(Exception e){
			//e.printStackTrace();
		}  
		
	}

	public static void clearPreferences() {
		editor.clear();
		editor.commit();
	}

	public static void removePerferance(String key) {

		editor.remove(key);
		editor.commit();
	}

	public static void savePreferencese() {
		editor.commit();
	}

	public static Context getContext() {
		return context;
	}

	public static void setContext(Context context) {
		Config.context = context;
	}

	public static Long getUserId() {
		return settings.getLong(USER_ID, Constants.INVALID_ID);
	}

	public static void setUserId(Long userId) {
		editor.putLong(USER_ID, userId);
	}

	public static String getLangCode() {
		return settings.getString(LANG_CODE, "en_us");
	}

	public static void setLangCode(String langCode) {
		editor.putString(LANG_CODE, langCode);
	}

	public static String getGcmRegisteredID() {
		return settings.getString(GCM_REGISTERED_ID, "");
	}

	public static void setGcmRegisteredID(String gcmId) {
		editor.putString(GCM_REGISTERED_ID, gcmId);
	}
	
	public static Integer getAppVersion() {
		return settings.getInt(LAST_APP_VERSION,Integer.MIN_VALUE);
	}

	public static void setAppVersion(Integer appVersion) {
		editor.putInt(LAST_APP_VERSION, appVersion);
	}

	public static void setTrno(String key, Long trno) {
		editor.putLong(key, trno);
	}

	public static Long getTrno(String key) {
		return settings.getLong(key, 0);
	}

	public static String getUserToken() {
		return settings.getString(USER_TOKEN, null);
	}

	public static void setUserToken(String userToken) {
		editor.putString(USER_TOKEN, userToken);
	}
	
	public static String getUserName() {
		return settings.getString(USER_NAME, null);
	}

	public static void setUserName(String userName) {
		editor.putString(USER_NAME, userName);
	}
	
	public static String getUserEmailId() {
		return settings.getString(USER_EMAIL_ID, null);
	}

	public static void setUserEmailId(String userEmailId) {
		editor.putString(USER_EMAIL_ID, userEmailId);
	}
	
	public static Long getUserDeviceId() {
		return settings.getLong(USER_DEVICE_ID, Constants.INVALID_ID);
	}

	public static void setUserDeviceId(Long usrDeviceId) {
		editor.putLong(USER_DEVICE_ID, usrDeviceId);
	}
	
	
	public static void setUserImage(String userImage) {
		editor.putString(USER_IMAGE, userImage);
	}
	
	public static String getUserImage() {
		return settings.getString(USER_IMAGE, null);
	}

	// -----------------DEVICE INFORMATION---------------------------

	public static void setUserDetails(boolean isLogout){
		if(isLogout){
			setUserToken(null);
			setUserId(Constants.INVALID_ID);
			setUserDeviceId(Constants.INVALID_ID);
			setUserName(null); 
			setUserImage(null); 
			setGcmRegisteredID("");  
			AppGlobals.userDetail = new UserDetail();
			Config.savePreferencese(); 
		}else{
			AppGlobals.userDetail.user_device_id = getUserDeviceId();
			AppGlobals.userDetail.user_id =  getUserId();
			AppGlobals.userDetail.user_token = getUserToken();
			AppGlobals.userDetail.fullname = getUserName();
			AppGlobals.userDetail.lang_code = getLangCode();
			AppGlobals.userDetail.email = getUserEmailId();
			AppGlobals.userDetail.userimage = getUserImage();
		}
	}
	
	public static DeviceInfo getDeviceInfo() {
		getScreenResolution((Activity) context);
		DeviceInfo deviceInfo = new DeviceInfo();
		TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		deviceInfo.setDeviceId(Util.getDeviceID(context));
		deviceInfo.setSimOperator(tMgr.getSimOperator());
		deviceInfo.setSimCountryIso(tMgr.getSimCountryIso());
		deviceInfo.setSimSerialNumber(tMgr.getSimSerialNumber());
		deviceInfo.setSimOperatorName(tMgr.getSimOperatorName());
		deviceInfo.setSimNetworkOperator(tMgr.getNetworkOperator());
		deviceInfo.setDeviceMacAddress(Util.getDeviceMacAddres(context));
		deviceInfo.setEmeiMeidEsn(tMgr.getDeviceId());
		deviceInfo.setDeviceModel(android.os.Build.MODEL);
		deviceInfo.setOsBuildNumber(Build.FINGERPRINT);
		deviceInfo.setOsVersion(System.getProperty("os.version"));
		deviceInfo.setOsType(System.getProperty("os.name"));
		deviceInfo.setAppToken(APP_TOKEN);
		deviceInfo.setDeviceDensity(getDensity(context));
		deviceInfo.setAppVersion(APP_VERSION);
		deviceInfo.setCurrAppClientVersion(Util.getAppVersion(context));
		deviceInfo.setDeviceResolution(width + "x" + height);
		//deviceInfo.setCloudKeyToNotify(getGcmRegisteredID());
		return deviceInfo;
	}

	public static String getResourceValue(int resourceId) {
		return context.getString(resourceId);
	}

	public static boolean isLargeScreen() {
		return screenType.equals("largescreen");
	}

	public static boolean isNormalScreen() {

		return screenType.equals("normalscreen");
	}

	public static boolean isSmallScreen() {

		return screenType.equals("smallscreen");
	}

	public static String getScreenSize(Activity activity) {
		if ((activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
			// Toast.makeText(this, "Large screen", Toast.LENGTH_LONG)
			// .show();
			return "largescreen";

		} else if ((activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
			// Toast.makeText(this, "Normal sized screen" ,
			// Toast.LENGTH_LONG).show();
			return "normalscreen";

		} else if ((activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
			// Toast.makeText(this, "Small sized screen" ,
			// Toast.LENGTH_LONG).show();
			// setsmallConstants();
			return "smallscreen";

		} else {
			// Toast.makeText(this,
			// "Screen size is neither large, normal or small" ,
			// Toast.LENGTH_LONG).show();
			return "none";

		}
	}

	public static void getScreenResolution(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		height = dm.heightPixels;
	}

	public static boolean isHDPIDevice() {
		System.out.println("hdpi");
		System.out.println("hdpimsg " + deviceType);
		return deviceType.equals("hdpi");
	}

	public static boolean isMDPIDevice() {
		System.out.println("mdpi");
		System.out.println("mdpimsg " + deviceType);
		return deviceType.equals("mdpi");
	}

	public static boolean isLDPIDevice() {
		System.out.println("ldpi");
		System.out.println("ldpimsg " + deviceType);
		return deviceType.equals("ldpi");
	}

	// Method to get the Density of the device.

	private static String getDensity(Context context) {
		String r;
		DisplayMetrics metrics = new DisplayMetrics();

		if (!(context instanceof Activity)) {
			r = "hdpi";
		} else {
			Activity activity = (Activity) context;
			activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

			if (metrics.densityDpi <= DisplayMetrics.DENSITY_LOW) {
				r = "ldpi";
			} else if (metrics.densityDpi <= DisplayMetrics.DENSITY_MEDIUM) {
				r = "mdpi";
			} else {
				r = "hdpi";
			}
		}

		return r;
	}
}
