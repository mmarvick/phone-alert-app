package com.mmarvick.urgentcall.activities;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.RulesDbContract.RulesEntry;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;


public class ContactListActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		String alertType = getIntent().getStringExtra(Constants.ALERT_TYPE);
		String listType;
		String alertTypeDescrip;
		int userState = getIntent().getIntExtra(Constants.USER_STATE, RulesEntry.STATE_ON);
		
		if (userState == RulesEntry.STATE_ON){
			listType = "Whitelist";
		} else {
			listType = "Blacklist";
		}
		
		if (alertType.equals(RulesEntry.MSG_STATE)) {
			alertTypeDescrip = "Message Alert";
		} else if (alertType.equals(RulesEntry.RC_STATE)) {
			alertTypeDescrip = "Repeat Call Alert";
		} else if (alertType.equals(RulesEntry.SC_STATE)) {
			alertTypeDescrip = "Single Call Alert";
		} else {
			alertTypeDescrip = alertType;
		}
		
		actionBar.setTitle(alertTypeDescrip + " " + listType);	
	}
	
	public void refresh() {
		finish();
		startActivity(getIntent());
	}
	

}
