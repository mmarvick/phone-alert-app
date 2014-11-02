package com.mmarvick.urgentcall.data;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.CallLog;
import android.util.Log;

import com.mmarvick.urgentcall.data.DbContractCallRule.CallRuleContactEntry;
import com.mmarvick.urgentcall.data.DbContractCallRule.CallRuleEntry;

public class AlertCall extends Alert {
	private final String mTableName = CallRuleEntry.TABLE_NAME;
	private final String mContactTableName = CallRuleContactEntry.TABLE_NAME;	
	
	private int mCallQty;
	private int mCallTime;
	
	public AlertCall(Context context, int id) {
		super(context, id);
	}
	
	public int getCallQty() {
		return mCallQty;
	}
	
	public void setCallQty(int callQty) {
		ContentValues newValues = new ContentValues();
		newValues.put(CallRuleEntry.COLUMN_CALL_QTY, callQty);
		updateRuleTable(newValues);		
		mCallQty = callQty;	
	}
	
	public int getCallTime() {
		return mCallTime;
	}
	
	public void setCallTime(int callTime) {
		ContentValues newValues = new ContentValues();
		newValues.put(CallRuleEntry.COLUMN_CALL_TIME, callTime);
		updateRuleTable(newValues);		
		mCallQty = callTime;	
	}	
	
	public boolean shouldAlert(String phoneNumber) {
		Log.e("CALL ALERT", "Testing...");
		Log.e("CALL ALERT", "On: " + getOnState());
		Log.e("CALL ALERT", "Enough Calls: " + enoughCallsReceived(phoneNumber));
		Log.e("CALL ALERT", "Matches Number: " + meetsContactCriteria(phoneNumber));
		return (getOnState() && enoughCallsReceived(phoneNumber) && meetsContactCriteria(phoneNumber));
	}
	
	private boolean enoughCallsReceived(String phoneNumber) {
		String time = "" + ((new Date()).getTime() - mCallTime * 60 * 1000);
		String selection = CallLog.Calls.NUMBER + " = ?";
		selection += " AND " + CallLog.Calls.DATE + "> ?";
		String[] selectors = {phoneNumber, time};
		Cursor calls = mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, selection, selectors, null);
		int numCalls = calls.getCount();
		numCalls++; // need to do this because the incoming call hasn't been registered yet
		Log.e("CALL ALERT", "Num calls: " + numCalls);
		return numCalls >= mCallQty;	
	}
	
	protected void loadRemainingRuleData(Cursor ruleCursor) {
		mCallQty = ruleCursor.getInt(ruleCursor.getColumnIndex(CallRuleEntry.COLUMN_CALL_QTY));
		mCallTime = ruleCursor.getInt(ruleCursor.getColumnIndex(CallRuleEntry.COLUMN_CALL_TIME));
	}
	
	
	protected String getTableName() {
		return mTableName;
	}
	
	protected String getContactTableName() {
		return mContactTableName;
	}
	
	protected SQLiteDatabase getReadableDatabase() {
		DbOpenHelperCall dbOpenHelper = new DbOpenHelperCall(mContext);
		return dbOpenHelper.getReadableDatabase();
	}
	
	protected SQLiteDatabase getWritableDatabase() {
		DbOpenHelperCall dbOpenHelper = new DbOpenHelperCall(mContext);
		return dbOpenHelper.getWritableDatabase();		
	}
}
