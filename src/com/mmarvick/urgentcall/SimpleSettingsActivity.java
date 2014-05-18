package com.mmarvick.urgentcall;

import android.app.Activity;
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
	private Editor editor;
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
		
		final String lookup = RulesDbContract.RulesEntry.LOOKUP_DEFAULT;
		
		callQty.setText("" + dbHelper.getCallsAllowed(lookup));
		callTime.setText("" + dbHelper.getCallMins(lookup));
		
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
		
	}	
}