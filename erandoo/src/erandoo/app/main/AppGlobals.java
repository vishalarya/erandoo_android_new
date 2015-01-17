package erandoo.app.main;

import java.util.ArrayList;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import erandoo.app.database.Country;
import erandoo.app.database.DatabaseMgr;
import erandoo.app.database.Question;
import erandoo.app.database.Skill;
import erandoo.app.database.TaskDoer;
import erandoo.app.database.UserDetail;
import erandoo.app.database.WorkLocation;
import erandoo.app.utilities.Util;

public class AppGlobals extends Application {
	
	public static UserDetail userDetail;
	
	public static ArrayList<Country> countries = new ArrayList<Country>(0);
	public static ArrayList<Skill> categorySkills = new ArrayList<Skill>(0);
	public static ArrayList<Question> categoryQuestions = new ArrayList<Question>(0);
	public static ArrayList<TaskDoer> invitedDoers = new ArrayList<TaskDoer>(0);
	public static ArrayList<WorkLocation> workLocations = new ArrayList<WorkLocation>(0);

	private static Typeface robotoBold;
	private static Typeface robotoLight;
	private static Typeface robotoRegular;
	
	public static String latitude = "123";
	public static String longitude = "567";

	public static Context appContext = null;
	public static boolean isChatOpened = false;
	public static WorkLocation defaultWLocation = new WorkLocation();
	private static LocationManager locationManager;
	private static LOCListener loclistener;

	public static String whichBtnClick;

	@Override
	public void onCreate() {
		super.onCreate();
		userDetail = new UserDetail();
		robotoBold = Typeface.createFromAsset(getApplicationContext()
				.getAssets(), "fonts/Roboto-Bold.ttf");

		robotoLight = Typeface.createFromAsset(getApplicationContext()
				.getAssets(), "fonts/Roboto-Light.ttf");

		robotoRegular = Typeface.createFromAsset(getApplicationContext()
				.getAssets(), "fonts/Roboto-Regular.ttf");

		try {
			getLocation();
			Util.getAppRootDirectory(getApplicationContext());
			DatabaseMgr.getInstance(getApplicationContext());
			appContext = getApplicationContext();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static Typeface getRobotoBold() {
		return robotoBold;
	}

	public static Typeface getRobotoLight() {
		return robotoLight;
	}

	public static Typeface getRobotoRegular() {
		return robotoRegular;
	}
	
	public void getLocation(){
	//	Util.showProDialog(this); 
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		loclistener = new LOCListener();
		boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        
		if(enabled){
            locationManager.requestLocationUpdates(locationManager.getBestProvider(createFineCriteria(),true), 0, 0, loclistener);
        }else{
        	//Util.dimissProDialog(); 
           // Util.showCenteredToast(getApplicationContext(), "Please connect to internet...");
        }
    }
	
	private static Criteria createFineCriteria() {
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);
        c.setAltitudeRequired(false);
        c.setBearingRequired(false);
        c.setSpeedRequired(false);
        c.setCostAllowed(true);
        c.setPowerRequirement(Criteria.POWER_HIGH);
        return c;
    }
	
	private class LOCListener implements LocationListener{
		@Override
		public void onLocationChanged(Location location) {
			try{
			//	Util.dimissProDialog();
				
				AppGlobals.latitude = String.valueOf(location.getLatitude());
				AppGlobals.longitude = String.valueOf(location.getLongitude());

				AppGlobals.defaultWLocation.latitude = AppGlobals.latitude;
				AppGlobals.defaultWLocation.longitude = AppGlobals.longitude;
	        	
				locationManager.removeUpdates(loclistener);
				
			/*	Geocoder gc = new Geocoder(getApplicationContext(), Locale.getDefault());
		        List<Address> addresses = gc.getFromLocation(Double.valueOf(latitude), Double.valueOf(longitude), 1);
		        AppGlobals.defaultWLocation.address = addresses.get(0).toString();*/
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			//Util.dimissProDialog();
		}

		@Override
		public void onProviderEnabled(String provider) {
			//Util.dimissProDialog();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			//Util.dimissProDialog();
		}
		
	}
}
