package com.mmarvick.urgentcall;

import com.mmarvick.urgentcall.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import android.support.v7.app.ActionBarActivity;

public class AdvancedMainActivity extends ActionBarActivity {

	private SharedPreferences mPrefs;
	private Editor mEditor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advanced_main);
		
		mPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		mEditor = mPrefs.edit();
		
		ToggleButton mToggle = (ToggleButton) findViewById(R.id.toggleState);
		Button mUserSettings = (Button) findViewById(R.id.settings_button);
		Button mDefaultSettings = (Button) findViewById(R.id.default_settings_button);
		
		int simpleState = mPrefs.getInt(Constants.SIMPLE_STATE, Constants.SIMPLE_STATE_ON);
		boolean state = !(simpleState == Constants.SIMPLE_STATE_OFF);
		
		mToggle.setChecked(state);
		
		mToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					mEditor.putInt(Constants.SIMPLE_STATE, Constants.SIMPLE_STATE_ON);
				} else {
					mEditor.putInt(Constants.SIMPLE_STATE, Constants.SIMPLE_STATE_OFF);
				}
				mEditor.commit();
				
			}
			
			
		});
		
		mUserSettings.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View button) {
				Intent i = new Intent(getApplicationContext(), ContactListActivity.class);
				startActivity(i);
			}
		});
		
		mDefaultSettings.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View button) {
				Intent i = new Intent(getApplicationContext(), UserSettingActivity.class);
				i.putExtra("lookup", "_default");
				startActivity(i);				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.advanced_main_activity_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.simple) {
			Editor edit = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
			edit.putInt(Constants.MODE, Constants.MODE_SIMPLE);
			edit.commit();
			
			Intent i = new Intent(this, MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			return true;				
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

}