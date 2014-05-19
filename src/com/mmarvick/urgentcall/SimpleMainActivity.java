package com.mmarvick.urgentcall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
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

import com.mmarvick.urgentcall.Constants;

public class SimpleMainActivity extends ActionBarActivity {
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
		checkTwoVersions();		
		super.onResume();
	}
	
	private void initializeState() {
		final TextView stateText = (TextView) findViewById(R.id.simpleStateText);
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
				seekBar.setProgress(snap * res);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				int state = Constants.SIMPLE_STATES[(seekBar.getProgress() + res/2) / res];
				setStateText(state, stateText);
				editor.putInt(Constants.SIMPLE_STATE, state);
				editor.commit();				
			}
		});
		
			
	}
	
	public void setStateText(int progress, TextView stateText) {
		Log.e("State:", ""+progress);
		switch (progress) {
		case Constants.SIMPLE_STATE_OFF:
			stateText.setText("OFF");
			stateText.setTextColor(Color.RED);
			break;
		case Constants.SIMPLE_STATE_ON:
			stateText.setText("ON");
			stateText.setTextColor(Color.GREEN);
			break;
		case Constants.SIMPLE_STATE_WHITELIST:
			stateText.setText("ON FOR WHITELIST");
			stateText.setTextColor(Color.YELLOW);
			break;
		case Constants.SIMPLE_STATE_BLACKLIST:
			stateText.setText("ON EXCEPT BLACKLIST");
			stateText.setTextColor(Color.YELLOW);
			break;
		default:
			break;
		}
		
	}
	
	public int getStateIndex(int state) {
		for (int i = 0; i < Constants.SIMPLE_STATES.length; i++) {
			if (Constants.SIMPLE_STATES[i] == state) return i;
		}
		return -1;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.simple_main_activity_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.advanced) {
			if (getResources().getBoolean(R.bool.paid_version)) {
				Editor edit = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
				edit.putInt(Constants.MODE, Constants.MODE_ADVANCED);
				edit.commit();
				
				Intent i = new Intent(this, MainActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				return true;				
			} else {
				advancedInFree();
				return true;
			} 
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void advancedInFree() {
		upgradeDialog("This feature only available in the pro version!");
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
					.setMessage("Thank you for installing Urgent Call Pro! Please remove Urgent Call Lite before continuing. Keeping both versions may cause buggy behavior.")
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
	
}	
