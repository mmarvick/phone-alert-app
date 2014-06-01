package com.mmarvick.urgentcall.activities;

import java.util.ArrayList;
import java.util.List;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.PrefHelper;
import com.mmarvick.urgentcall.widgets.MyViewPager;
import com.mmarvick.urgentcall.widgets.OnOptionsChangedListener;
import com.mmarvick.urgentcall.widgets.RateDialog;
import com.mmarvick.urgentcall.widgets.SnoozeDialog;
import com.mmarvick.urgentcall.widgets.SnoozeEndDialog;
import com.mmarvick.urgentcall.widgets.UpgradeDialog;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity 
	implements TimePickerDialog.OnTimeSetListener{
	
	public ArrayList<TabFragment> fragments;
	public String[] fragmentTitles;
	
	private MyPagerAdapter mAdapter;
	private SnoozeEndDialog endSnoozeDialog;
	private MyViewPager mViewPager;
	private PeriodicChecker mChecker;
	
	private boolean mCanChangeTabs;
	
	private ActionBar actionBar;
	
	public static final int TAB_HOME = 0;
	public static final int TAB_MSG = 1;
	public static final int TAB_RC = 2;
	public static final int TAB_SC = 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		fragments = new ArrayList<TabFragment>();
		fragments.add((TabFragment) (new HomeFragment()));
		fragments.add((TabFragment) (new MessageFragment()));	
		fragments.add((TabFragment) (new RepeatCallFragment()));
		fragments.add((TabFragment) (new SingleCallFragment()));
		fragmentTitles = new String[] {"Home", "Text", "Repeat\nCall", "Single\nCall"};
		
		mAdapter = new MyPagerAdapter(getSupportFragmentManager());
		mViewPager = (MyViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				getSupportActionBar().setSelectedNavigationItem(position);
			}
		});
		
		actionBar = getSupportActionBar();
		
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				mViewPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		for (int i = 0; i < fragmentTitles.length; i++) {
			actionBar.addTab(
					actionBar.newTab()
							.setText(fragmentTitles[i])
							.setTabListener(tabListener));
		}
		
	}
	
	@Override
	public void onResume() {
		updateSettings();
		super.onResume();
	}
	
	@Override
	public void onPause() {
		if (mChecker != null) {
			mChecker.cancel(true);
			mChecker = null;
		}
		super.onPause();
	}
	
	public void setTab(int tab) {
		mViewPager.setCurrentItem(tab);;
	}
	
	@Override
	public void onBackPressed() {
		int tab = mViewPager.getCurrentItem();
		if (tab == TAB_RC || tab == TAB_MSG || tab == TAB_SC) {
			mViewPager.setCurrentItem(TAB_HOME);
		} else {
			super.onBackPressed();
		}
	}
	
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
	
	private void disableEnableWhenOff() {
		if (PrefHelper.isSnoozing(getApplicationContext())
				|| PrefHelper.getState(getApplicationContext(), Constants.OVERALL_STATE) == Constants.URGENT_CALL_STATE_OFF) {
			mViewPager.setCurrentItem(TAB_HOME);
			mViewPager.setScrollable(false);
			
		} else {
			mViewPager.setScrollable(true);
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
	

	private void showSnooze() {
		if (getResources().getBoolean(R.bool.paid_version)) {
			SnoozeDialog snooze = new SnoozeDialog(this, this, 0, 0, true);
			snooze.show();
		} else {
			UpgradeDialog.upgradeDialog(this, getString(R.string.upgrade_body_snooze));
		}
	}
	
	public void endSnooze() {
		endSnoozeDialog = new SnoozeEndDialog(this);
		endSnoozeDialog.show();
		endSnoozeDialog.setOnOptionsChangedListener(new OnOptionsChangedListener() {
			
			@Override
			public void onOptionsChanged() {
				updateSettings();
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
		updateSettings();
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
		String message = getString(R.string.share_1) + PrefHelper.getRepeatedCallQty(getApplicationContext());
		message += getString(R.string.share_2) + PrefHelper.getRepeatedCallMins(getApplicationContext());
		message += getString(R.string.share_3);
		shareIntent.putExtra(Intent.EXTRA_TEXT, message);
		shareIntent.setType("text/plain");
		startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    int itemId = item.getItemId();
		/*if (itemId == R.id.action_settings) {
			Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
			startActivity(i);
			return true;
		} else */
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
			}
			cancel(true);
			return null;
		}
	}
	
}
