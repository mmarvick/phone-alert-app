package com.mmarvick.urgentcall;

import com.mmarvick.phonealert.R;
import com.mmarvick.urgentcall.RulesDbContract.RulesEntry;

import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

public class UserSettingActivity extends ActionBarActivity {

	public final String SETTING_CALL_QTY = "callQty";
	public final String SETTING_CALL_TIME = "callTime";
	private EditText mCallQty;
	private EditText mCallTime; 
	private ToggleButton mStateButton;
	private SharedPreferences mPrefs;
	private Editor mEditor;
	private RulesDbHelper dbHelper;
	private String lookup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_setting);
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);		
		
		mPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		mEditor = mPrefs.edit();
		mCallQty = (EditText) findViewById(R.id.call_qty_value);
		mCallTime = (EditText) findViewById(R.id.call_time_value);
		mStateButton = (ToggleButton) findViewById(R.id.state_on_value);
		Button mSaveButton = (Button) findViewById(R.id.save_button);
		Button mCancelButton = (Button) findViewById(R.id.cancel_button);
		Button mDeleteButton = (Button) findViewById(R.id.delete_button);
		dbHelper = new RulesDbHelper(getApplicationContext());
		Intent intent = getIntent();
		lookup = intent.getStringExtra("lookup");
		
		if (lookup.equals(RulesEntry.LOOKUP_DEFAULT)) {
			setTitle("Editing default settings");
		} else {
			setTitle("Editing settings for " + dbHelper.getName(lookup));
		}
		
		loadData();
		
		mSaveButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveData();
				finish();
				
			}
		});
		
		mCancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		mDeleteButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				delete();
			}
		});
	}	
	
	private void loadData() {
		if (!dbHelper.isInDb(lookup)) {
			mCallQty.setText("3");
			mCallTime.setText("15");
			mStateButton.setChecked(true);
		} else {
			mCallQty.setText("" + dbHelper.getCallsAllowed(lookup));
			mCallTime.setText("" + dbHelper.getCallMins(lookup));
			mStateButton.setChecked(dbHelper.getStateOn(lookup));
		}
	}

	private void saveData() {
		dbHelper.makeContact(lookup, Integer.parseInt(mCallQty.getText().toString()), Integer.parseInt(mCallTime.getText().toString()), mStateButton.isChecked());
	}
	
	private void delete() {
		if (dbHelper.isInDb(lookup) && !lookup.equals(RulesEntry.LOOKUP_DEFAULT)) {
			dbHelper.deleteContact(lookup);
			finish();
		} else if (lookup.equals(RulesEntry.LOOKUP_DEFAULT)) {
			Toast.makeText(getApplicationContext(), "Cannot delete default settings", Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    	case android.R.id.home:
	    		finish();
	    		return true;
	        default:
	        	return true;
	    }
	}	
}
