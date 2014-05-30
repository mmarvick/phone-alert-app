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
	
	private final int REQUEST_CONTACT = 1;
	
	private String[] mContactNames;
	private String[] mContactLookups;
	
	private RulesDbHelper dbHelper;
	private String alertType;
	private int userState;
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		alertType = getActivity().getIntent().getStringExtra(Constants.ALERT_TYPE);
		userState = getActivity().getIntent().getIntExtra(Constants.USER_STATE, RulesEntry.STATE_ON);
		
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
		dbHelper = new RulesDbHelper(getActivity().getApplicationContext());
		loadContacts();
		
		ContactsArrayAdapter adapter = new ContactsArrayAdapter(getActivity(), mContactNames);
		setListAdapter(adapter);
	}
	
	@Override
	public void onPause() {
		dbHelper.close();
		super.onPause();
	}

	private void loadContacts() {
		String[][] namesLookupsString;
		namesLookupsString = dbHelper.getNamesLookups(alertType, userState);
		mContactLookups = namesLookupsString[0];
		mContactNames = namesLookupsString[1];
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
				
				dbHelper.setContactStateForAlert(alertType, lookup, userState);

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
			TextView textView = (TextView) rowView.findViewById(R.id.list_contact_name_simple);
			textView.setText(values.get(position));
			
			ImageButton imageButton = (ImageButton) rowView.findViewById(R.id.list_contact_delete_button);
			
			imageButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					String lookup = mContactLookups[position];
					
					dbHelper.removeContactForAlertType(alertType, lookup);
					((ContactListActivity) getActivity()).refresh();
					
				};	
			});
			
			return rowView;
		}
		
	}
	
}
