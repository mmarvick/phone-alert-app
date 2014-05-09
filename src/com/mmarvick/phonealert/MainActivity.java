package com.mmarvick.phonealert;

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
		Button mSettings = (Button) findViewById(R.id.settings_button);
		
		mToggle.setChecked(mPrefs.getBoolean("state", true));
		
		mToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				mEditor.putBoolean("state", isChecked);
				mEditor.commit();
				
			}
			
			
		});
		
		mSettings.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View button) {
				Intent i = new Intent(getApplicationContext(), ContactListActivity.class);
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
