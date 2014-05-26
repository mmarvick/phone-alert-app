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
	
	public RulesDbHelper(Context context) {
		this.context = context;
		RulesDbOpenHelper mDbHelper = new RulesDbOpenHelper(context);
		mRulesDb = mDbHelper.getReadableDatabase();
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
	
	public String[] getNames(String[] lookups) {
		String[] names = new String[lookups.length];
		
		for (int i = 0; i < lookups.length; i++) {
			names[i] = getName(lookups[i]);
		}
		
		return names;
	}
	
	public String getName(String lookup) {
		Cursor cursor = mContentResolver.query(Data.CONTENT_URI,
				new String[] {Phone.DISPLAY_NAME},
				Data.LOOKUP_KEY + "=?",
				new String[] {lookup}, null);
		cursor.moveToFirst();
		return cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));	
	}
	
	public boolean isInDb(String lookup) {
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
		String[] columns = new String[] {RulesEntry.COLUMN_NAME_ON};
		Cursor c = mRulesDb.query(RulesEntry.TABLE_NAME, columns, RulesEntry.COLUMN_NAME_CONTACT_LOOKUP + "='" + lookup + "'", null, null, null, null);
		c.moveToFirst();
		return c.getInt(c.getColumnIndex(RulesEntry.COLUMN_NAME_ON)) == 1;
	}
	
	public void makeContact(String lookup, boolean state) {
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
		if (isInDb(lookup)) {
			mRulesDb.delete(RulesEntry.TABLE_NAME, RulesEntry.COLUMN_NAME_CONTACT_LOOKUP + "=?", new String[] {lookup});
		}
	}
}
