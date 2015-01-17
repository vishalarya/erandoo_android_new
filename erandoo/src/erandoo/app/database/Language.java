package erandoo.app.database;

import android.provider.BaseColumns;

public class Language {
	public static final String TABLE_NAME = "language";
	
	public static final String ID = BaseColumns._ID;
	public static final String LANGUAGE_CODE = "language_code";
	public static final String LANGUAGE_NAME = "language_name";
	public static final String LANGUAGE_STATUS = "language_status";
	public static final String LANGUAGE_PRIORITY = "language_priority";
	public static final String CREATE_TIMESTAMP = "create_timestamp";
	public static final String UPDATE_TIMESTAMP = "update_timestamp";
}
