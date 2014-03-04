package com.mmarvick.phonealert;

import com.mmarvick.phonealert.RulesDbContract.RulesEntry;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RulesDbOpenHelper extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 3;
	public static final String DATABASE_FILE = "Rules.db";
	
	public static final String TEXT_TYPE = " TEXT";
	public static final String INTEGER_TYPE = " INTEGER";
	public static final String SEPARATOR = ", ";
	
	public static final String SQL_CREATE_ENTRIES = 
			"CREATE TABLE " + RulesEntry.TABLE_NAME + " (" +
					RulesEntry._ID + " INTEGER PRIMARY KEY," +
					RulesEntry.COLUMN_NAME_CONTACT_LOOKUP + TEXT_TYPE + SEPARATOR +
					RulesEntry.COLUMN_NAME_CALLS + INTEGER_TYPE + SEPARATOR +
					RulesEntry.COLUMN_NAME_MINS + INTEGER_TYPE + ")";
	
	public static final String SQL_CREATE_FIRST_ROW =
			"INSERT INTO " + RulesEntry.TABLE_NAME + "(" + RulesEntry._ID + SEPARATOR +
				RulesEntry.COLUMN_NAME_CONTACT_LOOKUP + SEPARATOR +
				RulesEntry.COLUMN_NAME_CALLS + SEPARATOR +
				RulesEntry.COLUMN_NAME_MINS + ")" +
				"VALUES(1, '" + RulesEntry.LOOKUP_DEFAULT + "', 3, 15)";
	
	public static final String SQL_DELETE_ENTRIES =
			"DROP TABLE " + RulesEntry.TABLE_NAME;
	
	public RulesDbOpenHelper(Context context) {
		super(context, DATABASE_FILE, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);
		db.execSQL(SQL_CREATE_FIRST_ROW);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);
	}

}
