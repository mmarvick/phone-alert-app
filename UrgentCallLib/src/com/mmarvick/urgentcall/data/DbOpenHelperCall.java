package com.mmarvick.urgentcall.data;

import com.mmarvick.urgentcall.data.DbContractCallRule.CallRuleContactEntry;
import com.mmarvick.urgentcall.data.DbContractCallRule.CallRuleEntry;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelperCall extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_FILE = "CallRules.db";
	
	public static final String TEXT_TYPE = " TEXT";
	public static final String INTEGER_TYPE = " INTEGER";
	public static final String NOT_NULL = " NOT NULL";
	public static final String SEPARATOR = ", ";
	
	public static final String SQL_CREATE_CALL_RULES_TABLE =
			"CREATE TABLE " + CallRuleEntry.TABLE_NAME + " (" +
					CallRuleEntry._ID + " INTEGER PRIMARY KEY" + SEPARATOR +
					CallRuleEntry.COLUMN_TITLE + TEXT_TYPE + NOT_NULL + SEPARATOR +
					CallRuleEntry.COLUMN_ON_STATE + INTEGER_TYPE + NOT_NULL + SEPARATOR +
					CallRuleEntry.COLUMN_FILTER_BY + INTEGER_TYPE + NOT_NULL + SEPARATOR +
					CallRuleEntry.COLUMN_RING + INTEGER_TYPE + NOT_NULL + SEPARATOR +
					CallRuleEntry.COLUMN_VIBRATE + INTEGER_TYPE + NOT_NULL + SEPARATOR +
					CallRuleEntry.COLUMN_TONE + TEXT_TYPE + SEPARATOR +
					CallRuleEntry.COLUMN_VOLUME + INTEGER_TYPE + NOT_NULL + SEPARATOR +
					CallRuleEntry.COLUMN_CALL_QTY + INTEGER_TYPE + NOT_NULL + SEPARATOR +
					CallRuleEntry.COLUMN_CALL_TIME + INTEGER_TYPE + NOT_NULL +
					")";
	
	public static final String SQL_CREATE_CALL_RULES_CONTACTS_TABLE = 
			"CREATE TABLE " + CallRuleContactEntry.TABLE_NAME + " (" +
					CallRuleContactEntry._ID + " INTEGER PRIMARY KEY" + SEPARATOR +
					CallRuleContactEntry.COLUMN_ALERT_RULE_ID + INTEGER_TYPE + NOT_NULL + SEPARATOR +
					CallRuleContactEntry.COLUMN_LOOKUP + TEXT_TYPE + NOT_NULL + SEPARATOR +
					CallRuleContactEntry.COLUMN_LIST + INTEGER_TYPE + NOT_NULL +
					")";
	
	public DbOpenHelperCall(Context context) {
		super(context, DATABASE_FILE, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL(SQL_CREATE_CALL_RULES_TABLE);
		ContentValues callRulesFirstRow = new ContentValues();
		callRulesFirstRow.put(CallRuleEntry.COLUMN_TITLE, "Repeated Call Alert");
		callRulesFirstRow.put(CallRuleEntry.COLUMN_ON_STATE, true);
		callRulesFirstRow.put(CallRuleEntry.COLUMN_FILTER_BY, DbContractCallRule.ENTRY_FILTER_BY_EVERYONE);
		callRulesFirstRow.put(CallRuleEntry.COLUMN_RING, true);
		callRulesFirstRow.put(CallRuleEntry.COLUMN_VIBRATE, true);
		callRulesFirstRow.put(CallRuleEntry.COLUMN_VOLUME, 1000);
		callRulesFirstRow.put(CallRuleEntry.COLUMN_CALL_QTY, 3);
		callRulesFirstRow.put(CallRuleEntry.COLUMN_CALL_TIME, 15);
		db.insert(CallRuleEntry.TABLE_NAME, null, callRulesFirstRow);
		
		db.execSQL(SQL_CREATE_CALL_RULES_CONTACTS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		
	}	
}
