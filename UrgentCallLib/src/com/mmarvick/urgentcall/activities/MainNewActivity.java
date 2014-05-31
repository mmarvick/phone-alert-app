package com.mmarvick.urgentcall.activities;

import java.util.ArrayList;
import java.util.List;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.PrefHelper;
import com.mmarvick.urgentcall.widgets.RateDialog;
import com.mmarvick.urgentcall.widgets.SnoozeDialog;
import com.mmarvick.urgentcall.widgets.SnoozeEndDialog;
import com.mmarvick.urgentcall.widgets.UpgradeDialog;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
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
import android.widget.Toast;

public class MainNewActivity extends ActionBarActivity 
	implements TimePickerDialog.OnTimeSetListener{
	
	public ArrayList<TabFragment> fragments;
	public String[] fragmentTitles;
	
	private MyPagerAdapter mAdapter;
	private SnoozeEndDialog endSnoozeDialog;
	private ViewPager mViewPager;
	
	public static final int TAB_HOME = 0;
	public static final int TAB_MSG = 1;
	public static final int TAB_CALL = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_main);

		fragments = new ArrayList<TabFragment>();
		fragments.add((TabFragment) (new HomeFragment()));
		fragments.add((TabFragment) (new MessageFragment()));	
		fragments.add((TabFragment) (new CallFragment()));
		fragmentTitles = new String[] {"Home", "Message\nAlert", "Call\nAlert"};
		
		final ActionBar actionBar = getSupportActionBar();
		
		mAdapter = new MyPagerAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				getSupportActionBar().setSelectedNavigationItem(position);
			}
		});
		

		
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
	
	public void setTab(int tab) {
		mViewPager.setCurrentItem(tab);;
	}
	
	@Override
	public void onBackPressed() {
		int tab = mViewPager.getCurrentItem();
		if (tab == TAB_CALL || tab == TAB_MSG) {
			mViewPager.setCurrentItem(TAB_HOME);
		} else {
			super.onBackPressed();
		}
	}
	
	public void updateSettings() {
		for (int i = 0; i < fragments.size(); i++) {
			TabFragment frag = fragments.get(i);
			if (frag.isUpdatable()) {
				frag.fragUpdateSettings();
			}
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
		endSnoozeDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				
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
		} else if (itemId == R.id.action_switch) {
			PrefHelper.setView(getApplicationContext(), Constants.VIEW_OLD);
			finish();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}	
	
}
