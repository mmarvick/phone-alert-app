package com.mmarvick.urgentcall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
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
import android.widget.TimePicker;

import com.mmarvick.urgentcall.Constants;

// Launcher icons created with Android Asset Studio
// http://romannurik.github.io/AndroidAssetStudio

public class MainActivity extends ActionBarActivity
				implements TimePickerDialog.OnTimeSetListener {
	private SharedPreferences pref;
	private Editor editor;
	private CheckSnooze checker;
	
	private TextView stateText;
	private TextView footerTextMain;
	private TextView footerText2;
	private TextView footerText3;
	private TextView footerTextCallsNum;
	private TextView footerTextCallsText;
	private TextView footerTextInBetween;
	private TextView footerTextMinsNum; 
	private TextView footerTextMinsText; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		editor = pref.edit();
		
		stateText = (TextView) findViewById(R.id.simpleStateText);	
		footerTextMain = (TextView) findViewById(R.id.textView_footer1);
		footerText2 = (TextView) findViewById(R.id.textView_footer2);
		footerText3 = (TextView) findViewById(R.id.textView_footer3);	
		footerTextCallsNum = (TextView) findViewById(R.id.textView_callsNumber);
		footerTextCallsText = (TextView) findViewById(R.id.textView_callsText);
		footerTextMinsNum = (TextView) findViewById(R.id.textView_minsNumber);
		footerTextMinsText = (TextView) findViewById(R.id.textView_minsText);
		
		footerTextCallsNum.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				new EditTextPrompt(MainActivity.this, 1, Integer.MAX_VALUE, Constants.CALL_QTY, 
						"Number of calls");
				
			}
		});
		
		footerTextMinsNum.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				new EditTextPrompt(MainActivity.this, 1, Integer.MAX_VALUE, Constants.CALL_MIN, 
						"Minutes for calls");
				
			}
		});
	}
	
	@Override
	protected void onResume() {
		initializeState();
		checker = new CheckSnooze();
		checker.execute();
		checkTwoVersions();
		super.onResume();		
	}
	
	@Override
	protected void onPause() {
		checker.cancel(true);	
		super.onPause();
	}
	
	public void check() {
		setStateText();
	}	
	
	private void showSnooze() {
		if (getResources().getBoolean(R.bool.paid_version)) {
			SnoozeDialog snooze = new SnoozeDialog(this, this, 0, 0, true);
			snooze.show();
		} else {
			UpgradeDialog.upgradeDialog(this,
					"Users of Urgent Call Pro can snooze alerts for a period of time.\n\n"
					+ "Users of Urgent Call Lite mustturn the application off and on manually.");
		}
	}
	
	@Override
	public void onTimeSet(TimePicker view, int hours, int minutes) {
		long snoozeTime = hours * 3600000 + minutes * 60000 + 500;
			//TODO: Hack! Added 1/2 s to make snooze time show up correctly when first set.
		PrefHelper.setSnoozeTime(getApplicationContext(), snoozeTime);
		setStateText();
	}	
	

	
	public String setCountdown() {
		long time = PrefHelper.snoozeRemaining(getApplicationContext());
		long allsec = time / 1000;
		long sec = allsec % 60;
		long min = (allsec % 3600) / 60;
		long hour = allsec / 3600;
		String extraMin = ((min<10) ? "0" : "");
		String extraSec = ((sec<10) ? "0" : "");
		return hour + ":" + extraMin + min + ":" + extraSec + sec;			
		
	}
	
	private void initializeState() {
		
		final SeekBar stateBar = (SeekBar) findViewById(R.id.simpleState);
		
		final int res = 100;
		final int states = Constants.SIMPLE_STATES.length;
		
		final int max = (states - 1) * res;
		
		int state = pref.getInt(Constants.SIMPLE_STATE, Constants.SIMPLE_STATE_ON);
		
		stateBar.setMax(max);
		stateBar.setProgress(getStateIndex(state) * res);
		setStateText();
		
		stateBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int snap = (seekBar.getProgress() + res/2) / res;
				int state = Constants.SIMPLE_STATES[snap];
				seekBar.setProgress(snap * res);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				int state = Constants.SIMPLE_STATES[(seekBar.getProgress() + res/2) / res];
				editor.putInt(Constants.SIMPLE_STATE, state);
				editor.commit();
				setStateText();
			}
		});
		
			
	}
	
	public void setStateText() {
		if (PrefHelper.isSnoozing(getApplicationContext())) {
			stateText.setText("SNOOZING");
			stateText.setTextColor(Color.RED);
			footerOneText();
			footerText2.setText(setCountdown());
			footerText3.setText("");
		} else {
			int state = PrefHelper.getState(getApplicationContext());
			int list = PrefHelper.getListMode(getApplicationContext());
			
			if (state == Constants.SIMPLE_STATE_OFF) {
				stateText.setText("OFF");
				stateText.setTextColor(Color.RED);
				footerOneText();
				footerText2.setText("trigger an alert");
				footerText3.setText("");	
			} else if (state == Constants.SIMPLE_STATE_ON && list == Constants.LIST_NONE) {
				stateText.setText("ON");
				stateText.setTextColor(Color.GREEN);
				footerOneText();
				footerText2.setText("trigger an alert");
				footerText3.setText("from any caller");
			} else {
				stateText.setText("ON FOR SOME");
				stateText.setTextColor(Color.YELLOW);
				footerOneText();
				footerText2.setText("trigger an alert");
				if (list == Constants.LIST_WHITELIST) {
					footerText3.setText("from whitelisted callers only");
				} else {
					footerText3.setText("from all except blacklisted callers");
				}
			}
		}	
	}
	
	public void footerOneText() {
		if (PrefHelper.isSnoozing(getApplicationContext())) {
			footerTextCallsNum.setText("");
			footerTextCallsText.setText("");
			footerTextMinsNum.setText("");
			footerTextMinsText.setText("");
			footerTextMain.setText("No alerts for");
		} else {
			int state = PrefHelper.getState(getApplicationContext());
			if (state == Constants.SIMPLE_STATE_OFF) {
				footerTextCallsNum.setText("");
				footerTextCallsText.setText("");
				footerTextMinsNum.setText("");
				footerTextMinsText.setText("");
				footerTextMain.setText("No calls");
			} else {
				footerTextCallsNum.setText("" + PrefHelper.getCallQty(getApplicationContext()));
				footerTextCallsText.setText(" calls");
				footerTextMain.setText(" in ");
				footerTextMinsNum.setText("" + PrefHelper.getCallMins(getApplicationContext()));
				footerTextMinsText.setText(" minutes");
			}
		}

	}
	
	public int getStateIndex(int state) {
		for (int i = 0; i < Constants.SIMPLE_STATES.length; i++) {
			if (Constants.SIMPLE_STATES[i] == state) return i;
		}
		return -1;
	}
	


	public void checkTwoVersions() {
		List<PackageInfo> pkgs = getPackageManager().getInstalledPackages(0);
		boolean lite = false;
		boolean pro = false;
		
		for (int i = 0; i < pkgs.size(); i++) {
			if (pkgs.get(i).packageName.equals("com.mmarvick.urgentcall_lite")) {
				lite = true;
			} else if (pkgs.get(i).packageName.equals("com.mmarvick.urgentcall_pro")) {
				pro = true;
			}
		}
		
		if (lite && pro) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			
			Log.e("Test", "Makes it to the builder");
			
			alertDialogBuilder
				.setTitle("Thank you!")
				.setMessage("Thank you for installing Urgent Call Pro!\n\nBefore continuing, please remove Urgent Call Lite.")
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Uri pkg_uri = Uri.parse("package:com.mmarvick.urgentcall_lite");
						Intent removeIntent = new Intent(Intent.ACTION_DELETE, pkg_uri);
						startActivity(removeIntent);
					}
				});
			
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    int itemId = item.getItemId();
		if (itemId == R.id.action_settings) {
			Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
			startActivity(i);
			return true;
		} else if (itemId == R.id.action_snooze) {
			if (PrefHelper.isSnoozing(getApplicationContext())) {
				PrefHelper.setSnoozeTime(getApplicationContext(), 0);
				check();
			} else {
				showSnooze();
			}
			return true;
		}
		else {
			return super.onOptionsItemSelected(item);
		}
	}
	
	private class CheckSnooze extends AsyncTask<Void, Void, Boolean> {
		protected Boolean doInBackground(Void... none) {
			long i = 1;
			while (i > 0) {
				runOnUiThread(new Runnable() {public void run() {check();}});
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
			return true;
		}
	}
	
}	
