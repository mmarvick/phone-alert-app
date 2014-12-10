package com.mmarvick.urgentcall.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	/** A string identifying the alert type of a call alert */
	public static final String ALERT_TYPE = "CALL_ALERT";
	
	/** The default name of a call alert */
	public static final String ALERT_CALL_TYPE_NAME = "Call Alert";
	
	/** The call threshold for a call alert */
	private int mCallQty;
	
	/** The time span within which calls must be made to trigger a call alert
	 * in minutes */
	private int mCallTime;
	
	/** Returns a list of all call alerts in the database
	 * @param context the context
	 * @return a list of AlertCall objects representing each currently stored
	 * call alert
	 */
	public static List<AlertCall> getAlerts(Context context) {
		DbOpenHelperCall dbOpenHelper = new DbOpenHelperCall(context);
		SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
		List<AlertCall> callAlerts = new ArrayList<AlertCall>();
		
		Cursor ruleCursor = database.query(CallRuleEntry.TABLE_NAME,
				new String[] {CallRuleEntry._ID},
				null, null, null, null, null);
		
		ruleCursor.moveToFirst();
		while (!ruleCursor.isAfterLast()) {
			long alertId = ruleCursor.getInt(ruleCursor.getColumnIndex(CallRuleEntry._ID));
			callAlerts.add(new AlertCall(context, alertId));
			ruleCursor.moveToNext();
		}
		
		ruleCursor.close();
		
		return callAlerts;
	}	

	/** Constructor for an AlertCall not currently in the database, with all
	 * initial values generated as defaults. Also adds the Alert to the
	 * database.
	 * 
	 * @param context the current context
	 */
	public AlertCall(Context context) {
		super(context);
	}

	/** Constructor for an AlertCall not currently in the database, with all
	 * initial values generated as defaults. Also adds the Alert to the
	 * database.
	 * 
	 * @param context the current context
	 * @param db a writable database for call rules
	 * @param isInitial <code>true</code> if is the initial rule created when
	 * the application runs for the first time; <code>false</code> if not 
	 */	
	public AlertCall(Context context, SQLiteDatabase db, boolean isInitial) {
		super(context, db, isInitial);
	}	
	
	/** Constructor for an AlertCall already in the database..
	 * 
	 * @param context (required) context of the call alert
	 * @param id (required) row number of the call alert
	 * @throws IndexOutOfBoundsException no row exists in the call alert table
	 * with that id
	 */
	public AlertCall(Context context, long id) {
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
		mCallTime = callTime;	
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
	protected void loadRemainingRuleData(SQLiteDatabase database, Cursor ruleCursor) {
		mCallQty = ruleCursor.getInt(ruleCursor.getColumnIndex(CallRuleEntry.COLUMN_CALL_QTY));
		mCallTime = ruleCursor.getInt(ruleCursor.getColumnIndex(CallRuleEntry.COLUMN_CALL_TIME));
	}
	
	/** Initializes the remaining information that is specific to call alerts,
	 * and stores into the ContentValues as key-value pairs to be saved
	 * to the database.
	 * @param ruleValues the repository of key-value pairs to save in the database
	 */		
	protected void initializeAndStoreRemainingRuleData(ContentValues ruleValues) {
		mCallQty = 3;
		mCallTime = 15;
		ruleValues.put(CallRuleEntry.COLUMN_CALL_QTY, mCallQty);
		ruleValues.put(CallRuleEntry.COLUMN_CALL_TIME, mCallTime);
	}
	
	/** {@inheritDoc} */
	protected void performRemainingDropCommands(SQLiteDatabase db) {
		// nothing needed
	}

	/** {@inheritDoc} */
	protected String getAlertTypeName() {
		return ALERT_CALL_TYPE_NAME;
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
	
	/** {@inheritDoc} */
	public String getAlertType() {
		return ALERT_TYPE;
	}
}
