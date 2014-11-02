package com.mmarvick.urgentcall.activities;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.OldDbContractDatabase.RulesEntryOld;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;


public class ContactListActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);
		
		// Get a string for the title bar depending on if this is a block list or allow list
		int userState = getIntent().getIntExtra(Constants.USER_STATE, RulesEntryOld.STATE_ON);
		String listType = "";
		
		switch (userState) {
		case (RulesEntryOld.STATE_ON):
			listType = getString(R.string.list_fragment_list_allowed);
			break;
		case (RulesEntryOld.STATE_OFF):
			listType = getString(R.string.list_fragment_list_blocked);
			break;
		}
		
		// Get a string for the title bar depending on alert type
		String alertType = getIntent().getStringExtra(Constants.ALERT_TYPE);
		String alertTypeDescrip = "";
		
		if (alertType.equals(RulesEntryOld.MSG_STATE)) {
			alertTypeDescrip = getString(R.string.list_fragment_alert_msg);
		} else if (alertType.equals(RulesEntryOld.RC_STATE)) {
			alertTypeDescrip = getString(R.string.list_fragment_alert_rc);
		} else if (alertType.equals(RulesEntryOld.SC_STATE)) {
			alertTypeDescrip = getString(R.string.list_fragment_alert_sc);
		}
		
		
		// Add an action bar and set title
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);		
		actionBar.setTitle(listType + " " + alertTypeDescrip);	
	}
	
	// Refresh the intent when a value is added/removed from the list.
	// TODO: The list logic should be improved so that this won't be necessary
	public void refresh() {
		finish();
		startActivity(getIntent());
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        finish();
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
}
