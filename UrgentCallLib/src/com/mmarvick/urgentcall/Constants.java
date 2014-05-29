package com.mmarvick.urgentcall;

public class Constants {
	public final static String SIMPLE_STATE = "STATE";
	public final static int SIMPLE_STATE_OFF = 0;
	public final static int SIMPLE_STATE_ON = 1;
	public final static int SIMPLE_STATE_WHITELIST = 2;
	public final static int SIMPLE_STATE_BLACKLIST = 3;
	public final static int[] SIMPLE_STATES = {SIMPLE_STATE_OFF,
		SIMPLE_STATE_ON};
	
	public final static String SNOOZE_TIME = "SNOOZE";
	
	public final static String CALL_QTY = "CALL_QTY";
	public final static String CALL_QTY_TITLE = "Number of calls";
	public final static int CALL_QTY_MIN = 2;
	public final static int CALL_QTY_MAX = 9;
	public final static int CALL_QTY_DEFAULT = 3;
	
	public final static String CALL_MIN = "CALL_MIN";
	public final static String CALL_MIN_TITLE = "Number of minutes";
	public final static int CALL_MIN_MIN = 1;
	public final static int CALL_MIN_MAX = 60;
	public final static int CALL_MIN_DEFAULT = 15;
	
	public final static String LIST_TYPE = "LIST_TYPE";
	public final static int LIST_WHITELIST = 1;
	public final static int LIST_BLACKLIST = 2;
	
	public final static String SETTING_VOLUME = "VOLUME";
	public final static String SETTING_VOLUME_CHANGED = "VOLUME_CHANGED";
	
	public final static String DISCLAIMER_VERSION = "DISCLAIMER_VERSION";
	public final static int DISCLAIMER_DEFAULT = 0;
	public final static String DISCLAIMER_BACKUP_MODE = "DISCLAIMER_BACKUP";
	public final static String DISCLAIMER_BACKUP_FLAG = "DISCLAIMER_BACKUP_FLAG";
}
