package com.mmarvick.urgentcall;

import android.provider.BaseColumns;

public class RulesDbContract {
	public RulesDbContract() {};
	
	public static abstract class RulesEntry implements BaseColumns{
		public static final String TABLE_NAME = "rules";
		public static final String COLUMN_NAME_CONTACT_LOOKUP = "lookup";
		public static final String COLUMN_NAME_CALLS = "num_calls";
		public static final String COLUMN_NAME_MINS = "num_minutes";
		public static final String COLUMN_NAME_ON = "on_state";
		public static final String COLUMN_NAME_SYS_TYPE = "sys_type";
		public static final String LOOKUP_DEFAULT = "_default";
	}
}