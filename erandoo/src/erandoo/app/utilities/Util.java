package erandoo.app.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import erandoo.app.R;
import erandoo.app.config.Config;
import erandoo.app.database.Project;
import erandoo.app.main.AppGlobals;
import erandoo.app.main.SignInActivity;
import erandoo.app.mqtt.ErandooMqttMessage;
import erandoo.app.utilities.ScalingUtilities.ScalingLogic;

@SuppressLint("SimpleDateFormat")
public class Util {

	public static ProgressDialog progressDialog = null;
	
	public static int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }
	
	public static void showProDialog(Context context) {
		progressDialog = new ProgressDialog(context);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Wait...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setProgress(0);
		progressDialog.show();
	}
	
	public static void dimissProDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	
	public static void showCenteredToast(Context ctx, String msg) {
		Toast toast = Toast.makeText(ctx, msg, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	public static void hideKeypad(Context ctx, EditText vEText) {
		InputMethodManager imm = (InputMethodManager) ctx
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(vEText.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public static boolean isDeviceOnline(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			return netInfo.isConnected();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String getDeviceMacAddres(Context context) {
		WifiManager manager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = manager.getConnectionInfo();
		return info.getMacAddress();
	}

	public static String getDeviceID(Context context) {
		return Secure
				.getString(context.getContentResolver(), Secure.ANDROID_ID);
	}

	public static Integer getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}

	public static void startActivity(Context context, Class<?> class1) {
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setClass(context, class1);
		((Activity) context).startActivity(intent);
		((Activity) context).finish();
	}

	public static void loadFragment(FragmentActivity frgActivity,
			int containerId, Fragment frag, boolean isBackStack) {
		FragmentTransaction ft = frgActivity.getSupportFragmentManager()
				.beginTransaction();
		ft.setTransition(android.R.anim.bounce_interpolator);
		ft.add(containerId, frag);
		if (isBackStack) {
			ft.addToBackStack(null);
		}
		ft.commit();
	}

	public static void unloadFragment(FragmentActivity frgActivity,
			Fragment frag) {
		if (frag != null) {
			frgActivity.getSupportFragmentManager().beginTransaction()
					.remove(frag).commit();
		}
	}

	public static void ShowDialogFragment(FragmentActivity frgActivity,
			DialogFragment dFrag, String tag) {
		FragmentManager fm = frgActivity.getSupportFragmentManager();
		dFrag.show(fm, tag);
	}

	public static boolean isEmailValid(CharSequence email) {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}

	public static void loadImage(ImageView imgView, String url,
			int defaultResource) {
		UrlImageViewHelper.setUrlDrawable(imgView, url, defaultResource,0,
				new UrlImageViewCallback() {
					@Override
					public void onLoaded(ImageView imageView,
							Bitmap loadedBitmap, String url,
							boolean loadedFromCache) {
						if (!loadedFromCache) {
							ScaleAnimation scale = new ScaleAnimation(0, 1, 0,
									1, ScaleAnimation.RELATIVE_TO_SELF, .5f,
									ScaleAnimation.RELATIVE_TO_SELF, .5f);
							scale.setDuration(800);
							scale.setInterpolator(new OvershootInterpolator(0));
							imageView.startAnimation(scale);
						}
					}
				});
	}

	public static void quitFromAppPopup(Context context) {
		AlertDialog.Builder alertbox = new AlertDialog.Builder(context);
		alertbox.setMessage("Quit from erandoo");
		alertbox.setTitle("erandoo");
		alertbox.setIcon(android.R.drawable.ic_dialog_alert);
		alertbox.setCancelable(true);
		alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alertbox.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						System.exit(0);
					}
				});
		alertbox.show();
	}

	public static boolean compareTwoDates(String setDateByUser) {
		boolean setDatebooleanValue = false;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String currentTime = getCurrentDate();
			Date current = formatter.parse(currentTime);
			Date userDate = formatter.parse(setDateByUser);
			int compareValue = userDate.compareTo(current);

			if (compareValue >= 0) {
				setDatebooleanValue = true;
			} else {
				setDatebooleanValue = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return setDatebooleanValue;
	}

	public static String getCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public static String getCurrentDateWithMonth() {
		DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public static String setProjectDateWithMonth(String projectDate, boolean isSearch) {
		SimpleDateFormat dateFormat1;
		if(isSearch){
			dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
		}else{
			dateFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
		Date date = new Date();
		try {
			date = dateFormat1.parse(projectDate);
			projectDate = dateFormat.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return projectDate;
	}
	
	public static String getCurrentTime() {
		DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
		Date date = new Date();
		return dateFormat.format(date.getTime());
	}
	
	public static String getTimeIn24Format(String value){
		String str = "";
		try {
			if(!value.equals("")){
				SimpleDateFormat inFormat = new SimpleDateFormat("hh:mm aa");
				SimpleDateFormat outFormat = new SimpleDateFormat("HH:mm"); 
				str = outFormat.format(inFormat.parse(value));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	public static String getChangeDateFormat(String sendedDate,
			String oldPattern, String applyPattern, boolean retunrnWithTime) {
		String showingPate = "yyyy" + applyPattern + "MM" + applyPattern + "dd";
		String oldPat = "yyyy" + oldPattern + "MM" + oldPattern + "dd";
		if (sendedDate.contains(":")) {
			oldPat = "yyyy" + oldPattern + "MM" + oldPattern + "dd"
					+ " HH:mm:ss";
		}
		if (retunrnWithTime) {
			showingPate = "yyyy" + applyPattern + "MM" + applyPattern + "dd"
					+ " HH:mm:ss";
		}

		SimpleDateFormat sdf = new SimpleDateFormat(oldPat);

		try {
			Date date = sdf.parse(sendedDate);
			sdf.applyPattern(showingPate);
			sendedDate = sdf.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sendedDate;
	}

	public static boolean isExternalStorageAvailable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			return true;
		}
		// Media storage not present
		return false;
	}

	public static File getAppRootDirectory(Context appContext) {
		String appDirectory = Config.APP_FOLDER_NAME;
		File dir = null;
		if (isExternalStorageAvailable()) {
			// sdcard present
			dir = new File(Environment.getExternalStorageDirectory(),
					appDirectory);
		} else {
			// use cache directory
			dir = new File(appContext.getCacheDir(), appDirectory);
		}
		if (dir.exists() == false) {
			// System.out.println("root "+dir.getAbsolutePath());
			dir.mkdirs();
			// createNomediaFile(dir.getAbsolutePath());
		}

		File check_nomedia = new File(dir, ".nomedia");
		if (check_nomedia.exists()) {
			check_nomedia.delete();
		}

		return dir;
	}

	public static long getDateDiff(String selectedDate) {
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
			Date date1 = simpleDateFormat.parse(getCurrentDateWithMonth());
			Date date2 = simpleDateFormat.parse(selectedDate);
			long different = date2.getTime() - date1.getTime();

			long secondsInMilli = 1000;
			long minutesInMilli = secondsInMilli * 60;
			long hoursInMilli = minutesInMilli * 60;
			long daysInMilli = hoursInMilli * 24;

			return different / daysInMilli;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static void writeDBfileOnSdcard(Context context) {
		File f = new File(context.getDatabasePath("erandoo").getAbsolutePath());
		FileInputStream fis = null;
		FileOutputStream fos = null;

		try {
			fis = new FileInputStream(f);
			fos = new FileOutputStream(Environment
					.getExternalStorageDirectory().getAbsolutePath()
					+ "/erandoo.sqlite");
			while (true) {
				int i = fis.read();
				if (i != -1) {
					fos.write(i);
				} else {
					break;
				}
			}
			fos.flush();
			Log.v("DB file Exported", "Success");
		} catch (Exception e) {
			e.printStackTrace();
			Log.v("DB file Exported", "Failed");
		} finally {
			try {
				fos.close();
				fis.close();
			} catch (IOException ioe) {
			}
		}
	}

	public static int toInt(String value, int defaultVal) {
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			return defaultVal;
		}
	}

	public static int toInt(String value) {
		return toInt(value, 0);
	}
	
	public static double toDouble(String value, double defaultVal) {
		try {
			return Double.parseDouble(value);
		} catch (Exception e) {
			return defaultVal;
		}
	}

	public static double toDouble(String value) {
		return toDouble(value, 0);
	}
	
	public static float toFloat(String value, float defaultVal) {
		try {
			return Float.parseFloat(value);
		} catch (Exception e) {
			return defaultVal;
		}
	}

	public static float toFloat(String value) {
		return toFloat(value, 0);
	}
	
	public static boolean getBoolean(String value){
		int v = toInt(value);
		if(v == 1){
			return true;
		}else{
			return false;
		}
	}
	
	public static String getDistance(String slat1,String slon1,String slat2,String slon2){
		//Log.v("Values", "slat1="+slat1+",slon1="+slon1+",slat2="+slat2+",slon2="+slon2); 
		float distance = 0;
		try{
			Location locationA = new Location("point A");    
			locationA.setLatitude(Double.parseDouble(slat1)); 
			locationA.setLongitude(Double.parseDouble(slon1));
			
			Location locationB = new Location("point B");
			locationB.setLatitude(Double.parseDouble(slat2)); 
			locationB.setLongitude(Double.parseDouble(slon2));
			
			distance = locationA.distanceTo(locationB);
		}catch(Exception e){
			e.printStackTrace();
		}
		return String.format("%.2f",(distance * 0.00062137119));
	}
	
	public static Long getMobileRecId(String mobileRecToken){
		//Log.v("getMobileRecId-> mobileRecToken=", mobileRecToken==null?"null":mobileRecToken);
		if(mobileRecToken != null){
			boolean isMyRecord = mobileRecToken.startsWith(AppGlobals.userDetail.user_device_id+":");
			if(isMyRecord){
				String[] tokens = mobileRecToken.split(":");
				return Long.valueOf(tokens[1]);
			}
		}
		return null;
	}
	
	public static String getMobileRecToken(Long mobileRecId){
		return AppGlobals.userDetail.user_device_id+":"+mobileRecId;
	}
	
	public static void clearAsync(AsyncTask<?,?,?>  task){ 
		try{
			if(task != null && task.getStatus().equals(AsyncTask.Status.RUNNING)){ 
				task.cancel(true);
				dimissProDialog();
		    }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	//http://54.84.193.236/test/pn  to send test notification .
	public static void sendNotification(ErandooMqttMessage eMqttMessage) {
		NotificationManager mNotificationManager = (NotificationManager)AppGlobals.appContext.getSystemService(Context.NOTIFICATION_SERVICE);
		if(eMqttMessage != null){
			Integer notifiId = 1;//Integer.valueOf((int)System.currentTimeMillis());
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(AppGlobals.appContext);
			
			PendingIntent contentIntent = PendingIntent.getActivity(AppGlobals.appContext, notifiId,
			new Intent(AppGlobals.appContext, SignInActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(contentIntent);
					
			if(eMqttMessage.event_type.equals(Constants.MQTT_EVENT_TYPE_CHAT)){ 
				mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(eMqttMessage.msg));
				mBuilder.setContentTitle(eMqttMessage.from_display_id);
				mBuilder.setContentText(eMqttMessage.msg);
			}else{
				mBuilder.setContentTitle(AppGlobals.appContext.getResources().getString(R.string.app_name)); 
				mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(eMqttMessage.msg));
				mBuilder.setContentText(eMqttMessage.msg);
			}
			
			mBuilder.setSmallIcon(R.drawable.ic_notifi_bar);
			mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 } );
			mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)); 
			mBuilder.setAutoCancel(true);
			mNotificationManager.notify(notifiId, mBuilder.build());
		}
	}
	
	public static Object getJsonToClassObject(String jsonStr,Class<?> classs){ 
		try{
			Gson gson = new GsonBuilder().create();
			return (Object)gson.fromJson(jsonStr, classs);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getMsgThreadKey(Long from_id, Long to_id){
		if(from_id > to_id){
			return to_id+":"+from_id;
		}else{
			return from_id+":"+to_id;
		}
	}
	
	public static String getBudgetString(Project project){
		try{
			if(project.task_kind.equals(Constants.INSTANT)){ 
				return "$" +project.min_price +" " + AppGlobals.appContext.getResources().getString(R.string.fixed); 
			}else{
				if(project.payment_mode.equals(Constants.PAYMENT_MODE_HOURLY)){
					return AppGlobals.appContext.getResources().getString(R.string.hourly); 
				}else{
					return "$" +project.min_price +" " + AppGlobals.appContext.getResources().getString(R.string.fixed); 
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getProjectTypeName(String task_kind){
		if(task_kind.equals(Constants.VIRTUAL)){ 
			return Constants.PROJECT_VIRTUAL;
		}else if(task_kind.equals(Constants.INPERSON)){
			return Constants.PROJECT_INPERSON;
		}else{
			return Constants.PROJECT_INSTANT;
		}
	}
	
	public static void shareProjectLink(Context ctx, String title, String link){
		 try {
			    Intent share = new Intent(android.content.Intent.ACTION_SEND);
			    share.setType("text/plain");
			    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			    share.putExtra(Intent.EXTRA_SUBJECT, title);
				share.putExtra(Intent.EXTRA_TEXT, urlValidator(link));
				ctx.startActivity(Intent.createChooser(share, "Share link!"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String urlValidator(String link){
		URL url = null;
		try{
			url = new URL(link);
			URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
			url = uri.toURL();
		}catch(Exception e){
			e.printStackTrace();
		}
		return url.toString();
	}
	
	public static void scrollUpToScrollView(final ScrollView scrollViewSign) {
		scrollViewSign.post(new Runnable() {
			@Override
			public void run() {
				scrollViewSign.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});
	}

	public static Spanned getRestrictedName(Context ctx, String StringName) {
		String restrictedString = null;

		int id = ctx.getResources().getIdentifier(StringName, "string",
				ctx.getPackageName());
		restrictedString = ctx.getResources().getString(id);
		restrictedString = restrictedString + "<font color=" + "'"
				+ ctx.getResources().getColor(R.color.red_dark) + "'" + ">"
				+ " *" + "</font>";
		Spanned ss = Html.fromHtml(restrictedString);
		return ss;
	}
	
//	public static boolean isActivityRuning(Context context,Activity activity){
//		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
//
//        for (ActivityManager.RunningTaskInfo task : tasks) {
//            if (activity.getClass().getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName()))
//                return true;
//        }
//
//        return false;
//	}
	
	
//	double eRadius = 3958;
//	double dLat = Math.toRadians(lat2 - lat1);
//	double dLon = Math.toRadians(lon2 - lon1);
//	double att = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lon1)) * Math.cos(Math.toRadians(lon2)) * Math.sin(dLon/2) * Math.sin(dLon/2);
//    double ctt = 2 * Math.asin(Math.sqrt(att));
//    double dtt = eRadius * ctt;

	// --------------SAMPLE CODE TO VALIDATE A PHONE NUMBER-----------------

	/*
	 * This is required on Sign and Sign Up time [10:17:18 AM] Ajay Thapar:
	 * PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance(); [10:17:48 AM]
	 * Ajay Thapar: phNumberProto = phoneUtil.parse(phonenumber, countryIso);
	 * [10:18:50 AM] Ajay Thapar: // check if the number is valid boolean
	 * isValid = phoneUtil.isValidNumber(phNumberProto); //
	 * System.out.println("getCanonicalPhoneNumber isValid "+isValid); if
	 * (isValid) { // get the valid number's international format
	 * internationalFormat = phoneUtil.format(phNumberProto,
	 * PhoneNumberFormat.E164); } [10:19:23 AM] Ajay Thapar: For this you need
	 * to use the following jar [10:19:24 AM] Ajay Thapar:
	 * libphonenumber-6.1.jar
	 */
	public static String decodeFile(String path,int DESIREDWIDTH, int DESIREDHEIGHT,int rotation) {
        String strMyImagePath = null;
        Bitmap scaledBitmap = null;

        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT, ScalingLogic.FIT);

//            if (rotation != 0) {
//    			Matrix matrix = new Matrix();
//    			matrix.postRotate(rotation);
//    			unscaledBitmap = Bitmap.createBitmap(unscaledBitmap, 0, 0, unscaledBitmap.getWidth(),
//    					unscaledBitmap.getHeight(), matrix, true);
//    			
//    		}
            
            if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap.getHeight() <= DESIREDHEIGHT)) {
                // Part 2: Scale image
            	
                scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingLogic.FIT,rotation);
            } else {
                unscaledBitmap.recycle();
                return path;
            }

            // Store to tmp file

            String extr = Environment.getExternalStorageDirectory().toString();
            File mFolder = new File(extr + "/TMMFOLDER");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }

            String s = "tmp.png";

            File f = new File(mFolder.getAbsolutePath(), s);

            strMyImagePath = f.getAbsolutePath();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }

            scaledBitmap.recycle();
        } catch (Throwable e) {
        }

        if (strMyImagePath == null) {
            return path;
        }
        return strMyImagePath;

    }
	
	
	public static Dialog ShowYesNoDialog(Context ctx, String contentData,String yes,String no,String title){
		final Dialog dialog = new Dialog(ctx);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.ed_custom_dialog);
		TextView txtTitleCD = (TextView) dialog.findViewById(R.id.txtTitleCD);
		TextView txtDialogNo = (TextView) dialog.findViewById(R.id.txtDialogNo);
		TextView txtDialogYes = (TextView) dialog.findViewById(R.id.txtDialogYes);
		TextView txtMessage = (TextView) dialog.findViewById(R.id.txtMessage);
		
		txtDialogYes.setText(yes);
		txtDialogNo.setText(no);
		txtMessage.setText(contentData);
		txtTitleCD.setText(title);
		txtDialogYes.setOnClickListener((OnClickListener) ctx);
		
		txtDialogNo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(ctx.getResources().getColor(R.color.trans_color)));
		dialog.show();
		return dialog;
	}
	/**
	 * Rotate an image if required.
	 * 
	 * @param img
	 * @param selectedImage
	 * @return
	 */
//	private static Bitmap rotateImageIfRequired( Bitmap img,Uri selectedImage,int rotation) {
//		if (rotation != 0) {
//			Matrix matrix = new Matrix();
//			matrix.postRotate(rotation);
//			Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(),
//					img.getHeight(), matrix, true);
//			img.recycle();
//			return rotatedImg;
//		} else {
//			return img;
//		}
//	}
	
	/*public static void sendNotification(ErandooMqttMessage eMqttMessage) {
		NotificationManager mNotificationManager = (NotificationManager)AppGlobals.appContext.getSystemService(Context.NOTIFICATION_SERVICE);
		if(eMqttMessage != null){
			Integer notifiId = 1;//Integer.valueOf((int)System.currentTimeMillis());
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(AppGlobals.appContext);
			
			if(eMqttMessage.event_type.equals(Constants.MQTT_EVENT_TYPE_CHAT)){ 
				notifiId = ChatMgr.createChatNotificationId(eMqttMessage.getFriendUserId(), notifiId);
				PendingIntent contentIntent = PendingIntent.getActivity(AppGlobals.appContext, notifiId,
				new Intent(AppGlobals.appContext, ChatNHandlerActivity.class).putExtra(Constants.SERIALIZABLE_DATA,eMqttMessage), PendingIntent.FLAG_UPDATE_CURRENT);
				mBuilder.setContentIntent(contentIntent);
				String msg = ChatMgr.createChatNotificationMsg(eMqttMessage);
				mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(msg));
				mBuilder.setContentTitle(eMqttMessage.from_display_id);
				mBuilder.setContentText(msg);
			}else{
				mBuilder.setContentTitle(AppGlobals.appContext.getResources().getString(R.string.app_name)); 
				mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(eMqttMessage.msg));
				mBuilder.setContentText(eMqttMessage.msg);
			}
			mBuilder.setSmallIcon(R.drawable.ic_notifi_bar);
			mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 } );
			mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)); 
			mBuilder.setAutoCancel(true);
			mNotificationManager.notify(notifiId, mBuilder.build());
		}
	}*/
}
