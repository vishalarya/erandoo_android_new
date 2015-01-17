package erandoo.app.tempclasses;

import java.io.Serializable;
import java.util.ArrayList;

import erandoo.app.database.Category;
import erandoo.app.database.Country;
import erandoo.app.database.Skill;
import erandoo.app.database.UserDetail;
import erandoo.app.database.WorkLocation;

public class MasterSyncData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public DataLists data; 
	
	public MasterSyncData(){
		
	}
	
	public class DataLists implements Serializable{
		private static final long serialVersionUID = 1L;
		public ArrayList<Country> country;
		public ArrayList<Category> category;
		public ArrayList<WorkLocation> work_location;
		public ArrayList<Skill> skill;
		public UserDetail user_detail;
		public String base_url;

		public DataLists(){
			
		}
	}
}
