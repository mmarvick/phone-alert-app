package com.mmarvick.urgentcall.data;

import java.util.ArrayList;

import com.mmarvick.urgentcall.data.DbContractOldDatabase.RulesEntryOld;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;

public class RulesDbHelper {
	
	Context context;
	SQLiteDatabase mRulesDb;
	ContentResolver mContentResolver;
	RulesDbOpenHelper mDbHelper;
	
	private final String CONTACT_LOOKUP = ContactsContract.Contacts.LOOKUP_KEY;
	private final String CONTACT_NAME = ContactsContract.Contacts.DISPLAY_NAME;
	
	public RulesDbHelper(Context context) {
		this.context = context;
		mDbHelper = new RulesDbOpenHelper(context);
		mContentResolver = context.getContentResolver();
	}
	
	public String[][] getNamesLookups(String alertType, int alertState) {
		String[] lookups = getContactLookups(alertType, alertState);
		
		if (lookups.length == 0) return new String[][] {new String[0], new String[0]};
		
		String where = CONTACT_LOOKUP + "='" + lookups[0] + "'";
		for (int i = 1; i < lookups.length; i++) {
			where += " OR " + CONTACT_LOOKUP + "='" + lookups[i] + "'";
		}
		
		Cursor cursor = mContentResolver.query(Contacts.CONTENT_URI,
				new String[] {CONTACT_LOOKUP, CONTACT_NAME},
				where,
				null,
				CONTACT_NAME + " ASC");
		
		String[][] data = new String[2][cursor.getCount()];
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			data[0][cursor.getPosition()] = cursor.getString(cursor.getColumnIndex(CONTACT_LOOKUP));
			data[1][cursor.getPosition()] = cursor.getString(cursor.getColumnIndex(CONTACT_NAME));
			cursor.moveToNext();
		}
		
		cursor.close();
		
		return data;
	}	
	
	private String[] getContactLookups(String alertType, int alertState) {
		open();
		ArrayList<String> contactIDs = new ArrayList<String>();
		String query = "SELECT * FROM rules WHERE " + alertType + " = '" + alertState + "'";
		Cursor c = mRulesDb.rawQuery(query, null);
		
		c.moveToFirst();
		
		while (!c.isAfterLast()) {
			contactIDs.add(c.getString(c.getColumnIndex(RulesEntryOld.COLUMN_NAME_CONTACT_LOOKUP)));
			c.moveToNext();
		}
		
		c.close();
		
		Object[] lookupObjects = contactIDs.toArray();
		String[] lookups = new String[lookupObjects.length];
		for (int i = 0; i < lookupObjects.length; i++)
			lookups[i] = (String) lookupObjects[i];
		
		return lookups;			
	}
	
	public int getCount(String alertType, int alertState) {
		open();
		String query = "SELECT * FROM rules WHERE " + alertType + " = '" + alertState + "'";
		Cursor c = mRulesDb.rawQuery(query, null);
		int count = c.getCount();
		
		c.close();
		
		return count;
	}	
	
	public String getName(String lookup) {
		Cursor cursor = mContentResolver.query(Contacts.CONTENT_URI,
				new String[] {CONTACT_NAME},
				CONTACT_LOOKUP + "=?",
				new String[] {lookup}, null);
		cursor.moveToFirst();
		String name = cursor.getString(cursor.getColumnIndex(CONTACT_NAME));
		cursor.close();
		return name;	
	}
	
	// Returns RulesEntry.STATE_DEFAULT if the lookup is null, not in the database, or the value in the db
	// is null or RulesEntry.STATE_DEFAULT for the alertType. Otherwise, returns the user state for that alertType.
	public int getUserState(String alertType, String lookup) {
		if (lookup == null) {
			return RulesEntryOld.STATE_DEFAULT;
		}
		
		open();
		String[] columns = new String[] {alertType};
		Cursor c = mRulesDb.query(RulesEntryOld.TABLE_NAME, columns, RulesEntryOld.COLUMN_NAME_CONTACT_LOOKUP + "='" + lookup + "'", null, null, null, null);
		if (c.getCount() == 0) {
			return RulesEntryOld.STATE_DEFAULT;
		}
		c.moveToFirst();
		if (c.isNull(c.getColumnIndex(alertType))) {
			return RulesEntryOld.STATE_DEFAULT;
		}
		
		int state = c.getInt(c.getColumnIndex(alertType));
		c.close();
		return state;
	}
	
	public void setContactStateForAlert(String alertType, String lookup, int userState) {
		open();
		if (!isInDb(lookup)) {
			ContentValues values = new ContentValues();
			values.put(RulesEntryOld.COLUMN_NAME_CONTACT_LOOKUP, lookup);		
			mRulesDb.insert(RulesEntryOld.TABLE_NAME, null, values);
		} 
		ContentValues values = new ContentValues();
		values.put(alertType, userState);
		mRulesDb.update(RulesEntryOld.TABLE_NAME, values, RulesEntryOld.COLUMN_NAME_CONTACT_LOOKUP + "='" + lookup + "'", null);
	}	
	
	public String getLookupFromNumber(String phoneNumber) {
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		Cursor cursor = mContentResolver.query(uri,
				new String[] {CONTACT_LOOKUP},
				null, null, null);
		if (cursor.getCount() == 0) {
			return null;
		}
		cursor.moveToFirst();
		
		String lookup = cursor.getString(cursor.getColumnIndex(Phone.LOOKUP_KEY));
		cursor.close();
		
		return lookup;
	}
	
	public void removeContactForAlertType(String alertType, String lookup) {
		setContactStateForAlert(alertType, lookup, RulesEntryOld.STATE_DEFAULT);
	}
	
	private boolean isInDb(String lookup) {
		open();
		Cursor c = mRulesDb.query(RulesEntryOld.TABLE_NAME, null, RulesEntryOld.COLUMN_NAME_CONTACT_LOOKUP + "='" + lookup + "'", null, null, null, null);
		boolean isInDb = (c.getCount() == 0);
		c.close();
		
		if (isInDb) {
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
