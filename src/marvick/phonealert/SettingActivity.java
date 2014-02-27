package marvick.phonealert;

import marvick.phonealert.RulesDbContract.RulesEntry;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingActivity extends Activity {

	public final String SETTING_CALL_QTY = "callQty";
	public final String SETTING_CALL_TIME = "callTime";
	private EditText mCallQty;
	private EditText mCallTime;
	private SharedPreferences mPrefs;
	private Editor mEditor;
	private RulesDbOpenHelper mDbHelper;
	private SQLiteDatabase mRulesDb;
	private Uri uri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		mPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		mEditor = mPrefs.edit();
		mCallQty = (EditText) findViewById(R.id.call_qty_value);
		mCallTime = (EditText) findViewById(R.id.call_time_value);
		Button mSaveButton = (Button) findViewById(R.id.save_button);
		mDbHelper = new RulesDbOpenHelper(this);
		mRulesDb = mDbHelper.getReadableDatabase();	
		Intent intent = getIntent();
		uri = intent.getData();
		
		loadData();
		
		mSaveButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveData();
				finish();
				
			}
		});
	}
	
	private void loadData() {
		if (uri != null) {
			String lookup = Uri.encode(uri.toString());
			Cursor c = mRulesDb.query(RulesEntry.TABLE_NAME, null, RulesEntry.COLUMN_NAME_CONTACT_LOOKUP + "='" + lookup + "'", null, null, null, null);
			if (c.getCount() == 0) {
				mCallQty.setText("3");
				mCallTime.setText("15");
			} else {
				Toast.makeText(getApplicationContext(), "This user already exists!", Toast.LENGTH_LONG).show();
				c.moveToFirst();
				mCallQty.setText("" + c.getInt(c.getColumnIndex(RulesEntry.COLUMN_NAME_CALLS)));
				mCallTime.setText("" + c.getInt(c.getColumnIndex(RulesEntry.COLUMN_NAME_MINS)));
			}
		} else {
			mCallQty.setText("" + mPrefs.getInt(SETTING_CALL_QTY, 3));
			mCallTime.setText("" + mPrefs.getInt(SETTING_CALL_TIME, 15));
			Toast.makeText(getApplicationContext(), "Pulling from default file", Toast.LENGTH_LONG).show();
		}
	}

	private void saveData() {
		String[] columns = {RulesEntry.COLUMN_NAME_CONTACT_LOOKUP};
		if (uri != null) {
			String lookup = Uri.encode(uri.toString());
			Cursor c = mRulesDb.query(RulesEntry.TABLE_NAME, columns, RulesEntry.COLUMN_NAME_CONTACT_LOOKUP + "='" + lookup + "'", null, null, null, null);
			if (c.getCount() == 0) {
				ContentValues values = new ContentValues();
				values.put(RulesEntry.COLUMN_NAME_CONTACT_LOOKUP, lookup);
				values.put(RulesEntry.COLUMN_NAME_CALLS, Integer.parseInt(mCallQty.getText().toString()));
				values.put(RulesEntry.COLUMN_NAME_MINS, Integer.parseInt(mCallTime.getText().toString()));
				mRulesDb.insert(RulesEntry.TABLE_NAME, null, values);
			} 
			ContentValues values = new ContentValues();
			values.put(RulesEntry.COLUMN_NAME_CONTACT_LOOKUP, lookup);
			values.put(RulesEntry.COLUMN_NAME_CALLS, Integer.parseInt(mCallQty.getText().toString()));
			values.put(RulesEntry.COLUMN_NAME_MINS, Integer.parseInt(mCallTime.getText().toString()));
			mRulesDb.update(RulesEntry.TABLE_NAME, values, RulesEntry.COLUMN_NAME_CONTACT_LOOKUP + "='" + lookup + "'", null);
		} else {
			mEditor.putInt(SETTING_CALL_QTY, Integer.parseInt(mCallQty.getText().toString()));
			mEditor.putInt(SETTING_CALL_TIME, Integer.parseInt(mCallTime.getText().toString()));
			mEditor.commit();	
		}
	}
}
