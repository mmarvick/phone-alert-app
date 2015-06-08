package com.mmarvick.urgentcall.data.text;

import com.mmarvick.urgentcall.data.text.DbContractTextRule.TextRuleContactEntry;
import com.mmarvick.urgentcall.data.text.DbContractTextRule.TextRuleEntry;
import com.mmarvick.urgentcall.data.text.DbContractTextRule.TextRulePhraseEntry;
import com.mmarvick.urgentcall.data.base.DbOpenHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/** Used to open a connection to a database holding tables pertaining to text alerts */
public class DbOpenHelperText extends DbOpenHelper {

	/** The text alert database filename */
	public static final String DATABASE_FILE = "TextRules.db";
	
	/** The text alert rule table name */
	public static final String RULE_TABLE_NAME = TextRuleEntry.TABLE_NAME;
	
	/** The text alert rule contact table name */
	public static final String RULE_CONTACT_TABLE_NAME = TextRuleContactEntry.TABLE_NAME;
	
	/** The text alert rule phrase table name */
	public static final String RULE_PHRASE_TABLE_NAME = TextRulePhraseEntry.TABLE_NAME;	
	
	/** SQLite column definitions for the text rule phrase table */
	public static final String CREATE_RULE_PHRASE_TABLE_COLUMNS = 
			TextRulePhraseEntry._ID + " INTEGER PRIMARY KEY" + SEPARATOR +
			TextRulePhraseEntry.COLUMN_ALERT_RULE_ID + INTEGER_TYPE + NOT_NULL + SEPARATOR +
			TextRulePhraseEntry.COLUMN_TEXT_PHRASE + TEXT_TYPE + NOT_NULL;	
	
	/** Constructor for text alert database open helper
	 * 
	 * @param context the context
	 */	
	public DbOpenHelperText(Context context) {
		super(context, DATABASE_FILE);
	}
	
	/** Automatically called when constructing the text alert database tables
	 * for the first time. Creates the text rule table, rule contact table,
	 * rule phrase table, and initial entry in the rule table.
	 * @param db the database
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		super.onCreate(db);
		createRulePhraseTable(db);
		//TextAlert textAlert = new TextAlert();
        //TextAlertStore.getInstance(mContext).addAlert(mContext, textAlert);
	}	
	
	private void createRulePhraseTable(SQLiteDatabase db) {
		String tableName = getRulePhraseTableName();
		String columns = CREATE_RULE_PHRASE_TABLE_COLUMNS;
		createTable(db, tableName, columns);
	}

	/** {@inheritDoc} */
	protected String createRemainingRuleColumns() {
		String sqlCreateColumns = SEPARATOR + 
				TextRuleEntry.COLUMN_ALERT_DURATION + INTEGER_TYPE + NOT_NULL;
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
	
	/** Returns the name for the rule phrase table
	 * @return the rule phrase table name
	 */	
	protected String getRulePhraseTableName() {
		return RULE_PHRASE_TABLE_NAME;
	}	

}
