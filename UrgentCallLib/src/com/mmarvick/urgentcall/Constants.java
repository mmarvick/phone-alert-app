package com.mmarvick.urgentcall;

public class Constants {
	
	/* --------------- Settings ---------------------- 
	 * Note that many of the strings are keys to a saved preference.
	 */

	// On/off for entire app
	public final static String APP_STATE = "STATE";
	
	// Used for both the overall app state and individual alert type states
	public final static int URGENT_CALL_STATE_OFF = 0;
	public final static int URGENT_CALL_STATE_ON = 1;
	public final static int URGENT_CALL_STATE_WHITELIST = 2;
	public final static int URGENT_CALL_STATE_BLACKLIST = 3;
	
	// Snooze end time
	public final static String SNOOZE_TIME = "SNOOZE";
	
	// Repeated call setting for quantity of calls
	public final static String CALL_QTY = "CALL_QTY";
	public final static String CALL_QTY_TITLE = "Number of calls";
	public final static int CALL_QTY_MIN = 1;
	public final static int CALL_QTY_MAX = 9;
	public final static int CALL_QTY_DEFAULT = 3;
	
	// Repeated call setting for number of minutes
	public final static String CALL_MIN = "CALL_MIN";
	public final static String CALL_MIN_TITLE = "Number of minutes";
	public final static int CALL_MIN_MIN = 1;
	public final static int CALL_MIN_MAX = 60;
	public final static int CALL_MIN_DEFAULT = 15;
	
	// Message alert setting for keyword
	public final static String MSG_MESSAGE = "MSG_MESSAGE";
	public final static String MSG_MESSAGE_TITLE = "Message Key";
	public final static String MSG_MESSAGE_DEFAULT = "Urgent!";
	public final static int MSG_MESSAGE_MIN = 5;
	
	// Volume alert setting
	public final static int ALERT_VOLUME_MAX = 1000;
	public final static int ALERT_VOLUME_DEFAULT = ALERT_VOLUME_MAX;
	
	// How to alert
	public final static String ALERT_HOW_RING = "0";
	public final static String ALERT_HOW_VIBE = "1";
	public final static String ALERT_HOW_RING_AND_VIBE = "2";
	public final static String ALERT_HOW_DEFAULT = ALERT_HOW_RING;
	
	// Message alarm constant for how long alert plays for
	public final static int MSG_ALARM_TIME = 10;
	
	// Call alert type
	public final static int CALL_ALERT_TYPE_RC = 0;
	public final static int CALL_ALERT_TYPE_SC = 1;
	public final static int CALL_ALERT_TYPE_BOTH = 2;
	
	// Most recently accepted disclaimer
	public final static String DISCLAIMER_VERSION = "DISCLAIMER_VERSION";
	public final static int DISCLAIMER_DEFAULT = 0;
	
	// If the disclaimer is showing, a backup of the 
	public final static String DISCLAIMER_BACKUP_MODE = "DISCLAIMER_BACKUP";
	public final static String DISCLAIMER_BACKUP_FLAG = "DISCLAIMER_BACKUP_FLAG";
	
	// Extensions for data about different alerts.
	public final static String ALERT_BACKUP = "_BACKUP"; // a backup of the alert state
	public final static String ALERT_TIME = "_TIME"; // time for alert
	public final static String ALERT_VOLUME = "_VOLUME"; // volume for an alert
	public final static String ALERT_SOUND = "_SOUND"; // sound for an alert
	public final static String ALERT_HOW = "_HOW"; // how to alert (ring, vibrate, etc...)
	
	/* --------------- Intent Flags ---------------------- 
	 * These are keys for values passed in the intent to the ContactListActivity so
	 * that it knows whether to show a whitelist or blacklist, and for what alert type
	 */
	
	public final static String ALERT_TYPE = "ALERT_TYPE";
	public final static String USER_STATE = "USER_STATE";
	

}
