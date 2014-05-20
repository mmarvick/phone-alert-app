package com.mmarvick.urgentcall;

public class Constants {
	public final static String MODE = "MODE";
	public final static int MODE_SIMPLE = 0;
	public final static int MODE_ADVANCED = 1;
	
	public final static String SIMPLE_STATE = "STATE";
	public final static int SIMPLE_STATE_OFF = 0;
	public final static int SIMPLE_STATE_ON = 1;
	public final static int SIMPLE_STATE_WHITELIST = 2;
	public final static int SIMPLE_STATE_BLACKLIST = 3;
	public final static int[] SIMPLE_STATES = {SIMPLE_STATE_OFF,
		SIMPLE_STATE_WHITELIST, SIMPLE_STATE_BLACKLIST,
		SIMPLE_STATE_ON};
}