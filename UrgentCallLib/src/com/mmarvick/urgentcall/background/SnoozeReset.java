package com.mmarvick.urgentcall.background;

import com.mmarvick.urgentcall.data.PrefHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SnoozeReset extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		PrefHelper.setSnoozeTime(context, 0);
	}

}
