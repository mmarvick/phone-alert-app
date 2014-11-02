package com.mmarvick.urgentcall.data;

import com.mmarvick.urgentcall.data.DbContract.RuleContactEntry;
import com.mmarvick.urgentcall.data.DbContract.RuleEntry;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class DbOpenHelper extends SQLiteOpenHelper {
	
	public Context mContext;
	
	public static final int DATABASE_VERSION = 1;
	
	public static final String TEXT_TYPE = " TEXT";
	public static final String INTEGER_TYPE = " INTEGER";
	public static final String NOT_NULL = " NOT NULL";
	public static final String SEPARATOR = ", ";
	
	public static final String CREATE_RULE_TABLE_COLUMNS =
			RuleEntry._ID + " INTEGER PRIMARY KEY" + SEPARATOR +
			RuleEntry.COLUMN_TITLE + TEXT_TYPE + NOT_NULL + SEPARATOR +
			RuleEntry.COLUMN_ON_STATE + INTEGER_TYPE + NOT_NULL + SEPARATOR +
			RuleEntry.COLUMN_FILTER_BY + INTEGER_TYPE + NOT_NULL + SEPARATOR +
			RuleEntry.COLUMN_RING + INTEGER_TYPE + NOT_NULL + SEPARATOR +
			RuleEntry.COLUMN_VIBRATE + INTEGER_TYPE + NOT_NULL + SEPARATOR +
			RuleEntry.COLUMN_TONE + TEXT_TYPE + SEPARATOR +
			RuleEntry.COLUMN_VOLUME + INTEGER_TYPE + NOT_NULL;
	
	public static final String CREATE_RULE_CONTACT_TABLE_COLUMNS = 
			RuleContactEntry._ID + " INTEGER PRIMARY KEY" + SEPARATOR +
			RuleContactEntry.COLUMN_ALERT_RULE_ID + INTEGER_TYPE + NOT_NULL + SEPARATOR +
			RuleContactEntry.COLUMN_LOOKUP + TEXT_TYPE + NOT_NULL + SEPARATOR +
			RuleContactEntry.COLUMN_LIST + INTEGER_TYPE + NOT_NULL;

	public DbOpenHelper(Context context, String name) {
		super(context, name, null, DATABASE_VERSION);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createRuleTable(db);
		createRuleContactTable(db);
		createFirstRule(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	private void createRuleTable(SQLiteDatabase db) {
		String tableName = getRuleTableName();
		String columns = CREATE_RULE_TABLE_COLUMNS + createRemainingRuleColumns();
		createTable(db, tableName, columns);
	}
	
	private void createRuleContactTable(SQLiteDatabase db) {
		String tableName = getRuleContactTableName();
		String columns = CREATE_RULE_CONTACT_TABLE_COLUMNS;
		createTable(db, tableName, columns);
	}
	
	protected void createTable(SQLiteDatabase db, String tableName, String columns) {
		String sqlCreateTable =
				"CREATE TABLE " + tableName + " (" +
						columns + ")";
		db.execSQL(sqlCreateTable);
	}	
	
	protected abstract String getDatabaseFile();
	protected abstract String getRuleTableName();
	protected abstract String getRuleContactTableName();
	protected abstract String createRemainingRuleColumns();
	protected abstract void createFirstRule(SQLiteDatabase db);

}
