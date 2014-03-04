package com.mmarvick.phonealert;

import com.mmarvick.phonealert.RulesDbContract.RulesEntry;

import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

public class SettingActivity extends Activity {

	public final String SETTING_CALL_QTY = "callQty";
	public final String SETTING_CALL_TIME = "callTime";
	private EditText mCallQty;
	private EditText mCallTime;
	private SharedPreferences mPrefs;
	private Editor mEditor;
	private RulesDbHelper dbHelper;
	private String lookup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		mPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		mEditor = mPrefs.edit();
		mCallQty = (EditText) findViewById(R.id.call_qty_value);
		mCallTime = (EditText) findViewById(R.id.call_time_value);
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
	        case R.id.action_bug:
	        	Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://github.com/mmarvick/phone-alert-app/issues"));
	        	startActivity(i);
	        default:
	        	return true;
	    }
	}	
	
	private void loadData() {
		if (lookup != null) {
			if (!dbHelper.isInDb(lookup)) {
				mCallQty.setText("3");
				mCallTime.setText("15");
			} else {
				mCallQty.setText("" + dbHelper.getCallsAllowed(lookup));
				mCallTime.setText("" + dbHelper.getCallMins(lookup));
			}
		} else {
			mCallQty.setText("" + mPrefs.getInt(SETTING_CALL_QTY, 3));
			mCallTime.setText("" + mPrefs.getInt(SETTING_CALL_TIME, 15));
			Toast.makeText(getApplicationContext(), "Pulling from default file", Toast.LENGTH_LONG).show();
		}
	}

	private void saveData() {
		if (lookup != null) {
			dbHelper.makeContact(lookup, Integer.parseInt(mCallQty.getText().toString()), Integer.parseInt(mCallTime.getText().toString()));
		} else {
			mEditor.putInt(SETTING_CALL_QTY, Integer.parseInt(mCallQty.getText().toString()));
			mEditor.putInt(SETTING_CALL_TIME, Integer.parseInt(mCallTime.getText().toString()));
			mEditor.commit();	
		}
	}
	
	private void delete() {
		if (dbHelper.isInDb(lookup) && !lookup.equals(RulesEntry.LOOKUP_DEFAULT)) {
			dbHelper.deleteContact(lookup);
			finish();
		} else if (lookup.equals(RulesEntry.LOOKUP_DEFAULT)) {
			Toast.makeText(getApplicationContext(), "Cannot delete default settings", Toast.LENGTH_LONG).show();
		}
	}
}
