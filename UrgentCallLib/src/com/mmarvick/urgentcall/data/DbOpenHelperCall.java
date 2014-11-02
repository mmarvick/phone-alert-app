package com.mmarvick.urgentcall.data;

import com.mmarvick.urgentcall.data.DbContractCallRule.CallRuleContactEntry;
import com.mmarvick.urgentcall.data.DbContractCallRule.CallRuleEntry;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DbOpenHelperCall extends DbOpenHelper {
	
	public static final String DATABASE_FILE = "CallRules.db";
	public static final String RULE_TABLE_NAME = CallRuleEntry.TABLE_NAME;
	public static final String RULE_CONTACT_TABLE_NAME = CallRuleContactEntry.TABLE_NAME;
	
	public static final String RULE_FIRST_NAME = "Repeated Call Alert";
	
	public DbOpenHelperCall(Context context) {
		super(context, DATABASE_FILE);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		super.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		super.onUpgrade(db, oldVersion, newVersion);
		
	}
	
	protected String createRemainingRuleColumns() {
		String sqlCreateColumns = SEPARATOR + 
				CallRuleEntry.COLUMN_CALL_QTY + INTEGER_TYPE + NOT_NULL + SEPARATOR +
				CallRuleEntry.COLUMN_CALL_TIME + INTEGER_TYPE + NOT_NULL;
		return sqlCreateColumns;
	}
	
	protected void createRemainingFirstRule(ContentValues ruleValues) {
		ruleValues.put(CallRuleEntry.COLUMN_CALL_QTY, 3);
		ruleValues.put(CallRuleEntry.COLUMN_CALL_TIME, 15);	
	}
	
	protected String getDatabaseFile() {
		return DATABASE_FILE;
	}
	
	protected String getRuleTableName() {
		return RULE_TABLE_NAME;
	}
	
	protected String getRuleContactTableName() {
		return RULE_CONTACT_TABLE_NAME;
	}
	
	protected String getFirstRuleName() {
		return RULE_FIRST_NAME;
	}
}
