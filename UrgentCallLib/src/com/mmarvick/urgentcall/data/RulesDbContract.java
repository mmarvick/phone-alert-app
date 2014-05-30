package com.mmarvick.urgentcall.data;

import android.provider.BaseColumns;

public class RulesDbContract {
	public RulesDbContract() {};
	
	public static abstract class RulesEntry implements BaseColumns{
		public static final String TABLE_NAME = "rules";
		public static final String COLUMN_NAME_CONTACT_LOOKUP = "lookup";
		public static final String REPEATED_CALL_STATE = "on_state";
		public static final String SINGLE_CALL_STATE = "single_call_state";
		public static final String MSG_STATE = "msg_state";
		
		//Use these constants for individual user preferences for all 3 alert types
		public static final int STATE_OFF = 0;
		public static final int STATE_ON = 1;
		public static final int STATE_DEFAULT = 2;
	}
}