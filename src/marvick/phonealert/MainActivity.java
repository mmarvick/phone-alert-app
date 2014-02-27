package marvick.phonealert;

import java.util.ArrayList;

import android.os.Bundle;
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
	private String[] mContactNames;
	private String[] mContactLookUps;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		loadContacts();
		
		//String[] test = new String[1];
		//test[0] = "Default Settings";
		
		
		
		ListView lv = getListView();
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				Intent i = new Intent(getApplicationContext(), SettingActivity.class);
				startActivity(i);
			};	
		});
		
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
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item_contact, mContactLookUps));
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
		for (int i = 0; i < mContactLookUpsObj.length; i++)
			mContactLookUps[i] = (String) mContactLookUpsObj[i];
		
	}
	
	private void createUser() {
		
	}
}
