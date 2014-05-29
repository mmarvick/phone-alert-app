package com.mmarvick.urgentcall.activities;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;

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
		if (getIntent().getExtras().getInt(Constants.LIST_TYPE) == Constants.LIST_WHITELIST) {
			actionBar.setTitle(getString(R.string.title_activity_list_whitelist));	
		} else {
			actionBar.setTitle(getString(R.string.title_activity_list_blacklist));
		}
	}
	
	public void refresh() {
		finish();
		startActivity(getIntent());
	}
	

}
