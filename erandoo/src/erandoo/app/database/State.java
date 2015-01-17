package erandoo.app.database;

import java.io.Serializable;

public class State implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "state";
	
	public static final String STATE_ID = "state_id";
	public static final String STATE_NAME = "state_name";
	
	public String state_id;
	public String state_name;
	public State(){
		
	}
}
