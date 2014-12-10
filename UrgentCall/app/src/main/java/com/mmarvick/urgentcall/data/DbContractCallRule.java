package com.mmarvick.urgentcall.data;

/** Contract for the tables that store information about call rules */
public class DbContractCallRule extends DbContract {
	
	/** Contract for the table that stores information about an individual
	 * call rule
	 */
	public static class CallRuleEntry extends RuleEntry {
		
		/** Name for the table that stores information about an individual
		 * call rule
		 */
		public static final String TABLE_NAME = "CALL_RULE";
		
		/** Name for the column that stores the quantity of calls that must
		 * be received to trigger an alarm.
		 */
		public static final String COLUMN_CALL_QTY = "CALL_QTY";
		
		/** Name for the column that stores the number of minutes that calls
		 * must be received within to trigger an alarm.
		 */
		public static final String COLUMN_CALL_TIME = "CALL_TIME";
		
		
		
	}
	
	/** Contract for the table that stores information about contacts for
	 * a call rule */	
	public static abstract class CallRuleContactEntry extends RuleContactEntry {
		
		/** Name for the table that stores information about contacts involved
		 * in call rules
		 */
		public static final String TABLE_NAME = "CALL_RULE_CONTACT";
	}
}
