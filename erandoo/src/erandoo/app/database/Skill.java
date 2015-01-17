package erandoo.app.database;

import java.io.Serializable;

public class Skill implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "skill";
	
	public static final String FLD_SKILL_ID = "skill_id";
	public static final String FLD_SKILL_DESC = "skill_desc";
	public static final String FLD_SKILL_STATUS = "status";

	public boolean isSelected = false;
	
	public String skill_id;
	public String skill_desc;
	public String status;
	public Long trno;
	public Skill(){}
}
