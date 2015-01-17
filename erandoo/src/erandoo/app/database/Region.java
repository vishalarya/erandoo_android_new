package erandoo.app.database;

import java.io.Serializable;

public class Region implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "region";
	
	public static final String REGION_ID = "region_id";
	public static final String REGION_NAME = "region_name";
	
	public String region_id;
	public String region_name;
	public Region(){
		
	}

}
