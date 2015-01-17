package erandoo.app.database;

import java.io.Serializable;

public class UserDetail implements Serializable{
	private static final long serialVersionUID = 1L;

	public String cloud_key_to_notify;
	public Long user_device_id;
	public String firstname;
	public String lastname;
	public String fullname;
	public Long user_id;
	public String email;
	public String account_type;
	public String user_token;
	public String lang_code = "en_us";
	
	public String skills_used_cnt;
	public String bids_used_cnt;
	public String skills_allowed;
	public String premium_background_check;
	public String premium_bids_allowed;
	public String background_check;
	public String safety_check;
	public String is_quiz_complete;
	public String is_profile_portfolio;
	public String active_projects_used_cnt;
	public String active_projects_allowed;
	public String bids_per_month_allowed;
	public String next_plan_end_date;
	public String next_plan_start_date;
	public String plan_start_date;
	public String plan_end_date;
	public String is_poster_license;
	public String is_premiumdoer_license;
	public String is_instantdoer_license;
	public String is_inpersondoer_license;
	public String is_virtualdoer_license;
	public String online_status;
	public String rating_avg_as_poster;
	public String rating_avg_as_tasker;
	public String latitude;
	public String longitude;
	public String task_done_cnt;
	public String task_post_cnt;
	public String userimage;
	public Long plan_id;
	public Long trno;
	public String is_profile_complete;
	
	
	public UserDetail(){
		
	}
}
