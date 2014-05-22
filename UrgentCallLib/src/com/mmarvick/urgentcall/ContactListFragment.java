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
	private int state;
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		dbHelper = new RulesDbHelper(getActivity().getApplicationContext());
		
		state = getActivity().getIntent().getIntExtra(Constants.LIST_TYPE, Constants.LIST_WHITELIST);
		
		getListView().setFooterDividersEnabled(true);
		
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		 setHasOptionsMenu(true);
		 return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		loadContacts();
		
		final ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				String lookup = mContactLookups[position];
				
				dbHelper.deleteContact(lookup);
				((ContactListActivity) getActivity()).refresh();
			};	
		});

		setListAdapter(new ArrayAdapter<String>(getActivity(), R.layout.list_item_contact_adv, mContactNames));
	}	

	private void loadContacts() {
		if (state == Constants.LIST_WHITELIST) { 
			mContactLookups = dbHelper.getContactLookups(true);
		} else if (state == Constants.LIST_BLACKLIST) {
			mContactLookups = dbHelper.getContactLookups(false);
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

				if (dbHelper.isInDb(lookup)) {
					//TODO: alert
				} else {
					//just add
				}
				
				dbHelper.makeContact(lookup, (state == Constants.LIST_WHITELIST));

			}
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    // Inflate the menu items for use in the action bar
	    inflater.inflate(R.menu.list_actions, menu);
	    super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    int itemId = item.getItemId();
		if (itemId == R.id.action_add_contact) {
			createUser();
			return true;
		}
		else {
			return super.onOptionsItemSelected(item);
		}
	}	
	
}
