package com.mmarvick.urgentcall.data.text;

import android.provider.BaseColumns;

import com.mmarvick.urgentcall.data.base.DbContract;

/** Contract for the tables that store information about text rules */
public class DbContractTextRule extends DbContract {

	/** Contract for the table that stores information about an individual
	 * text rule
	 */
	public static class TextRuleEntry extends RuleEntry {
		
		/** Name for the table that stores information about an individual
		 * text rule
		 */
		public static final String TABLE_NAME = "TEXT_RULE";
		
		/** Name for the column that stores the length of time the text
		 * alert should last for
		 */
		public static final String COLUMN_ALERT_DURATION = "ALERT_DURATION";	
		
	}	
	
	/** Contract for the table that stores information about contacts for
	 * a text rule */	
	public static abstract class TextRuleContactEntry extends RuleContactEntry {
		
		/** Name for the table that stores information about contacts involved
		 * in text rules
		 */
		public static final String TABLE_NAME = "TEXT_RULE_CONTACT";
	}	
	
	public static abstract class TextRulePhraseEntry implements BaseColumns {
		
		/** Name for the table that stores information about an individual
		 * text rule
		 */
		public static final String TABLE_NAME = "TEXT_PHRASE";		
		
		/** Name for the column that stores the ID for the associated alert
		 * rule
		 */		
		public static final String COLUMN_ALERT_RULE_ID = "ALERT_RULE_ID";
		
		/** Name for the column that stores the keyword for the associated alert
		 * rule
		 */		
		public static final String COLUMN_TEXT_PHRASE = "TEXT_PHRASE";		
	}
	
}
