package erandoo.app.database;

import java.io.Serializable;

public class City implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "city";
	
	public static final String CITY_ID = "city_id";
	public static final String CITY_NAME = "city_name";

	public String city_id;
	public String city_name;
	public City(){
		
	}
}
