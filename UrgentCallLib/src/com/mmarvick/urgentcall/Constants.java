package com.mmarvick.urgentcall;

public class Constants {
	public final static String SIMPLE_STATE = "STATE";
	public final static int SIMPLE_STATE_OFF = 0;
	public final static int SIMPLE_STATE_ON = 1;
	public final static int SIMPLE_STATE_SOME = 2;
	public final static int[] SIMPLE_STATES = {SIMPLE_STATE_OFF,
		SIMPLE_STATE_SOME,
		SIMPLE_STATE_ON};
	
	public final static String SNOOZE_TIME = "SNOOZE";
	
	public final static String CALL_QTY = "CALL_QTY";
	public final static String CALL_MIN = "CALL_MIN";
	
	public final static int CALL_QTY_DEFAULT = 3;
	public final static int CALL_MIN_DEFAULT = 15;
	
	public final static String LIST_TYPE = "LIST_TYPE";
	public final static int LIST_WHITELIST = 0;
	public final static int LIST_BLACKLIST = 1;
}
