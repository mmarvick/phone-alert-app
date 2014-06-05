package com.mmarvick.urgentcall.activities;


import java.util.ArrayList;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.RulesDbHelper;
import com.mmarvick.urgentcall.data.RulesDbContract.RulesEntry;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ListFragment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class ContactListFragment extends ListFragment {
	
	private String[] mContactNames;
	private String[] mContactLookups;
	
	String alertType;
	int userState;
	
	private RulesDbHelper dbHelper;
	
	private final int REQUEST_CONTACT = 1;	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		
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
		
		// Load contacts
		dbHelper = new RulesDbHelper(getActivity().getApplicationContext());		
		alertType = getActivity().getIntent().getStringExtra(Constants.ALERT_TYPE);
		userState = getActivity().getIntent().getIntExtra(Constants.USER_STATE, RulesEntry.STATE_ON);
		loadContacts();
		
		// Put contacts into the ListView
		ContactsArrayAdapter adapter = new ContactsArrayAdapter(getActivity(), mContactNames);
		setListAdapter(adapter);
	}
	
	@Override
	public void onPause() {
		dbHelper.close();
		super.onPause();
	}

	// Load contacts from database
	// Must load database and alertType and userState before calling
	private void loadContacts() {
		String[][] namesLookupsString;
		namesLookupsString = dbHelper.getNamesLookups(alertType, userState);
		mContactLookups = namesLookupsString[0];
		mContactNames = namesLookupsString[1];
	}
	
	// Add a user to list
	// Must instantiate alertType and userState before calling
	private void createUser() {
		Intent contactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(contactIntent, REQUEST_CONTACT);
	}
	
	// Add user to database when returned from picker
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		switch (reqCode) {
		case (REQUEST_CONTACT):
			if (resultCode == Activity.RESULT_OK){
				//Get contact lookup key
				Uri uri = data.getData();
				String[] projection = new String[] {ContactsContract.Contacts.LOOKUP_KEY};
				Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
				cursor.moveToFirst();
				String lookup = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
				
				//Add to database
				dbHelper.setContactStateForAlert(alertType, lookup, userState);

			}
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    inflater.inflate(R.menu.list_actions, menu);
	    super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    int itemId = item.getItemId();
		if (itemId == R.id.action_add_contact) {
			createUser();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}	
	
	// Adapter for showing contacts in the ListView
	private class ContactsArrayAdapter extends ArrayAdapter<String> {
		private final Context context;
		private final ArrayList<String> values;

		public ContactsArrayAdapter(Context context, String[] values) {
			super(context, R.layout.list_item_contact, values);
			this.context = context;
			this.values = new ArrayList<String>();
			for (String s : values) {
				this.values.add(s);
			}
		}
		
		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			
			LayoutInflater inflater = LayoutInflater.from(context);
			View rowView = inflater.inflate(R.layout.list_item_contact, null);
			
			// Set contact name
			TextView textView = (TextView) rowView.findViewById(R.id.list_contact_name_simple);
			textView.setText(values.get(position));
			
			// Create delete action
			ImageButton imageButton = (ImageButton) rowView.findViewById(R.id.list_contact_delete_button);
			imageButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					String lookup = mContactLookups[position];
					
					dbHelper.removeContactForAlertType(alertType, lookup);
					//TODO: Improve this so that we don't have to refresh the activity when a user is removed
					((ContactListActivity) getActivity()).refresh();
					
				};	
			});
			
			return rowView;
		}
		
	}
	
}
