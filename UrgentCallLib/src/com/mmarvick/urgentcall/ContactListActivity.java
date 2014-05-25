package com.mmarvick.urgentcall;

import com.mmarvick.urgentcall.R;

import android.os.Build;
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
			actionBar.setTitle("Edit Whitelist");	
		} else {
			actionBar.setTitle("Edit Blacklist");
		}
	}
	
	public void refresh() {
		finish();
		startActivity(getIntent());
	}
	

}
