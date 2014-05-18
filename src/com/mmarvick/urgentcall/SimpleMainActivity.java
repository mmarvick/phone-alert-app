package com.mmarvick.urgentcall;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SimpleMainActivity extends ActionBarActivity {

	public final String STATE = "STATE";
	public final int STATE_OFF = 0;
	public final int STATE_ON_WHITELIST = 1;
	public final int STATE_ON = 2;
	
	private SharedPreferences pref;
	private Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_main);
		
		pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		editor = pref.edit();
		
		initializeState();
	}
	
	private void initializeState() {
		final TextView stateText = (TextView) findViewById(R.id.simpleStateText);
		final SeekBar state = (SeekBar) findViewById(R.id.simpleState);
		
		state.setMax(STATE_ON);
		state.setProgress(pref.getInt(STATE, STATE_ON));
		setStateText(pref.getInt(STATE, STATE_ON), stateText);
		
		state.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) { }
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				setStateText(progress, stateText);
				editor.putInt(STATE, progress);
				editor.commit();				
			}
		});
		
			
	}
	
	public void setStateText(int progress, TextView stateText) {
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
		default:
			break;
		}
		

	}
}	
