package erandoo.app.database;

import android.provider.BaseColumns;

public class TaskLocation {
	public static final String TABLE_NAME = "task_location";
	
	public static final String ID = BaseColumns._ID;
	public static final String FLD_COUNTRY_CODE = "country_code";
	public static final String FLD_COUNTRY_NAME = "country_name";
	public static final String FLD_MOBILE_REC_ID = "mobile_rec_id"; 
	public static final String FLD_LOCATION_REGION = "is_location_region";
	public static final String FLD_TASK_ID = "task_id";
	public static final String FLD_REGION_ID = "region_id";
	public static final String FLD_WORK_LOCATION_ID = "work_location_id";
	public static final String FLD_LONGITUDE = "longitude";
	public static final String FLD_LATITUDE = "latitude";
	public static final String FLD_LOC_GEO_AREA = "location_geo_area";
}
