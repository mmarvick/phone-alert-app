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
		String listType = "";
		String alertTypeDescrip = "";
		int userState = getIntent().getIntExtra(Constants.USER_STATE, RulesEntry.STATE_ON);
		
		switch (userState) {
		case (RulesEntry.STATE_ON):
			listType = getString(R.string.list_fragment_list_allowed);
			break;
		case (RulesEntry.STATE_OFF):
			listType = getString(R.string.list_fragment_list_blocked);
			break;
		}
		
		
		if (alertType.equals(RulesEntry.MSG_STATE)) {
			alertTypeDescrip = getString(R.string.list_fragment_alert_msg);
		} else if (alertType.equals(RulesEntry.RC_STATE)) {
			alertTypeDescrip = getString(R.string.list_fragment_alert_rc);
		} else if (alertType.equals(RulesEntry.SC_STATE)) {
			alertTypeDescrip = getString(R.string.list_fragment_alert_sc);
		}
		
		actionBar.setTitle(listType + " " + alertTypeDescrip);	
	}
	
	public void refresh() {
		finish();
		startActivity(getIntent());
	}
	

}
