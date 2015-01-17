package erandoo.app.database;

import java.io.Serializable;

import android.provider.BaseColumns;


public class Attachment implements Serializable{
	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "attachment";
	
	public static final String ID = BaseColumns._ID;
	public static final String FLD_FILE_URL = "file_url";
	public static final String FLD_FILE_NAME = "file_name";
	public static final String FLD_MOBILE_REC_ID = "mobile_rec_id"; 

	public String file_url;
	public String file_name;
	public Attachment(){
		
	}
}
