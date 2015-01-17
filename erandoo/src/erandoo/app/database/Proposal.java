package erandoo.app.database;

import java.io.Serializable;
import java.util.ArrayList;

public class Proposal implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public static final String FLD_TASK_TASKER_ID = "task_tasker_id";
	public static final String FLD_TASKER_COMMENTS = "tasker_comments";
	public static final String FLD_MY_PAY = "my_pay";
	public static final String FLD_POSTER_PAY = "poster_pay";
	public static final String FLD_PROPOSED_DURATION = "proposed_duration";
	public static final String FLD_MULTIQUESTION_REPLY = "multiquestion_reply";
	public static final String FLD_TASK_ID = "task_id";
	public static final String FLD_PLAN_VALUE_IS_PERCENT = "plan_value_is_percent";
    public static final String FLD_PLAN_VALUE = "plan_value";
    
    public static final String FLD_AGREE_FOR_PROJECT_COMPLETE = "agree_for_project_complete";
    public static final String FLD_AGREE_FOR_SATISFACTION = "agree_for_satisfaction";
    
	public String poster_comments;
	public String tasker_comments;
	public String proposed_completion_date;
	public String proposed_duration;
	public String project_assign_date;
	public String project_status;
	public String user_agent_id;
	public String doer_hire_service_fee_percent;
	public String doer_approval_pending;
	public String my_pay;
	public String poster_pay;
	public String status;
	
	public String agree_for_project_complete;
	public String agree_for_satisfaction;
	
	public TaskDoer task_doer;
	public ArrayList<Attachment> attachments;
	public ArrayList<Question> questions;
	
	public Long task_tasker_id;
	public String plan_value_is_percent;
	public String plan_value;
   
	public String bookmark_user;
	public String poster_message;
	public String complete_date;
	
	public Project projectData;
	public String viewMode; //edit,apply
	

	public ArrayList<String> filename;
	
	public Proposal() {

	}

}
