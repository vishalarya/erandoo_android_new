package erandoo.app.database;

import java.io.Serializable;

public class Money implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String created_at;
	public	String reason;
	public String project_title;
	
	public String  project_url;
	public String  invoice_id;
	public String  invoice_url;
	public String user_name;
	public	String debit;
	public	String credit;
	public	String balance;
	public	String status;
	

	public float spent;
	public float earn;
	public float wallet;
	
	public Money()
	{
		
	}

}
