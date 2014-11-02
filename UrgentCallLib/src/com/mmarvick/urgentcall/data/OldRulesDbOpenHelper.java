package com.mmarvick.urgentcall.data;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.data.DbContractCallRule.CallRuleEntry;
import com.mmarvick.urgentcall.data.OldDbContractDatabase.RulesEntryOld;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class OldRulesDbOpenHelper extends SQLiteOpenHelper {
	public Context mContext;
	
	public static final int DATABASE_VERSION = 8;
	public static final String DATABASE_FILE = "Rules.db";
	
	public void updateToNewDataScheme(SQLiteDatabase oldDb) {
		Log.e("UC", "UPDATING SCHEM A");
		
		/* RC ALERT */
		
		// Find the alert, or create a new one if there are none
		DbOpenHelperCall dbOpenHelperCall = new DbOpenHelperCall(mContext);
		SQLiteDatabase callDb = dbOpenHelperCall.getReadableDatabase();
		Cursor callDbCursor = callDb.query(DbOpenHelperCall.RULE_TABLE_NAME, new String[]{CallRuleEntry._ID}, null, null, null, null, null);
		callDbCursor.moveToFirst();
		AlertCall alertCall;
		if (callDbCursor.isAfterLast()) {
			alertCall = new AlertCall(mContext);
		} else {
			long id = callDbCursor.getLong(callDbCursor.getColumnIndex(CallRuleEntry._ID));
			alertCall = new AlertCall(mContext, id);
		}
		callDb.close();
		callDbCursor.close();
		
		// Update the alert
		updateAlert(oldDb, alertCall, RulesEntryOld.RC_STATE);
		alertCall.setCallQty(OldPrefHelper.getRepeatedCallQty(mContext));
		alertCall.setCallTime(OldPrefHelper.getRepeatedCallMins(mContext));
		
		
		/* SC ALERT */
		AlertCall alertCallSingle = new AlertCall(mContext);
		updateAlert(oldDb, alertCallSingle, RulesEntryOld.SC_STATE);
		if (alertCallSingle.getAllowedContacts().isEmpty()) {
			alertCallSingle.delete();
		} else {
			alertCallSingle.setCallQty(1);
			alertCallSingle.setTitle("Single Call Alert");
		}
		
	}
	
	private void updateAlert(SQLiteDatabase oldDb, Alert alert, String type) {
		
		int oldState = OldPrefHelper.getState(mContext, type);
		int backupState = OldPrefHelper.getBackupState(mContext, type);
		int oldStyleFilterState = (oldState == Constants.URGENT_CALL_STATE_OFF) ? backupState : oldState;
		
		alert.setOnState(oldState != Constants.URGENT_CALL_STATE_OFF);
		
		switch (oldStyleFilterState) {
			case (Constants.URGENT_CALL_STATE_WHITELIST):
				alert.setFilterBy(DbContract.ENTRY_FILTER_BY_ALLOWED_ONLY);
				break;
			case (Constants.URGENT_CALL_STATE_BLACKLIST):
				alert.setFilterBy(DbContract.ENTRY_FILTER_BY_BLOCKED_IGNORED);
				break;
			default:
				alert.setFilterBy(DbContract.ENTRY_FILTER_BY_EVERYONE);
				break;
		}
		
		String oldAlertHow = OldPrefHelper.getMessageHow(mContext, type);
		
		if (oldAlertHow.equals(Constants.ALERT_HOW_VIBE)) {
			alert.setRing(false);
			alert.setVibrate(true);
		} else if (oldAlertHow.equals(Constants.ALERT_HOW_RING_AND_VIBE)) {
			alert.setRing(true);
			alert.setVibrate(true);
		} else {
			alert.setRing(true);
			alert.setVibrate(false);
		}

		alert.setVolume(OldPrefHelper.getMessageVolumeIntValue(mContext, type));
		
		alert.setTone(OldPrefHelper.getMessageSoundString(mContext, type));
		
		Cursor callers = oldDb.query(RulesEntryOld.TABLE_NAME,
				new String[] {RulesEntryOld.COLUMN_NAME_CONTACT_LOOKUP, type},
				type + " IS NOT NULL", null, null, null, null);
		callers.moveToFirst();
		while (!callers.isAfterLast()) {
			String lookup = callers.getString(callers.getColumnIndex(RulesEntryOld.COLUMN_NAME_CONTACT_LOOKUP));
			if (callers.getInt(callers.getColumnIndex(type)) == RulesEntryOld.STATE_ON) {
				alert.addContact(lookup, DbContract.ENTRY_LIST_ALLOW_LIST);
			} else {
				alert.addContact(lookup, DbContract.ENTRY_LIST_BLOCK_LIST);
			}
			callers.moveToNext();
		}
		
		callers.close();
		
	}
	
	public OldRulesDbOpenHelper(Context context) {
		super(context, DATABASE_FILE, null, DATABASE_VERSION);
		mContext = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion <= 7) {
				updateToNewDataScheme(db);
				
		}	
	}

}
