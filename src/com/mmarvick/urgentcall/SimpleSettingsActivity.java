package com.mmarvick.urgentcall;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SimpleSettingsActivity extends ActionBarActivity {
	private SharedPreferences pref;
	private RulesDbHelper dbHelper;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_settings);
		
		dbHelper = new RulesDbHelper(getApplicationContext());
		
		final EditText callQty = (EditText) findViewById(R.id.simple_call_qty_value);
		final EditText callTime = (EditText) findViewById(R.id.simple_call_time_value);
		Button save = (Button) findViewById(R.id.simple_save_button);
		Button cancel = (Button) findViewById(R.id.simple_cancel_button);
		Button customList = (Button) findViewById(R.id.simple_custom_list);
		
		final String lookup = RulesDbContract.RulesEntry.LOOKUP_DEFAULT;
		
		callQty.setText("" + dbHelper.getCallsAllowed(lookup));
		callTime.setText("" + dbHelper.getCallMins(lookup));
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		int mode = pref.getInt(Constants.SIMPLE_STATE, Constants.SIMPLE_STATE_ON);
		if (!(mode == Constants.SIMPLE_STATE_BLACKLIST || mode == Constants.SIMPLE_STATE_WHITELIST)) {
			customList.setVisibility(View.GONE);
		}
		
		save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dbHelper.makeContact(lookup, Integer.parseInt(callQty.getText().toString()), Integer.parseInt(callTime.getText().toString()), true);
				finish();
			}
		});
		
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});	
		
		customList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(), ContactListActivity.class);
				startActivity(i);
			}
		});
		
	}	
}