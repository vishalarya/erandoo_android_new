package erandoo.app.tempclasses;

import java.io.Serializable;
import java.util.ArrayList;

import erandoo.app.database.Money;

public class MoneyData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ArrayList<Money> wallet_info;
	

	public MoneyData() {

	}
}
