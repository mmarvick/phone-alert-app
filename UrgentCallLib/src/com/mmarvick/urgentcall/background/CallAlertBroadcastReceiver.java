package com.mmarvick.urgentcall.background;

import java.util.ArrayList;
import java.util.Date;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.data.AlertCall;
import com.mmarvick.urgentcall.data.DbContract;
import com.mmarvick.urgentcall.data.DbContractCallRule;
import com.mmarvick.urgentcall.data.DbContractCallRule.CallRuleEntry;
import com.mmarvick.urgentcall.data.DbOpenHelperCall;
import com.mmarvick.urgentcall.data.OldPrefHelper;
import com.mmarvick.urgentcall.data.DbContract.RuleEntry;
import com.mmarvick.urgentcall.data.OldDbContractDatabase.RulesEntryOld;
import com.mmarvick.urgentcall.data.OldRulesDbHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallAlertBroadcastReceiver extends BroadcastReceiver {

	OldRulesDbHelper dbHelper;
	AudioManager audio;

	@Override
	public void onReceive(Context context, Intent intent) {
		String callState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		dbHelper = new OldRulesDbHelper(context);
		audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		
		// If the phone is ringing...
		if (callState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
			
			String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER); // phone number
			Log.e("UC", "Call came in");
			// ... and the app isn't snoozing or turne off ...
			if (OldPrefHelper.getState(context, Constants.APP_STATE) == RulesEntryOld.STATE_ON
					&& !OldPrefHelper.isSnoozing(context)) {
			
				// ... and either a repeated call alert or single call alert criteria has been met ...
				// ... trigger an alert!
				
				Log.e("UC", "Gonna check stuff...");
				
				boolean shouldAlert = false;
				boolean ring = false;
				boolean vibrate = false;
				int volume = 0;
				
				DbOpenHelperCall dbOpenHelper = new DbOpenHelperCall(context);
				SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
				Cursor ruleCursor = database.query(CallRuleEntry.TABLE_NAME,
						new String[] {CallRuleEntry._ID},
						null, null, null, null, null);
				ArrayList<Integer> callAlerts = new ArrayList<Integer>();
				ruleCursor.moveToFirst();
				while (!ruleCursor.isAfterLast()) {
					callAlerts.add(ruleCursor.getInt(ruleCursor.getColumnIndex(CallRuleEntry._ID)));
					ruleCursor.moveToNext();
					Log.e("UC", "Added an alert");
				}
				ruleCursor.close();
				
				for (int id : callAlerts) {
					Log.e("UC", "Checking alert: " + id);
					AlertCall alert = new AlertCall(context, id);
					if (alert.shouldAlert(phoneNumber)) {
						shouldAlert = true;
						if (alert.getRing()) {
							ring = true;
							if (alert.getVolume() > volume) {
								volume = alert.getVolume();
							}
						}
						if (alert.getVibrate()) {
							vibrate = true;
						}
					}
				}
				
				if (shouldAlert) {
					alertAction(context, Constants.CALL_ALERT_TYPE_RC);
				}
			
			}
		} 
	}

	
	private void alertAction(Context context, int alertType) {
		Intent ringService = new Intent(context, RingService.class);
		ringService.putExtra(Constants.ALERT_TYPE, alertType);
		context.startService(ringService);			
	}	
	
}