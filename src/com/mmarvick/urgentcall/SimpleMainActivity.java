package com.mmarvick.urgentcall;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SimpleMainActivity extends ActionBarActivity {

	public final static String STATE = "STATE";
	public final static int STATE_OFF = 0;
	public final static int STATE_ON = 1;
	public final static int STATE_ON_WHITELIST = 2;
	public final static int STATE_ON_BLACKLIST = 3;
	public final static int[] STATES = {STATE_OFF, STATE_ON};
	
	private SharedPreferences pref;
	private Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_main);
		
		pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		editor = pref.edit();
		
		Button settings = (Button) findViewById(R.id.simpleSettingsButton);
		settings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), SimpleSettingsActivity.class);
				startActivity(i);
				
			}
			
		});
		
	}
	
	@Override
	protected void onResume() {
		initializeState();
		super.onResume();
	}
	
	private void initializeState() {
		final TextView stateText = (TextView) findViewById(R.id.simpleStateText);
		final SeekBar stateBar = (SeekBar) findViewById(R.id.simpleState);
		
		final int res = 100;
		final int states = STATES.length;
		
		final int max = (states - 1) * res;
		
		int state = pref.getInt(STATE, STATE_ON);
		
		stateBar.setMax(max);
		stateBar.setProgress(state * res);
		setStateText(state, stateText);
		
		stateBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int snap = (seekBar.getProgress() + res/2) / res;
				seekBar.setProgress(snap * res);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				int state = STATES[(seekBar.getProgress() + res/2) / res];
				setStateText(state, stateText);
				editor.putInt(STATE, state);
				editor.commit();				
			}
		});
		
			
	}
	
	public void setStateText(int progress, TextView stateText) {
		Log.e("State:", ""+progress);
		switch (progress) {
		case STATE_OFF:
			stateText.setText("OFF");
			stateText.setTextColor(Color.RED);
			break;
		case STATE_ON:
			stateText.setText("ON");
			stateText.setTextColor(Color.GREEN);
			break;
		case STATE_ON_WHITELIST:
			stateText.setText("ON FOR WHITELIST");
			stateText.setTextColor(Color.YELLOW);
			break;
		case STATE_ON_BLACKLIST:
			stateText.setText("ON EXCEPT BLACKLIST");
			stateText.setTextColor(Color.YELLOW);
			break;
		default:
			break;
		}
		
	}
	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.simple_main_activity_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.advanced:
			Intent i = new Intent(this, MainActivity.class);
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}*/
}	
