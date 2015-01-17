package erandoo.app.database;

import java.io.Serializable;

public class Recommandation implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String FLD_RECOMMANDATION_ID = "recommendation_id";
	public static final String FLD_RECOMMANDATION_DESC = "recommendation_desc";
	public static final String FLD_FULL_NAME = "fullname";
	public static final String FLD_IS_PUBLIC = "is_public";
	public static final String FLD_CREATED_AT = "created_at";
	public static final String FLD_USER_IMAGE = "userimage";

	
	public String recommendation_id;
	public String recommendation_desc;
	public String fullname;
	public String is_public;
	public String created_at;
	public String userimage;
	
	public Recommandation(){
		
	}
}
