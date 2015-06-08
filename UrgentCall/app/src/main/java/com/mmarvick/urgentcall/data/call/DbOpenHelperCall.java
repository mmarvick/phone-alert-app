package com.mmarvick.urgentcall.data.call;

import com.mmarvick.urgentcall.data.call.DbContractCallRule.CallRuleContactEntry;
import com.mmarvick.urgentcall.data.call.DbContractCallRule.CallRuleEntry;
import com.mmarvick.urgentcall.data.base.DbOpenHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/** Used to open a connection to a database holding tables pertaining to call alerts */
public class DbOpenHelperCall extends DbOpenHelper {
	
	/** The call alert database filename */
	public static final String DATABASE_FILE = "CallRules.db";
	
	/** The call alert rule table name */
	public static final String RULE_TABLE_NAME = CallRuleEntry.TABLE_NAME;
	
	/** The call alert rule contact table name */
	public static final String RULE_CONTACT_TABLE_NAME = CallRuleContactEntry.TABLE_NAME;

	/** Constructor for call alert database open helper
	 * 
	 * @param context the context
	 */	
	public DbOpenHelperCall(Context context) {
		super(context, DATABASE_FILE);
	}
	
	/** Automatically called when constructing the call alert database tables
	 * for the first time. Creates the call rule table, rule contact table,
	 * and initial entry in the rule table.
	 * @param db the database
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		super.onCreate(db);
		new CallAlert(mContext, db, true);
	}		
	
	/** {@inheritDoc} */
	protected String createRemainingRuleColumns() {
		String sqlCreateColumns = SEPARATOR + 
				CallRuleEntry.COLUMN_CALL_QTY + INTEGER_TYPE + NOT_NULL + SEPARATOR +
				CallRuleEntry.COLUMN_CALL_TIME + INTEGER_TYPE + NOT_NULL;
		return sqlCreateColumns;
	}
	
	/** {@inheritDoc} */
	protected String getDatabaseFile() {
		return DATABASE_FILE;
	}
	
	/** {@inheritDoc} */
	protected String getRuleTableName() {
		return RULE_TABLE_NAME;
	}
	
	/** {@inheritDoc} */
	protected String getRuleContactTableName() {
		return RULE_CONTACT_TABLE_NAME;
	}
	
}
