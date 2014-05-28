package com.mmarvick.urgentcall;

import java.util.ArrayList;

import com.mmarvick.urgentcall.RulesDbContract.RulesEntry;

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
	
	public String[] getContactLookups(boolean state) {
		String moreWhere = RulesDbContract.RulesEntry.COLUMN_NAME_ON + " = ";
		if (state) {
			moreWhere += "'1'";
		} else {
			moreWhere += "'0'";
		}
		return getContactLookups(moreWhere);
	}
	
	private String[] getContactLookups(String moreWhere) {
		open();
		ArrayList<String> contactIDs = new ArrayList<String>();
		Cursor c = mRulesDb.rawQuery("SELECT * FROM rules WHERE " + moreWhere, null);
		
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
	
	public String[][] getNamesLookups(boolean state) {
		String[] lookups = getContactLookups(state);
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
	
	public boolean isInDb(String lookup) {
		open();
		Cursor c = mRulesDb.query(RulesEntry.TABLE_NAME, null, RulesEntry.COLUMN_NAME_CONTACT_LOOKUP + "='" + lookup + "'", null, null, null, null);
		return c.getCount() > 0;
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
	
	public boolean getState(String lookup) {
		open();
		String[] columns = new String[] {RulesEntry.COLUMN_NAME_ON};
		Cursor c = mRulesDb.query(RulesEntry.TABLE_NAME, columns, RulesEntry.COLUMN_NAME_CONTACT_LOOKUP + "='" + lookup + "'", null, null, null, null);
		c.moveToFirst();
		return c.getInt(c.getColumnIndex(RulesEntry.COLUMN_NAME_ON)) == 1;
	}
	
	public void makeContact(String lookup, boolean state) {
		open();
		if (!isInDb(lookup)) {
			ContentValues values = new ContentValues();
			values.put(RulesEntry.COLUMN_NAME_CONTACT_LOOKUP, lookup);		
			mRulesDb.insert(RulesEntry.TABLE_NAME, null, values);
		} 
		ContentValues values = new ContentValues();
		values.put(RulesEntry.COLUMN_NAME_CONTACT_LOOKUP, lookup);
		values.put(RulesEntry.COLUMN_NAME_ON, (state? 1:0));
		mRulesDb.update(RulesEntry.TABLE_NAME, values, RulesEntry.COLUMN_NAME_CONTACT_LOOKUP + "='" + lookup + "'", null);
	}
	
	public void deleteContact(String lookup) {
		open();
		if (isInDb(lookup)) {
			mRulesDb.delete(RulesEntry.TABLE_NAME, RulesEntry.COLUMN_NAME_CONTACT_LOOKUP + "=?", new String[] {lookup});
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
