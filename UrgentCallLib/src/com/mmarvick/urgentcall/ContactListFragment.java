package com.mmarvick.urgentcall;


import com.mmarvick.urgentcall.R;

import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NavUtils;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ContactListFragment extends ListFragment {
	
	private final int REQUEST_CONTACT = 1;
	
	private String[] mContactNames;
	private String[] mContactLookups;
	
	private RulesDbHelper dbHelper;
	private SharedPreferences pref;
	private int mode;
	private int state;
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		dbHelper = new RulesDbHelper(getActivity().getApplicationContext());
		
		getListView().setFooterDividersEnabled(true);
		
		TextView header = (TextView) getActivity().getLayoutInflater().inflate(R.layout.list_item_contact_adv,  null).findViewById(R.id.list_contact_name);
		getListView().addHeaderView(header);
		
		header.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				createUser();
			}
		});
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mode = pref.getInt(Constants.MODE, Constants.MODE_SIMPLE);
		state = pref.getInt(Constants.SIMPLE_STATE, Constants.SIMPLE_STATE_ON);
		loadContacts();
		
		final ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				String lookup = mContactLookups[position-1]; //Subtract 1 due to header
				
				if (mode == Constants.MODE_ADVANCED) { 
					Intent i = new Intent(getActivity().getApplicationContext(), UserSettingActivity.class);
					i.putExtra("lookup", lookup); 
					startActivity(i);
				} else {
					dbHelper.deleteContact(lookup);
					((ContactListActivity) getActivity()).refresh();
				}
			};	
		});
		//if (mode == Constants.MODE_ADVANCED)
			setListAdapter(new ArrayAdapter<String>(getActivity(), R.layout.list_item_contact_adv, mContactNames));
		//else
			//setListAdapter(new ArrayAdapter<String>(getActivity(), R.layout.list_item_contact_simple, mContactNames));
	}	

	private void loadContacts() {
		if (mode == Constants.MODE_SIMPLE) {
			if (state == Constants.SIMPLE_STATE_WHITELIST) { 
				mContactLookups = dbHelper.getContactLookups(true);
			} else if (state == Constants.SIMPLE_STATE_BLACKLIST) {
				mContactLookups = dbHelper.getContactLookups(false);
			}
		} else {
			mContactLookups = dbHelper.getContactLookups();
		}
		mContactNames = dbHelper.getNames(mContactLookups);
	}
	
	private void createUser() {
		Intent contactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(contactIntent, REQUEST_CONTACT);
	}
	
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		switch (reqCode) {
		case (REQUEST_CONTACT):
			if (resultCode == Activity.RESULT_OK){
				Uri uri = data.getData();
				String[] projection = new String[] {ContactsContract.Contacts.LOOKUP_KEY};
				Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
				cursor.moveToFirst();
				String lookup = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
				
				if (mode == Constants.MODE_ADVANCED) {
					Intent i = new Intent(getActivity().getApplicationContext(), UserSettingActivity.class);
					i.putExtra("lookup", lookup);
					startActivity(i);
				} else {
					if (dbHelper.isInDb(lookup)) {
						//alert
					} else {
						//just add
					}
					
					dbHelper.makeContact(lookup, 3, 15, (state == Constants.SIMPLE_STATE_WHITELIST));
				}

			}
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    inflater.inflate(R.menu.main, menu);
	    super.onCreateOptionsMenu(menu,  inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    	case android.R.id.home:
	    		NavUtils.navigateUpFromSameTask(getActivity());
	    		return true;
	        default:
	        	return true;
	    }
	}		
	
}