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

/** Object that represents one call alert rule, keeps track of
 * information associated with it, and saves changes to the database when updated.
 */
public class AlertCall extends Alert {
	
	/** The call threshold for a call alert */
	private int mCallQty;
	
	/** The time span within which calls must be made to trigger a call alert
	 * in minutes */
	private int mCallTime;
	
	/** Constructor for an AlertCall.
	 * 
	 * @param context (required) context of the call alert
	 * @param id (required) row number of the call alert
	 * @throws IndexOutOfBoundsException no row exists in the call alert table
	 * with that id
	 */
	public AlertCall(Context context, int id) {
		super(context, id);
	}
	
	/** Gets the call quantity threshold for the alert
	 * @return the number of calls needed to trigger an alert
	 */
	public int getCallQty() {
		return mCallQty;
	}
	
	/** Sets the call quantity threshold for the alert, and saves it to the
	 * database
	 * @param callQty the number of calls needed to trigger an alert
	 */
	public void setCallQty(int callQty) {
		ContentValues newValues = new ContentValues();
		newValues.put(CallRuleEntry.COLUMN_CALL_QTY, callQty);
		updateRuleTable(newValues);		
		mCallQty = callQty;	
	}
	
	/** Gets the time span within which calls must be made to trigger a call alert
	 * @return the time span in minutes
	 */
	public int getCallTime() {
		return mCallTime;
	}

	/** Sets the time span within which calls must be made to trigger a call alert,
	 * and updates the database
	 * @param callTime the time span in minutes
	 */	
	public void setCallTime(int callTime) {
		ContentValues newValues = new ContentValues();
		newValues.put(CallRuleEntry.COLUMN_CALL_TIME, callTime);
		updateRuleTable(newValues);		
		mCallQty = callTime;	
	}	

	/** Checks to see if all the criteria of this alert have been met by the
	 * contact that's calling.
	 * @param phoneNumber the phone number that is calling
	 * @return <code>true</code> if all criteria are met;
	 * <code>false</code> otherwise
	 */
	public boolean shouldAlert(String phoneNumber) {
		return (getOnState() && meetsCallQuantityCriteria(phoneNumber) && meetsContactCriteria(phoneNumber));
	}
	
	/** Checks to see if a contact fulfills the call quantity criteria of the
	 * alert. The number of calls received from a contact within the time
	 * span of the alert must be greater or equal to the threshold value.
	 * @param phoneNumber the phone number of the contact
	 * @return <code>true<code> if the call quantity criteria is met;
	 * <code>false</code> if not
	 */	
	private boolean meetsCallQuantityCriteria(String phoneNumber) {
		String time = "" + ((new Date()).getTime() - mCallTime * 60 * 1000);
		String selection = CallLog.Calls.NUMBER + " = ?";
		selection += " AND " + CallLog.Calls.DATE + "> ?";
		String[] selectors = {phoneNumber, time};
		Cursor calls = mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, selection, selectors, null);
		int numCalls = calls.getCount();
		calls.close();
		numCalls++; // need to do this because the incoming call hasn't been registered yet
		Log.e("CALL ALERT", "Num calls: " + numCalls);
		return numCalls >= mCallQty;	
	}
	
	/** Loads information about the alert from the database that is specific
	 * to call alerts (call quantity threshold and time span)
	 * @param ruleCursor the Cursor that both contains and is currently
	 * positioned at the table row to read alert data from
	 */
	protected void loadRemainingRuleData(Cursor ruleCursor) {
		mCallQty = ruleCursor.getInt(ruleCursor.getColumnIndex(CallRuleEntry.COLUMN_CALL_QTY));
		mCallTime = ruleCursor.getInt(ruleCursor.getColumnIndex(CallRuleEntry.COLUMN_CALL_TIME));
	}
	
	/** {@inheritDoc} */
	protected String getTableName() {
		return CallRuleEntry.TABLE_NAME;
	}
	
	/** {@inheritDoc} */
	protected String getContactTableName() {
		return CallRuleContactEntry.TABLE_NAME;
	}
	
	/** {@inheritDoc} */
	protected SQLiteDatabase getReadableDatabase() {
		DbOpenHelperCall dbOpenHelper = new DbOpenHelperCall(mContext);
		return dbOpenHelper.getReadableDatabase();
	}
	
	/** {@inheritDoc} */
	protected SQLiteDatabase getWritableDatabase() {
		DbOpenHelperCall dbOpenHelper = new DbOpenHelperCall(mContext);
		return dbOpenHelper.getWritableDatabase();		
	}
}
