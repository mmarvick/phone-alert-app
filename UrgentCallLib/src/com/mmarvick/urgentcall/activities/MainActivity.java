package com.mmarvick.urgentcall.activities;

import java.util.List;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.TimePicker;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.PrefHelper;
import com.mmarvick.urgentcall.widgets.EditTextPrompt;
import com.mmarvick.urgentcall.widgets.RateDialog;
import com.mmarvick.urgentcall.widgets.SnoozeDialog;
import com.mmarvick.urgentcall.widgets.SnoozeEndDialog;
import com.mmarvick.urgentcall.widgets.StatePrompt;
import com.mmarvick.urgentcall.widgets.UpgradeDialog;

// Launcher icons created with Android Asset Studio
// http://romannurik.github.io/AndroidAssetStudio

public class MainActivity extends ActionBarActivity
				implements TimePickerDialog.OnTimeSetListener {
	private CheckSnooze checker;
	private SnoozeEndDialog endSnoozeDialog;
	private AlertDialog disclaimerDialog;
	private AlertDialog versionsDialog;
	
	private TextView stateText;
	private TextView footerTextMain;
	private TextView footerText2;
	private TextView footerTextCallsNum;
	private TextView footerTextCallsText;
	private TextView footerTextMinsNum; 
	private TextView footerTextMinsText; 
	private TextView footerTextForText;
	private TextView footerTextForSelection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		checkTwoVersions();
		checkDisclaimer();
		setContentView(R.layout.activity_main);
		
		stateText = (TextView) findViewById(R.id.simpleStateText);	
		footerTextMain = (TextView) findViewById(R.id.textView_footer1);
		footerText2 = (TextView) findViewById(R.id.textView_footer2);
		footerTextCallsNum = (TextView) findViewById(R.id.textView_callsNumber);
		footerTextCallsText = (TextView) findViewById(R.id.textView_callsText);
		footerTextMinsNum = (TextView) findViewById(R.id.textView_minsNumber);
		footerTextMinsText = (TextView) findViewById(R.id.textView_minsText);
		footerTextForText = (TextView) findViewById(R.id.textView_forText);
		footerTextForSelection = (TextView) findViewById(R.id.textView_forSelection);
		
		footerTextCallsNum.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				new EditTextPrompt(MainActivity.this, Constants.CALL_QTY_MIN, Constants.CALL_QTY_MAX,
						Constants.CALL_QTY, Constants.CALL_QTY_DEFAULT, Constants.CALL_QTY_TITLE);
				
			}
		});
		
		footerTextMinsNum.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				new EditTextPrompt(MainActivity.this, Constants.CALL_MIN_MIN, Constants.CALL_MIN_MAX,
						Constants.CALL_MIN, Constants.CALL_MIN_DEFAULT, Constants.CALL_MIN_TITLE);
				
			}
		});
		
		footerText2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (PrefHelper.isSnoozing(getApplicationContext())) {
					endSnooze();
				}
			}
		});
		
		footerTextForSelection.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				new StatePrompt(MainActivity.this).show();
			}
		});
		
		stateText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				new StatePrompt(MainActivity.this).show();
			}
		});
	}
	
	@Override
	protected void onResume() {

		check();
		checker = new CheckSnooze();
		checker.execute();
		super.onResume();		
	}
	
	@Override
	protected void onPause() {
		checker.cancel(true);	
		super.onPause();
	}
	
	public void check() {
		setStateText();
		if (!PrefHelper.isSnoozing(getApplicationContext()) && endSnoozeDialog != null) {
			endSnoozeDialog.cancel();
		}
		
		if (PrefHelper.isSnoozing(getApplicationContext())) {
			footerText2.setClickable(true);
		} else {
			footerText2.setClickable(false);
		}
	}	
	
	private void showSnooze() {
		if (getResources().getBoolean(R.bool.paid_version)) {
			SnoozeDialog snooze = new SnoozeDialog(this, this, 0, 0, true);
			snooze.show();
		} else {
			UpgradeDialog.upgradeDialog(this, getString(R.string.upgrade_body_snooze));
		}
	}
	
	private void endSnooze() {
		endSnoozeDialog = new SnoozeEndDialog(this);
		endSnoozeDialog.show();
		endSnoozeDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				check();
			}
		});
	}
	
	@Override
	public void onTimeSet(TimePicker view, int hours, int minutes) {
		long snoozeTime = hours * 3600000 + minutes * 60000;
		//TODO: Hack! Added 1/2 s to make snooze time show up correctly when first set.
		if (snoozeTime > 0) {
			snoozeTime += 500;
		}
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
	
	public void setStateText() {
		if (PrefHelper.isSnoozing(getApplicationContext())) {
			stateText.setText("SNOOZING");
			stateText.setTextColor(Color.RED);
			footerOneText();
			footerText2.setText(setCountdown());
			Log.e("Countdown", setCountdown());
			footerTextForText.setText("");
			footerTextForSelection.setText("");
		} else {
			int state = PrefHelper.getState(getApplicationContext());
			
			if (state == Constants.SIMPLE_STATE_OFF) {
				stateText.setText("OFF");
				stateText.setTextColor(Color.RED);
				footerOneText();
				footerText2.setText("trigger an alert");
				footerTextForText.setText("");
				footerTextForSelection.setText("");;	
			} else {
				stateText.setText("ON");
				stateText.setTextColor(Color.GREEN);
				footerOneText();
				footerText2.setText("triggers an alert");
				footerTextForText.setText("from ");
				if (state == Constants.SIMPLE_STATE_ON) {
					footerTextForSelection.setText("any caller");
				} else if (state == Constants.SIMPLE_STATE_WHITELIST) {
					footerTextForSelection.setText("whitelisted callers only");
				} else {
					footerTextForSelection.setText("all except blacklisted callers");
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
			
			alertDialogBuilder
				.setTitle(getString(R.string.pro_installed_title))
				.setMessage(getString(R.string.pro_installed_body))
				.setCancelable(false)
				.setPositiveButton(getString(R.string.pro_installed_ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Uri pkg_uri = Uri.parse("package:com.mmarvick.urgentcall_lite");
						Intent removeIntent = new Intent(Intent.ACTION_DELETE, pkg_uri);
						startActivity(removeIntent);
					}
				});
			
			versionsDialog = alertDialogBuilder.create();
			versionsDialog.show();
		}
	}
	
	public void checkDisclaimer() {
		if (!(PrefHelper.disclaimerCheck(getApplicationContext()))) {
			PrefHelper.disclaimerSaveBackup(getApplicationContext());
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			
			alertDialogBuilder
				.setTitle(getString(R.string.disclaimer_title))
				.setMessage(getString(R.string.disclaimer_body))
				.setCancelable(false)
				.setPositiveButton(getString(R.string.disclaimer_agree), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						PrefHelper.disclaimerAgreed(getApplicationContext());
						PrefHelper.disclaimerResumeBackup(getApplicationContext());
					}
				})
				.setNegativeButton(getString(R.string.disclaimer_disagree), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						finish();
						
					}
				});
			
			disclaimerDialog = alertDialogBuilder.create();
			disclaimerDialog.show();			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
  
	    // Call the super class
	    boolean returnValue = super.onCreateOptionsMenu(menu);
	    
	    // Remove upgrade option in pro version
		if (getResources().getBoolean(R.bool.paid_version)) {
		    MenuItem upgrade = menu.findItem(R.id.action_upgrade);
		    upgrade.setVisible(false);
		    supportInvalidateOptionsMenu();
		}
		
		return returnValue;
	}
	
	private void share() {
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
		String message = getString(R.string.share_1) + PrefHelper.getCallQty(getApplicationContext());
		message += getString(R.string.share_2) + PrefHelper.getCallMins(getApplicationContext());
		message += getString(R.string.share_3);
		shareIntent.putExtra(Intent.EXTRA_TEXT, message);
		shareIntent.setType("text/plain");
		startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)));
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
				endSnooze();
			} else {
				showSnooze();
			}
			return true;
		} else if (itemId == R.id.action_share) {
			share();
			return true;
		} else if (itemId == R.id.action_rate) {
			new RateDialog(this).show();
			return true;
		} else if (itemId == R.id.action_upgrade) {
			UpgradeDialog.upgradeDialog(this, getString(R.string.upgrade_body_menu), getString(R.string.upgrade_title_menu));
			return true;
		} else {
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
				if (isCancelled()) break;
			}
			return true;
		}
	}
	
}	
