package com.mmarvick.urgentcall;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
	
	@Override
	protected void onResume() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		int mode = pref.getInt(Constants.MODE, Constants.MODE_SIMPLE);
		switch(mode) {
		case Constants.MODE_ADVANCED:
			Intent advIntent = new Intent(this, AdvancedMainActivity.class);
			startActivity(advIntent);
			break;
		case Constants.MODE_SIMPLE:
			Intent simpleIntent = new Intent(this, SimpleMainActivity.class);
			startActivity(simpleIntent);
			break;
		}
		super.onResume();
	}
}
