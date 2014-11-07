package com.mmarvick.urgentcall.background;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.activities.MainActivity;
import com.mmarvick.urgentcall.data.AlertCall;
import com.mmarvick.urgentcall.data.DbContract;
import com.mmarvick.urgentcall.data.DbContractCallRule;
import com.mmarvick.urgentcall.data.OldRulesDbOpenHelper;
import com.mmarvick.urgentcall.data.DbContractCallRule.CallRuleEntry;
import com.mmarvick.urgentcall.data.DbOpenHelperCall;
import com.mmarvick.urgentcall.data.PrefHelper;
import com.mmarvick.urgentcall.data.DbContract.RuleEntry;
import com.mmarvick.urgentcall.data.OldDbContractDatabase.RulesEntryOld;
import com.mmarvick.urgentcall.data.OldRulesDbHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallAlertBroadcastReceiver extends BroadcastReceiver {

	OldRulesDbHelper dbHelper;
	AudioManager audio;

	@Override
	public void onReceive(Context context, Intent intent) {
		String callState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		
		/* ------- TEMPORARY, AND TO UPDATE THE DATABASE ------------*/
		OldRulesDbOpenHelper updateDb = new OldRulesDbOpenHelper(context);
		SQLiteDatabase updateDbDb = updateDb.getReadableDatabase();
		updateDbDb.close();	
		
		// If the phone is ringing...
		if (callState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
			Log.e("UC", "Incoming.. phone is ringing! ");
			String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER); // phone number
			Log.e("UC", "Call came in");
			// ... and the app isn't snoozing or turne off ...
			if (PrefHelper.getState(context, Constants.APP_STATE) == RulesEntryOld.STATE_ON
					&& !PrefHelper.isSnoozing(context)) {
			
				// ... and either a repeated call alert or single call alert criteria has been met ...
				// ... trigger an alert!
				
				Log.e("UC", "Gonna check stuff...");
				
				boolean shouldAlert = false;
				boolean ring = false;
				boolean vibrate = false;
				Uri tone = null;
				int volume = 0;
				
				List<AlertCall> callAlerts = AlertCall.getAlerts(context);
				
				for (AlertCall alert : callAlerts) {
					if (alert.shouldAlert(phoneNumber)) {
						shouldAlert = true;
						if (alert.getRing()) {
							ring = true;
							if (alert.getVolume() > volume) {
								volume = alert.getVolume();
								tone = alert.getTone();
							}
						}
						if (alert.getVibrate()) {
							vibrate = true;
						}
					}
				}
				
				if (shouldAlert) {
					Intent ringService = new Intent(context, RingService.class);
					ringService.putExtra(RingService.RING, ring);
					ringService.putExtra(RingService.VIBRATE, vibrate);
					ringService.putExtra(RingService.TONE, tone);
					ringService.putExtra(RingService.VOLUME, volume);
					context.startService(ringService);
				}
			
			}
		} 
	}
	
}