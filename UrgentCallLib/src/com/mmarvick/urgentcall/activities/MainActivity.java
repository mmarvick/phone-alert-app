package com.mmarvick.urgentcall.activities;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.PrefHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {
	
	@Override
	protected void onResume() {
		
		if (PrefHelper.getView(getApplicationContext()) == Constants.VIEW_NEW) {
			Intent i = new Intent(this, MainNewActivity.class);
			startActivity(i);
		} else {
			Intent i = new Intent(this, MainOldActivity.class);
			startActivity(i);
		}
		
		super.onResume();
	}
}
