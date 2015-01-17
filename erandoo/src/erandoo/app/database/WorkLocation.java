package erandoo.app.database;

import java.io.Serializable;

public class WorkLocation implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "work_location";
	public static final String FLD_WORK_LOCATION_ID = "work_location_id";
	public static final String FLD_COUNTRY_CODE = "country_code";
	public static final String FLD_COUNTRY_NAME = "country_name";
	public static final String FLD_STATE_ID = "state_id";
	public static final String FLD_STATE_NAME = "state_name";
	public static final String FLD_REGION_ID = "region_id";
	public static final String FLD_REGION_NAME = "region_name";
	public static final String FLD_CITY_ID = "city_id";
	public static final String FLD_CITY_NAME = "city_name";
	public static final String FLD_ADDRESS = "address";
	public static final String FLD_ZIP_CODE = "zipcode";
	public static final String FLD_LOCATION_NAME = "location_name";
	public static final String FLD_IS_DEFAULT_LOCATION = "is_default_location";
	public static final String FLD_LATITUDE = "latitude";
	public static final String FLD_LONGITUDE = "longitude";
	public static final String FLD_IS_BILLING = "is_billing";
	public static final String FLD_IS_SHIPPING = "is_shipping";
	public static final String FLD_STATUS = "status";
	
	public String work_location_id;
	public String location_geo_area;
	public String location_name;
	public String address;
	public String latitude;
	public String longitude;
	public String is_default_location;
	public String zipcode;
	public String status;
	public String source_app;
	public String is_billing;
	public String is_shipping;
	public Country country;
	public State state;
	public Region region;
	public City city;
	
	public WorkLocation(){
		
	}
}
