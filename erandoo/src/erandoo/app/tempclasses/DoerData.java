package erandoo.app.tempclasses;

import java.io.Serializable;
import java.util.ArrayList;

import erandoo.app.database.TaskDoer;

public class DoerData implements Serializable{
	private static final long serialVersionUID = 1L;
	public ArrayList<TaskDoer> data;
	public String is_more;
	public DoerData(){
		
	}
}
