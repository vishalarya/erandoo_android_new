package erandoo.app.database;

import java.io.Serializable;

public class Question implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "category_question";
	
	public static final String FLD_CATEGORY_ID = "category_id";
	public static final String FLD_QUESTION_ID = "question_id";	
	public static final String FLD_QUESTION_DESC = "question_desc";
	public static final String FLD_TRNO = "trno";
	public static final String FLD_QUESTION = "question";
	public static final String FLD_MOBILE_REC_ID = "mobile_rec_id"; 
	public static final String FLD_TASK_QUESTION_ID = "task_question_id";
	public static final String FLD_REPLY_DESC = "reply_desc";

	public String category_id;
	public Long question_id;
	public String question_desc;
	public String reply_desc;
	public Long task_question_id;
	public boolean isSelected = false;
	
	public Question(){
		
	}
}

