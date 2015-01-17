package erandoo.app.database;

import java.io.Serializable;

public class NetworkConnection implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Long connection_id;
	public Long invited_by_user_id;
	public String approval_status;
	
	public NetworkConnection(){
		
	}
}
