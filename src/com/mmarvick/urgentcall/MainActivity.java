package com.mmarvick.urgentcall;

import com.mmarvick.urgentcall.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import android.support.v7.app.ActionBarActivity;

public class MainActivity extends ActionBarActivity {

	private SharedPreferences mPrefs;
	private Editor mEditor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		mEditor = mPrefs.edit();
		
		ToggleButton mToggle = (ToggleButton) findViewById(R.id.toggleState);
		Button mUserSettings = (Button) findViewById(R.id.settings_button);
		Button mDefaultSettings = (Button) findViewById(R.id.default_settings_button);
		
		mToggle.setChecked(mPrefs.getBoolean("state", true));
		
		mToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				mEditor.putBoolean("state", isChecked);
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
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
