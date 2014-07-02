package com.mmarvick.urgentcall.activities;

import java.util.ArrayList;
import java.util.List;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.PrefHelper;
import com.mmarvick.urgentcall.settings.SettingsActivity;
import com.mmarvick.urgentcall.widgets.MyViewPager;
import com.mmarvick.urgentcall.widgets.OnOptionsChangedListener;
import com.mmarvick.urgentcall.widgets.RateDialog;
import com.mmarvick.urgentcall.widgets.SnoozeDialog;
import com.mmarvick.urgentcall.widgets.SnoozeEndDialog;
import com.mmarvick.urgentcall.widgets.UpgradeDialog;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TimePicker;

public class MainActivity extends ActionBarActivity 
	implements TimePickerDialog.OnTimeSetListener{
	
	public static final int TAB_HOME = 0;
	public static final int TAB_MSG = 1;
	public static final int TAB_RC = 2;
	public static final int TAB_SC = 3;
	public static final String [] fragmentTitles = new String[]
			{"Home", "Text", "Repeat\nCall", "Single\nCall"};
	public ArrayList<TabFragment> fragments;
	
	private ActionBar actionBar;
	private MyPagerAdapter mAdapter;
	private MyViewPager mViewPager;
	private PeriodicChecker mChecker;
	private SnoozeEndDialog endSnoozeDialog;	
	
	private boolean mCanChangeTabs = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		checkDisclaimer();		

		actionBar = getSupportActionBar();		
		
		// Add fragments to list
		fragments = new ArrayList<TabFragment>();
		fragments.add((TabFragment) (new HomeFragment()));
		fragments.add((TabFragment) (new MessageFragment()));	
		fragments.add((TabFragment) (new RepeatCallFragment()));
		fragments.add((TabFragment) (new SingleCallFragment()));
		
		// Add the fragments to the view pager
		mAdapter = new MyPagerAdapter(getSupportFragmentManager());
		mViewPager = (MyViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});
		
		// Add tabs to the action bar
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		// Add a tab listener that changes tabs when the flag is set that changing is allowed
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) { }

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				if (mCanChangeTabs) {
					mViewPager.setCurrentItem(tab.getPosition());
				}
				
				// If tabs can't be changed (due to off or snooze), alert the user and change the tab back
				else if (!mCanChangeTabs && tab.getPosition() != TAB_HOME) {
					AlertDialog.Builder dialogBuilder = new Builder(MainActivity.this);
					dialogBuilder
						.setCancelable(true)
						.setPositiveButton(R.string.tab_change_prohibitted_dialog_ok, new OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
					
					if (PrefHelper.isSnoozing(getApplicationContext())) {
						dialogBuilder.setTitle(getString(R.string.tab_change_prohibitted_dialog_snooze_title))
						.setMessage(getString(R.string.tab_change_prohibitted_dialog_snooze_message));						
					} else {
						dialogBuilder.setTitle(getString(R.string.tab_change_prohibitted_dialog_off_title))
							.setMessage(getString(R.string.tab_change_prohibitted_dialog_off_message));
					}
					
					
					AlertDialog dialog = dialogBuilder.create();
					dialog.setOnDismissListener(new OnDismissListener() {
						
						@Override
						public void onDismiss(DialogInterface dialog) {
							actionBar.selectTab(actionBar.getTabAt(TAB_HOME));
							mViewPager.setCurrentItem(TAB_HOME);
							
						}
					});
					
					dialog.show();
				}
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {}
			
		};
		
		for (int i = 0; i < fragments.size(); i++) {
			actionBar.addTab(
					actionBar.newTab()
						.setText(fragmentTitles[i])
						.setTabListener(tabListener));
		}
		
	}
	
	@Override
	public void onResume() {
		checkTwoVersions();
		updateSettings();	//update control panel views
		super.onResume();
	}
	

	@Override
	public void onPause() {
		// Stops periodic update of view
		if (mChecker != null) {
			mChecker.cancel(true);
			mChecker = null;
		}
		super.onPause();
	}
	
	// Used to set the tab
	public void setTab(int tab) {
		mViewPager.setCurrentItem(tab);;
	}
	
	// Hitting back on any tab other than the home tab returns home
	@Override
	public void onBackPressed() {
		int tab = mViewPager.getCurrentItem();
		if (tab == TAB_RC || tab == TAB_MSG || tab == TAB_SC) {
			mViewPager.setCurrentItem(TAB_HOME);
		} else {
			super.onBackPressed();
		}
	}
	
	// Updates all the views on the control panel. This needs to be called when settings changed (e.g. an alert
	// is turned off, the app starts snoozing, the keyword for an incoming message changes, or the activity is loaded)
	public void updateSettings() {
		disableEnableWhenOff();
		
		if (PrefHelper.isSnoozing(getApplicationContext()) && (mChecker == null || mChecker.isCancelled())) {
			mChecker = new PeriodicChecker();
			mChecker.execute();
			Log.e("Checker", "Checker made!");
		}
		
		for (int i = 0; i < fragments.size(); i++) {
			TabFragment frag = fragments.get(i);
			if (frag.isUpdatable()) {
				frag.fragUpdateSettings();
			}
		}
	}
	
	// Prevent the user from switching tabs or scrolling when the alerts are snoozed or off
	private void disableEnableWhenOff() {
		if (PrefHelper.isSnoozing(getApplicationContext())
				|| PrefHelper.getState(getApplicationContext(), Constants.APP_STATE) == Constants.URGENT_CALL_STATE_OFF) {
			mViewPager.setCurrentItem(TAB_HOME);
			mViewPager.setScrollable(false);
			mCanChangeTabs = false;
			
		} else {
			mViewPager.setScrollable(true);
			mCanChangeTabs = true;
		}
	}
	
	public class MyPagerAdapter extends FragmentPagerAdapter {
		
		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
		public Fragment getItem(int position) {
			return fragments.get(position);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}
		
	}
	
	// A prompt to snooze
	private void showSnooze() {
		SnoozeDialog snooze = new SnoozeDialog(this, this, 0, 0, true);
		snooze.show();
	}
	
	// A prompt to end snooze app
	public void endSnooze() {
		endSnoozeDialog = new SnoozeEndDialog(this);
		endSnoozeDialog.show();
		endSnoozeDialog.setOnOptionsChangedListener(new OnOptionsChangedListener() {
			
			@Override
			public void onOptionsChanged() {
				updateSettings(); //updates views on control panel
			}
		});

	}	
	
	// Return value from the snooze prompt
	@Override
	public void onTimeSet(TimePicker view, int hours, int minutes) {
		long snoozeTime = hours * 3600000 + minutes * 60000;
		//TODO: Hack! Added 1/2 s to make snooze time show up correctly when first set.
		if (snoozeTime > 0) {
			snoozeTime += 500;
		}
		PrefHelper.setSnoozeTime(getApplicationContext(), snoozeTime);
		updateSettings(); // updates views on control panel
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
	
	// Generate message and intent to share app with other users
	private void share() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.share_type_subject))
				.setItems(R.array.share_types, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent shareIntent = new Intent();
						
						String message = "";
						String subject = "";
						String intentPickerTitle = "";
						
						switch (which) {
						case 0:
						default:
							subject = getString(R.string.share_uc_subject);
							message = getString(R.string.share_uc_1) + getString(R.string.share_app_url);
							intentPickerTitle = getString(R.string.share_uc_by);
							break;
						case 1:
							subject = getString(R.string.share_msg_subject);
							message += getString(R.string.share_msg_1) + "\"" + PrefHelper.getMessageToken(getApplicationContext()) + "\"";
							message += getString(R.string.share_msg_2) + getString(R.string.share_app_url);
							intentPickerTitle = getString(R.string.share_msg_by);							
							break;
						case 2:
							subject = getString(R.string.share_rc_subject);
							message += getString(R.string.share_rc_1) + PrefHelper.getRepeatedCallQty(getApplicationContext());
							message += getString(R.string.share_rc_2) + PrefHelper.getRepeatedCallMins(getApplicationContext());
							message += getString(R.string.share_rc_3) + getString(R.string.share_app_url);
							intentPickerTitle = getString(R.string.share_rc_by);
							break;
						case 3:
							subject = getString(R.string.share_sc_subject);
							message = getString(R.string.share_sc_1) + getString(R.string.share_app_url);
							intentPickerTitle = getString(R.string.share_sc_by);							
							break;
						}

						shareIntent.setAction(Intent.ACTION_SEND);
						shareIntent.setType("text/plain");
						shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
						shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
						shareIntent.putExtra(Intent.EXTRA_TEXT, message);
						startActivity(Intent.createChooser(shareIntent, intentPickerTitle));
						
					}
				});
		builder.create().show();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    int itemId = item.getItemId();
		if (itemId == R.id.action_settings) {
			Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
			startActivity(i);
			return true;
		} else
		if (itemId == R.id.action_snooze) {
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
	
	// Updates the values showed in the control panel every second (useful when snoozing)
	private class PeriodicChecker extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... voids) {
			while (!isCancelled() && PrefHelper.isSnoozing(getApplicationContext())) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				runOnUiThread(new Runnable() { public void run() {updateSettings();}});
				Log.e("Time:",""+PrefHelper.snoozeRemaining(getApplicationContext()));
				if (!(PrefHelper.isSnoozing(getApplicationContext()))) {
					endSnoozeDialog.cancel();
				}
			}
			cancel(true);
			return null;
		}
	}
	
	// Check for both pro and lite being installed, and remove lite if so
	public void checkTwoVersions() {
		
		// Check for lite and pro
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

		// Uninstall lite if both lite and pro installed
		if (lite && pro) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			
			alertDialogBuilder
				.setTitle(getString(R.string.pro_installed_title))
				.setMessage(getString(R.string.pro_installed_body))
				.setCancelable(false)
				.setPositiveButton(getString(R.string.pro_installed_ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// Uninstalls lite
						Uri pkg_uri = Uri.parse("package:com.mmarvick.urgentcall_lite");
						Intent removeIntent = new Intent(Intent.ACTION_DELETE, pkg_uri);
						startActivity(removeIntent);
					}
				});
			
			AlertDialog versionsDialog = alertDialogBuilder.create();
			versionsDialog.show();
		}
	}
	
	// Checks if the current disclaimer has been agreed to
	public void checkDisclaimer() {
		if (!(PrefHelper.disclaimerCheck(getApplicationContext()))) {
			
			//Save the app state (on/off) as backup, and then turn off
			PrefHelper.disclaimerSaveBackup(getApplicationContext());
			
			//Create and show alert dialog with waiver
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
			
			alertDialogBuilder
				.setTitle(getString(R.string.disclaimer_title))
				.setMessage(getString(R.string.disclaimer_body))
				.setCancelable(false)
				.setPositiveButton(getString(R.string.disclaimer_agree), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// Set that the disclaimer was agreed, and resume phone state from backup
						PrefHelper.disclaimerAgreed(getApplicationContext());
						PrefHelper.disclaimerResumeBackup(getApplicationContext());
						
						// Refresh values due to state change
						updateSettings();
					}
				})
				.setNegativeButton(getString(R.string.disclaimer_disagree), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						finish();
						
					}
				});
			
			AlertDialog disclaimerDialog = alertDialogBuilder.create();
			disclaimerDialog.show();			
		}
	}	
	
}
