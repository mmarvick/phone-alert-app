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
import android.widget.TimePicker;

import com.mmarvick.urgentcall.Constants;

public class MainActivity extends ActionBarActivity
				implements TimePickerDialog.OnTimeSetListener {
	private SharedPreferences pref;
	private Editor editor;
	private CheckSnooze checker;
	
	private TextView stateText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		editor = pref.edit();
		
		stateText = (TextView) findViewById(R.id.simpleStateText);	
	}
	
	@Override
	protected void onResume() {
		initializeState();
		checkTwoVersions();
		checker = new CheckSnooze();
		checker.execute();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		checker.cancel(true);	
		super.onPause();
	}
	
	private void showSnooze() {
		if (getResources().getBoolean(R.bool.paid_version)) {
			TimePickerDialog snooze = new TimePickerDialog(this, this, 0, 0, true);
			snooze.setTitle("Snooze for...");
			snooze.show();
		} else {
			upgradeDialog("Users of Urgent Call Pro can snooze alerts for a period of time.\n\nUsers of Urgent Call Lite must turn the application off and on manually.");
		}
	}
	
	@Override
	public void onTimeSet(TimePicker view, int hours, int minutes) {
		long snoozeTime = SystemClock.elapsedRealtime() + hours * 3600000 + minutes * 60000;
		editor.putLong(Constants.SNOOZE_TIME, snoozeTime);
		editor.commit();
		updateState(hours*3600000 + minutes*60000);
	}	
	
	public void snoozing() {
		long remaining = PrefHelper.snoozeRemaining(getApplicationContext());
		updateState(remaining);
	}
	
	public void updateState(long remaining) {
		setCountdown(remaining);
		setStateText(pref.getInt(Constants.SIMPLE_STATE, Constants.SIMPLE_STATE_ON), stateText);
	}
	
	public void setCountdown(long time) {
		TextView snoozeText = (TextView) findViewById(R.id.textView_snooze);
		if (time > 0) {
			snoozeText.setVisibility(View.VISIBLE);
			long allsec = time / 1000;
			long sec = allsec % 60;
			long min = (allsec % 3600) / 60;
			long hour = allsec / 3600;
			String extraMin = ((min<10) ? "0" : "");
			String extraSec = ((sec<10) ? "0" : "");
			snoozeText.setText(hour + ":" + extraMin + min + ":" + extraSec + sec);			
		} else {
			snoozeText.setVisibility(View.GONE);
		}
		
	}
	
	private void initializeState() {
		
		final SeekBar stateBar = (SeekBar) findViewById(R.id.simpleState);
		
		final int res = 100;
		final int states = Constants.SIMPLE_STATES.length;
		
		final int max = (states - 1) * res;
		
		int state = pref.getInt(Constants.SIMPLE_STATE, Constants.SIMPLE_STATE_ON);
		
		stateBar.setMax(max);
		stateBar.setProgress(getStateIndex(state) * res);
		setStateText(state, stateText);
		
		stateBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int snap = (seekBar.getProgress() + res/2) / res;
				int state = Constants.SIMPLE_STATES[snap];
				if (!(getResources().getBoolean(R.bool.paid_version)) && (state == Constants.SIMPLE_STATE_SOME)) {
					upgradeDialog("Users of Urgent Call Pro may trigger alerts for specific users.\n\n"
							+ "Urgent Call Lite can only be turned on or off for all users.");
					state = PrefHelper.getState(getApplicationContext());
					snap = getStateIndex(state);
				}
				seekBar.setProgress(snap * res);
				editor.putInt(Constants.SIMPLE_STATE, state);
				editor.commit();				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				int state = Constants.SIMPLE_STATES[(seekBar.getProgress() + res/2) / res];
				setStateText(state, stateText);
			}
		});
		
			
	}
	
	public void setStateText(int progress, TextView stateText) {
		Log.e("State:", ""+progress);
		if (PrefHelper.isSnoozing(getApplicationContext())) {
			stateText.setText("SNOOZING");
			stateText.setTextColor(Color.RED);
		} else {
			switch (progress) {
			case Constants.SIMPLE_STATE_OFF:
				stateText.setText("OFF");
				stateText.setTextColor(Color.RED);
				break;
			case Constants.SIMPLE_STATE_ON:
				stateText.setText("ON");
				stateText.setTextColor(Color.GREEN);
				break;
			case Constants.SIMPLE_STATE_SOME:
				stateText.setText("ON FOR SOME");
				stateText.setTextColor(Color.YELLOW);
				break;
			default:
				break;
			}
		}
		
	}
	
	public int getStateIndex(int state) {
		for (int i = 0; i < Constants.SIMPLE_STATES.length; i++) {
			if (Constants.SIMPLE_STATES[i] == state) return i;
		}
		return -1;
	}
	
	public void upgradeDialog(String messageText) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		alertDialogBuilder
			.setTitle("Pro Version Only")
			.setMessage(messageText)
			.setCancelable(false)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			})
			.setNeutralButton("Upgrade", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					String packageName = "com.mmarvick.urgentcall_pro";
					try {
					    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
					} catch (android.content.ActivityNotFoundException anfe) {
					    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
					}
				}
			});
		
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
		
		
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
			
			if (lite && pro) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

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
				onTimeSet(null, 0, 0);
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
				runOnUiThread(new Runnable() {public void run() {snoozing();}});
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return true;
		}
	}
	
}	
