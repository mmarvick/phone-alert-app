package com.mmarvick.urgentcall.data.call;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.CallLog;
import android.util.Log;

import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.base.Alert;

/** Object that represents one call alert rule, keeps track of
 * information associated with it, and saves changes to the database when updated.
 */
public class CallAlert extends Alert {
	/** A string identifying the alert type of a call alert */
	public static final String ALERT_TYPE = "CALL_ALERT";
	
	/** The default name of a call alert */
	public static final String ALERT_CALL_TYPE_NAME = "Call Alert";

    private int mCallQty;
    private int mCallTime;

	public CallAlert() {
		super();
        mCallQty = 3;
        mCallTime = 15;
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
		mCallTime = callTime;
	}	
	
	/** Checks to see if all the criteria of this alert have been met by the
	 * contact that's calling.
	 * @param phoneNumber the phone number that is calling
	 * @return <code>true</code> if all criteria are met;
	 * <code>false</code> otherwise
	 */
	public boolean shouldAlert(Context context, String phoneNumber) {
		return (getOnState() && meetsCallQuantityCriteria(context, phoneNumber) && meetsContactCriteria(context, phoneNumber));
	}
	
	/** Checks to see if a contact fulfills the call quantity criteria of the
	 * alert. The number of calls received from a contact within the time
	 * span of the alert must be greater or equal to the threshold value.
	 * @param phoneNumber the phone number of the contact
	 * @return <code>true<code> if the call quantity criteria is met;
	 * <code>false</code> if not
	 */	
	private boolean meetsCallQuantityCriteria(Context context, String phoneNumber) {
		String time = "" + ((new Date()).getTime() - getCallTime() * 60 * 1000);
        String incomingType = "" + CallLog.Calls.INCOMING_TYPE;
        String missedType = "" +  CallLog.Calls.MISSED_TYPE;
		String selection = CallLog.Calls.NUMBER + " = ?";
		selection += " AND " + CallLog.Calls.DATE + " > ?";
        selection += " AND (" + CallLog.Calls.TYPE + " = ?";
        selection += " OR " + CallLog.Calls.TYPE + " = ?)";
		String[] selectors = {phoneNumber, time, incomingType, missedType};
		Cursor calls = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, selection, selectors, null);
		int numCalls = calls.getCount();
		calls.close();
		numCalls++; // need to do this because the incoming call hasn't been registered yet
		Log.e("CALL ALERT", "Num calls: " + numCalls);
		return numCalls >= getCallQty();
	}

	/** {@inheritDoc} */
	protected String getAlertTypeName() {
		return ALERT_CALL_TYPE_NAME;
	}
	
	/** {@inheritDoc} */
	public String getAlertType() {
		return ALERT_TYPE;
	}

    /** {@inheritDoc} */
    public String getShareText(Context context) {
        String shareText = "";
        if (getCallQty() == 1) {
            shareText += context.getString(R.string.share_sc_1);
        } else {
            shareText += context.getString(R.string.share_rc_1) + getCallQty();
            shareText += context.getString(R.string.share_rc_2) + getCallTime();
            shareText += context.getString(R.string.share_rc_3);
        }
        shareText += context.getString(R.string.share_app_alert_url);
        return shareText;
    }

    /** {@inheritDoc} */
    public String getShareSubject(Context context) {
        if (getCallQty() == 1) {
            return context.getString(R.string.share_sc_subject);
        } else {
            return context.getString(R.string.share_rc_subject);
        }
    }
}
