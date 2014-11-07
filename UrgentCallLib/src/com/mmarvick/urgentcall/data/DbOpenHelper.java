package com.mmarvick.urgentcall.data;

import com.mmarvick.urgentcall.data.DbContract.RuleContactEntry;
import com.mmarvick.urgentcall.data.DbContract.RuleEntry;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** Used to open a connection to a database holding tables pertaining to a alert type */
public abstract class DbOpenHelper extends SQLiteOpenHelper {
	
	/** The context */
	protected Context mContext;
	
	/** The schema version */
	public static final int DATABASE_VERSION = 1;
	
	/** SQLite text type with leading space */
	public static final String TEXT_TYPE = " TEXT";
	
	/** SQLite integer type with leading space */
	public static final String INTEGER_TYPE = " INTEGER";
	
	/** SQLite not null with leading space */
	public static final String NOT_NULL = " NOT NULL";
	
	/** SQLite separator between columns */
	public static final String SEPARATOR = ", ";
	
	/** SQLite column definitions that are common to rule databases for all
	 * alert types */
	public static final String CREATE_RULE_TABLE_COLUMNS =
			RuleEntry._ID + " INTEGER PRIMARY KEY" + SEPARATOR +
			RuleEntry.COLUMN_TITLE + TEXT_TYPE + NOT_NULL + SEPARATOR +
			RuleEntry.COLUMN_ON_STATE + INTEGER_TYPE + NOT_NULL + SEPARATOR +
			RuleEntry.COLUMN_FILTER_BY + INTEGER_TYPE + NOT_NULL + SEPARATOR +
			RuleEntry.COLUMN_RING + INTEGER_TYPE + NOT_NULL + SEPARATOR +
			RuleEntry.COLUMN_VIBRATE + INTEGER_TYPE + NOT_NULL + SEPARATOR +
			RuleEntry.COLUMN_TONE + TEXT_TYPE + NOT_NULL + SEPARATOR +
			RuleEntry.COLUMN_VOLUME + INTEGER_TYPE + NOT_NULL;
	
	/** SQLite column definitions that are common to rule contact databases for
	 * all alert types
	 */
	public static final String CREATE_RULE_CONTACT_TABLE_COLUMNS = 
			RuleContactEntry._ID + " INTEGER PRIMARY KEY" + SEPARATOR +
			RuleContactEntry.COLUMN_ALERT_RULE_ID + INTEGER_TYPE + NOT_NULL + SEPARATOR +
			RuleContactEntry.COLUMN_LOOKUP + TEXT_TYPE + NOT_NULL + SEPARATOR +
			RuleContactEntry.COLUMN_LIST + INTEGER_TYPE + NOT_NULL;

	/** Constructor for database open helper
	 * 
	 * @param context the context
	 * @param databaseFile the name of the file with the database
	 */
	public DbOpenHelper(Context context, String databaseFile) {
		super(context, databaseFile, null, DATABASE_VERSION);
		mContext = context;
	}

	/** Automatically called when constructing the database tables for the first time.
	 * Creates the rule table, rule contact table, and initial entry in the
	 * rule table.
	 * @param db the database
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		createRuleTable(db);
		createRuleContactTable(db);
	}

	/** Automatically called when upgrading the database schema 
	 * @param db the database
	 * @param oldVersion the schema version being updated from
	 * @param newVersion the schema version being updated to
	 * */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	/** Called to create the rule table
	 * @param db the database
	 * */
	private void createRuleTable(SQLiteDatabase db) {
		String tableName = getRuleTableName();
		String columns = CREATE_RULE_TABLE_COLUMNS + createRemainingRuleColumns();
		createTable(db, tableName, columns);
	}
	
	/** Called to create the rule contact table
	 * @param db the database
	 */
	private void createRuleContactTable(SQLiteDatabase db) {
		String tableName = getRuleContactTableName();
		String columns = CREATE_RULE_CONTACT_TABLE_COLUMNS;
		createTable(db, tableName, columns);
	}
	
	/** Helper function called to create any table
	 * @param db the database
	 * @param tableName the table name
	 * @param columns the SQLite statement for the column definitions
	 */
	protected void createTable(SQLiteDatabase db, String tableName, String columns) {
		String sqlCreateTable =
				"CREATE TABLE " + tableName + " (" +
						columns + ")";
		db.execSQL(sqlCreateTable);
	}	
	
	/** Returns the database file name
	 * @return the database file name
	 */
	protected abstract String getDatabaseFile();
	
	/** Returns the name for the rule table
	 * @return the rule table name
	 */
	protected abstract String getRuleTableName();
	
	/** Returns the name for the rule contact table
	 * @return the rule contact table name
	 */
	protected abstract String getRuleContactTableName();
	
	/** Returns the definitions for any rule type specific (not for both
	 * call and text alert) column names
	 * @return the SQLite statement for the column definitions
	 */
	protected abstract String createRemainingRuleColumns();

}
