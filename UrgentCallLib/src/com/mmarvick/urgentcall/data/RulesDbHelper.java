package com.mmarvick.urgentcall.data;

import java.util.ArrayList;

import com.mmarvick.urgentcall.data.RulesDbContract.RulesEntry;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;

public class RulesDbHelper {
	
	Context context;
	SQLiteDatabase mRulesDb;
	ContentResolver mContentResolver;
	RulesDbOpenHelper mDbHelper;
	
	public RulesDbHelper(Context context) {
		this.context = context;
		mDbHelper = new RulesDbOpenHelper(context);
		mContentResolver = context.getContentResolver();
	}
	
	public String[][] getNamesLookups(String alertType, int alertState) {
		String[] lookups = getContactLookups(alertType, alertState);
		
		if (lookups.length == 0) return new String[][] {new String[0], new String[0]};
		
		String where = Data.LOOKUP_KEY + "='" + lookups[0] + "'";
		for (int i = 1; i < lookups.length; i++) {
			where += " OR " + Data.LOOKUP_KEY + "='" + lookups[i] + "'";
		}
		
		Cursor cursor = mContentResolver.query(Data.CONTENT_URI,
				new String[] {Data.LOOKUP_KEY, Phone.DISPLAY_NAME},
				where,
				null,
				Phone.DISPLAY_NAME + " ASC");
		
		String[][] data = new String[2][cursor.getCount()];
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			data[0][cursor.getPosition()] = cursor.getString(cursor.getColumnIndex(Data.LOOKUP_KEY));
			data[1][cursor.getPosition()] = cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME));
			cursor.moveToNext();
		}
		
		return data;
	}	
	
	private String[] getContactLookups(String alertType, int alertState) {
		open();
		ArrayList<String> contactIDs = new ArrayList<String>();
		String query = "SELECT * FROM rules WHERE " + alertType + " = '" + alertState + "'";
		Cursor c = mRulesDb.rawQuery(query, null);
		
		c.moveToFirst();
		
		while (!c.isAfterLast()) {
			contactIDs.add(c.getString(c.getColumnIndex(RulesEntry.COLUMN_NAME_CONTACT_LOOKUP)));
			c.moveToNext();
		}
		
		Object[] lookupObjects = contactIDs.toArray();
		String[] lookups = new String[lookupObjects.length];
		for (int i = 0; i < lookupObjects.length; i++)
			lookups[i] = (String) lookupObjects[i];
		
		return lookups;			
	}
	
	public String getName(String lookup) {
		Cursor cursor = mContentResolver.query(Data.CONTENT_URI,
				new String[] {Phone.DISPLAY_NAME},
				Data.LOOKUP_KEY + "=?",
				new String[] {lookup}, null);
		cursor.moveToFirst();
		return cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));	
	}
	
	// Returns RulesEntry.STATE_DEFAULT if the lookup is null, not in the database, or the value in the db
	// is null or RulesEntry.STATE_DEFAULT for the alertType. Otherwise, returns the user state for that alertType.
	public int getUserState(String alertType, String lookup) {
		if (lookup == null) {
			return RulesEntry.STATE_DEFAULT;
		}
		
		open();
		String[] columns = new String[] {alertType};
		Cursor c = mRulesDb.query(RulesEntry.TABLE_NAME, columns, RulesEntry.COLUMN_NAME_CONTACT_LOOKUP + "='" + lookup + "'", null, null, null, null);
		if (c.getCount() == 0) {
			return RulesEntry.STATE_DEFAULT;
		}
		c.moveToFirst();
		if (c.isNull(c.getColumnIndex(alertType))) {
			return RulesEntry.STATE_DEFAULT;
		}
		return c.getInt(c.getColumnIndex(alertType));
	}
	
	public void setContactStateForAlert(String alertType, String lookup, int userState) {
		open();
		if (!isInDb(lookup)) {
			ContentValues values = new ContentValues();
			values.put(RulesEntry.COLUMN_NAME_CONTACT_LOOKUP, lookup);		
			mRulesDb.insert(RulesEntry.TABLE_NAME, null, values);
		} 
		ContentValues values = new ContentValues();
		values.put(RulesEntry.COLUMN_NAME_CONTACT_LOOKUP, lookup); //TODO: Is this necessary?
		values.put(alertType, userState);
		mRulesDb.update(RulesEntry.TABLE_NAME, values, RulesEntry.COLUMN_NAME_CONTACT_LOOKUP + "='" + lookup + "'", null);
	}	
	
	public String getLookupFromNumber(String phoneNumber) {
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		Cursor cursor = mContentResolver.query(uri,
				new String[] {Phone.LOOKUP_KEY},
				null, null, null);
		if (cursor.getCount() == 0) {
			return null;
		}
		cursor.moveToFirst();
		return cursor.getString(cursor.getColumnIndex(Phone.LOOKUP_KEY));
	}
	
	public void removeContactForAlertType(String alertType, String lookup) {
		setContactStateForAlert(alertType, lookup, RulesEntry.STATE_DEFAULT);
	}
	
	private boolean isInDb(String lookup) {
		open();
		Cursor c = mRulesDb.query(RulesEntry.TABLE_NAME, null, RulesEntry.COLUMN_NAME_CONTACT_LOOKUP + "='" + lookup + "'", null, null, null, null);
		if (c.getCount() == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public void open() {
		if (mRulesDb == null || !(mRulesDb.isOpen())) {
			mRulesDb = mDbHelper.getReadableDatabase();
		}
	}
	
	public void close() {
		mRulesDb.close();
	}
}
