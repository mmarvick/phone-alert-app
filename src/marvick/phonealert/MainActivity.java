package marvick.phonealert;

import java.util.ArrayList;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {
	private final int REQUEST_CONTACT = 1;
	
	private String[] mContactNames;
	private String[] mContactLookUps;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getListView().setFooterDividersEnabled(true);
		
		TextView footer = (TextView) getLayoutInflater().inflate(R.layout.list_item_contact,  null).findViewById(R.id.list_contact_name);
		//footer.setText("Add View");
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
				if (mContactLookUps[position].contains(Uri.encode("content://"))) {
					Uri uri = Uri.parse(Uri.decode(mContactLookUps[position]));
					i.setData(uri);
				}
				startActivity(i);
			};	
		});
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item_contact, mContactNames));
	}

	private void loadContacts() {
		RulesDbOpenHelper mDbHelper = new RulesDbOpenHelper(this);
		SQLiteDatabase mRulesDb = mDbHelper.getReadableDatabase();
		//SQLiteQueryBuilder mQBuilder = new SQLiteQueryBuilder();
		
		ArrayList<String> contactIDs = new ArrayList<String>();
		
		//String[] projectionIn = {RulesEntry.COLUMN_NAME_CONTACT_LOOKUP};
		
		//Cursor c = mQBuilder.query(mRulesDb, projectionIn, null, null, null, null, null);
		Cursor c = mRulesDb.rawQuery("SELECT * FROM rules", null);
		
		c.moveToFirst();
		while (!c.isAfterLast()) {
			contactIDs.add(c.getString(1));
			c.moveToNext();
		}
		
		Object[] mContactLookUpsObj = contactIDs.toArray();
		mContactLookUps = new String[mContactLookUpsObj.length];
		mContactNames = new String[mContactLookUps.length];
		for (int i = 0; i < mContactLookUpsObj.length; i++) {
			mContactLookUps[i] = (String) mContactLookUpsObj[i];
		
			//TODO FIX THIS SUPER HACKINESS
			if (i == 0) {
				mContactNames[i] = mContactLookUps[i];
			} else {
				Uri uri = Uri.parse(Uri.decode(mContactLookUps[i]));
				String[] projection = new String[] {ContactsContract.Contacts.DISPLAY_NAME};
				Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
				cursor.moveToFirst();
				mContactNames[i] = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			}
		}
		
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
				Intent i = new Intent(getApplicationContext(), SettingActivity.class);
				i.setData(uri);
				startActivity(i);

			}
		}
	}
}
