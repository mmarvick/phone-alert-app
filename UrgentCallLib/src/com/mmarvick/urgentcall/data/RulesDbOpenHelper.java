package com.mmarvick.urgentcall.data;

import com.mmarvick.urgentcall.data.RulesDbContract.RulesEntry;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RulesDbOpenHelper extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 6;
	public static final String DATABASE_FILE = "Rules.db";
	
	public static final String TEXT_TYPE = " TEXT";
	public static final String INTEGER_TYPE = " INTEGER";
	public static final String SEPARATOR = ", ";
	
	public static final String SQL_CREATE_ENTRIES = 
			"CREATE TABLE " + RulesEntry.TABLE_NAME + " (" +
					RulesEntry._ID + " INTEGER PRIMARY KEY," +
					RulesEntry.COLUMN_NAME_CONTACT_LOOKUP + TEXT_TYPE + SEPARATOR +
					RulesEntry.RC_STATE + INTEGER_TYPE + SEPARATOR +
					RulesEntry.SINGLE_CALL_STATE + INTEGER_TYPE + SEPARATOR +
					RulesEntry.MSG_STATE + INTEGER_TYPE +					
					")";
	
	public static final String SQL_DELETE_TABLE =
			"DROP TABLE " + RulesEntry.TABLE_NAME;
	
	public static final String SQL_ADD_SINGLE_CALL_ON =
			"ALTER TABLE " + RulesEntry.TABLE_NAME + " ADD " +
					RulesEntry.SINGLE_CALL_STATE + INTEGER_TYPE;
	
	public static final String SQL_ADD_MSG_ON =
			"ALTER TABLE " + RulesEntry.TABLE_NAME + " ADD " +
					RulesEntry.MSG_STATE + INTEGER_TYPE;	
	
	public RulesDbOpenHelper(Context context) {
		super(context, DATABASE_FILE, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drops the whole table and start from scratch. Probably nobody is using this type.
		if (oldVersion <= 4) {
			db.execSQL(SQL_DELETE_TABLE);
			onCreate(db);
		}
		
		// Add msg_state and single_call_state
		if (oldVersion <= 5) {
			db.execSQL(SQL_ADD_SINGLE_CALL_ON);
			db.execSQL(SQL_ADD_MSG_ON);
		}
		
	}

}
