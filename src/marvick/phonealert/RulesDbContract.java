package marvick.phonealert;

import android.provider.BaseColumns;

public class RulesDbContract {
	public RulesDbContract() {};
	
	public static abstract class RulesEntry implements BaseColumns{
		public static final String TABLE_NAME = "rules";
		public static final String COLUMN_NAME_CONTACT_LOOKUP = "contact";
		public static final String COLUMN_NAME_CALLS = "num_calls";
		public static final String COLUMN_NAME_MINS = "num_minutes";
	}
}