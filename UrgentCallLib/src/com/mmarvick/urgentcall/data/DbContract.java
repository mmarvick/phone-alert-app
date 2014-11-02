package com.mmarvick.urgentcall.data;

import android.provider.BaseColumns;

/** Contract for the tables that stores information about alert (call or
 * text) rules
 */
public abstract class DbContract {
	
	/** Contract for the table that stores information about an individual
	 * alert (call or text) rule
	 */	
	public static abstract class RuleEntry implements BaseColumns {
		
		/** Name for the column that stores the title for a rule */
		public static final String COLUMN_TITLE = "TITLE";
		
		/** Name for the column that stores whether the rule is on or off */
		public static final String COLUMN_ON_STATE = "ON_STATE";
		
		/** Name for the column that stores whether the rule is for everyone,
		 * whitelisted contacts, or blacklisted contacts only
		 */
		public static final String COLUMN_FILTER_BY = "CONTACT_MODE";
		
		/** Name for the column that stores whether or not this alert should
		 * cause the phone to ring.
		 */
		public static final String COLUMN_RING = "RING";
		
		/** Name for the column that stores whether or not this alert should
		 * cause the phone to vibrate.
		 */
		public static final String COLUMN_VIBRATE = "VIBRATE";
		
		/** Name for the column that stores the ringtone that should play
		 * for this alert.
		 */
		public static final String COLUMN_TONE = "TONE";
		
		/** Name for the column that stores the volume at which the phone
		 * should ring for this alert.
		 */		
		public static final String COLUMN_VOLUME = "VOLUME";
	}
	
	/** Contract for the table that stores information about an individual
	 * alert's (call or text) contacts
	 */		
	public static abstract class RuleContactEntry implements BaseColumns {
		
		/** Name for the column that stores the ID for the associated alert
		 * rule
		 */
		public static final String COLUMN_ALERT_RULE_ID = "ALERT_RULE_ID";
		
		/** Name for the column that contains the lookup value for the contact
		 * in the phone's address book.
		 */
		public static final String COLUMN_LOOKUP = "LOOKUP";
		
		/** Name for the column that specifies whether this row is for a
		 * blacklisted or whitelisted contact.
		 */
		public static final String COLUMN_LIST = "LIST";
	}
	
	/** Value for the entry that corresponds to a rule applying to all callers.
	 * This applies to the RULE and not the CONTACT. */
	public static final int ENTRY_FILTER_BY_EVERYONE = 0;
	
	/** Value for the entry that corresponds to a rule applying to allowed callers
	 * only. This applies to the RULE and not the CONTACT.
	 */
	public static final int ENTRY_FILTER_BY_ALLOWED_ONLY = 1;
	
	/** Value for the entry that corresponds to a rule applying to ignored callers
	 * only. This applies to the RULE and not the CONTACT.
	 */
	public static final int ENTRY_FILTER_BY_BLOCKED_IGNORED = 2;
	
	/** Value for the entry that corresponds to a contact being an allowed one. This
	 * applies to the CONTACT and not the RULE.
	 */
	public static final int ENTRY_LIST_ALLOW_LIST = 1;
	
	/** Value for the entry that corresponds to a contact being a blocked one. This
	 * applies to the CONTACT and not the RULE.
	 */
	public static final int ENTRY_LIST_BLOCK_LIST = 2;
	
	/** Converts a boolean value that's been saved as an integer in the database
	 * back to a boolean
	 * @param x the database value
	 * @return <code>false</code> if x is 0; <code>true</code> otherwise
	 */
	public static boolean intToBoolean(int x) {
		return (x == 0) ? false : true;
	}	
}
