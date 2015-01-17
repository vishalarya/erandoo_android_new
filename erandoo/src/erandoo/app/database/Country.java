package erandoo.app.database;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

public class Country implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "country";

	public static final String FLD_COUNTRY_CODE = "country_code";
	public static final String FLD_COUNTRY_NAME = "country_name";
	public static final String FLD_COUNTRY_STATUS = "country_status";

	public String country_code;
	public String country_name;
	public String country_status;
	public Long trno;
	
	@Expose(deserialize = false)
	public boolean isSelected = false;
	
	public Country(){
		
	}
}
