package com.mmarvick.urgentcall.data;

import android.provider.BaseColumns;

public class RulesDbContract {
	public RulesDbContract() {};
	
	public static abstract class RulesEntry implements BaseColumns{
		public static final String TABLE_NAME = "rules";
		public static final String COLUMN_NAME_CONTACT_LOOKUP = "lookup";
		public static final String COLUMN_NAME_REPEATED_CALL_ON = "on_state";
		public static final String COLUMN_NAME_SINGLE_CALL_ON = "single_call_state";
		public static final String COLUMN_NAME_MSG_ON = "msg_state";
	}
}