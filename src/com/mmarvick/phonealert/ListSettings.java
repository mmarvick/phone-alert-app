package com.mmarvick.phonealert;


import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ListSettings extends ListActivity {
	private final int REQUEST_CONTACT = 1;
	
	private String[] mContactNames;
	private String[] mContactLookups;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getListView().setFooterDividersEnabled(true);
		
		TextView footer = (TextView) getLayoutInflater().inflate(R.layout.list_item_contact,  null).findViewById(R.id.list_contact_name);
		getListView().addFooterView(footer);
		
		footer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				createUser();
			}
		});		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		loadContacts();
		
		ListView lv = getListView();
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				Intent i = new Intent(getApplicationContext(), SettingActivity.class);
				i.putExtra("lookup", mContactLookups[position]);
				startActivity(i);
			};	
		});
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item_contact, mContactNames));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_bug:
	        	Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://github.com/mmarvick/phone-alert-app/issues"));
	        	startActivity(i);
	        default:
	        	return true;
	    }
	}		

	private void loadContacts() {
		RulesDbHelper dbHelper = new RulesDbHelper(getApplicationContext());
		mContactLookups = dbHelper.getContactLookups();
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
				Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
				cursor.moveToFirst();
				String lookup = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
				Intent i = new Intent(getApplicationContext(), SettingActivity.class);
				i.putExtra("lookup", lookup);
				startActivity(i);

			}
		}
	}
}
