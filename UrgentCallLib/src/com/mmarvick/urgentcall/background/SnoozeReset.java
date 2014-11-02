package com.mmarvick.urgentcall.background;

import com.mmarvick.urgentcall.data.OldPrefHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SnoozeReset extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		OldPrefHelper.setSnoozeTime(context, 0);
	}

}
