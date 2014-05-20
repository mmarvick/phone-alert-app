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
import android.widget.Toast;

public class RulesDbHelper {
	public static final String NAME_DEFAULT = "Default settings";
	
	Context context;
	SQLiteDatabase mRulesDb;
	ContentResolver mContentResolver;
	
	public RulesDbHelper(Context context) {
		this.context = context;
		RulesDbOpenHelper mDbHelper = new RulesDbOpenHelper(context);
		mRulesDb = mDbHelper.getReadableDatabase();
		mContentResolver = context.getContentResolver();
	}
	
	public String[] getContactLookups() {
		return getContactLookups("");
	}
	
	public String[] getContactLookups(boolean on) {
		String moreWhere = " AND " + RulesDbContract.RulesEntry.COLUMN_NAME_ON + " = ";
		if (on) {
			moreWhere += "'1'";
		} else {
			moreWhere += "'0'";
		}
		return getContactLookups(moreWhere);
	}
	
	private String[] getContactLookups(String moreWhere) {
		ArrayList<String> contactIDs = new ArrayList<String>();
		Cursor c = mRulesDb.rawQuery("SELECT * FROM rules WHERE " + RulesDbContract.RulesEntry.COLUMN_NAME_SYS_TYPE + " = '0'" + moreWhere, null);
		
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
		if (lookup.equals(RulesEntry.LOOKUP_DEFAULT))
			return NAME_DEFAULT;
		else {
			Cursor cursor = mContentResolver.query(Data.CONTENT_URI,
					new String[] {Phone.DISPLAY_NAME},
					Data.LOOKUP_KEY + "=?",
					new String[] {lookup}, null);
			cursor.moveToFirst();
			return cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));	
		}
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
			return "NOT FUCKING HERE!";
			//return null;
		}
		cursor.moveToFirst();
		return cursor.getString(cursor.getColumnIndex(Phone.LOOKUP_KEY));
	}
	
	public boolean getStateOn(String lookup) {
		String[] columns = new String[] {RulesEntry.COLUMN_NAME_ON};
		Cursor c = mRulesDb.query(RulesEntry.TABLE_NAME, columns, RulesEntry.COLUMN_NAME_CONTACT_LOOKUP + "='" + lookup + "'", null, null, null, null);
		c.moveToFirst();
		return c.getInt(c.getColumnIndex(RulesEntry.COLUMN_NAME_ON)) == 1;
	}
	
	public int getCallsAllowed(String lookup) {
		return getXAllowed(lookup, RulesEntry.COLUMN_NAME_CALLS);
	}
	
	public int getCallMins(String lookup) {
		return getXAllowed(lookup, RulesEntry.COLUMN_NAME_MINS);
	}	
	
	private int getXAllowed(String lookup, String column) {
		String[] columns = new String[] {column};
		Cursor c = mRulesDb.query(RulesEntry.TABLE_NAME, columns, RulesEntry.COLUMN_NAME_CONTACT_LOOKUP + "='" + lookup + "'", null, null, null, null);
		c.moveToFirst();
		return c.getInt(c.getColumnIndex(column));
	}
	
	public void makeContact(String lookup, int callsAllowed, int callMins, boolean stateOn) {
		if (!isInDb(lookup)) {
			ContentValues values = new ContentValues();
			values.put(RulesEntry.COLUMN_NAME_CONTACT_LOOKUP, lookup);
			values.put(RulesEntry.COLUMN_NAME_SYS_TYPE, 0);			
			mRulesDb.insert(RulesEntry.TABLE_NAME, null, values);
		} 
		ContentValues values = new ContentValues();
		values.put(RulesEntry.COLUMN_NAME_CONTACT_LOOKUP, lookup);
		values.put(RulesEntry.COLUMN_NAME_CALLS, callsAllowed);
		values.put(RulesEntry.COLUMN_NAME_MINS, callMins);
		values.put(RulesEntry.COLUMN_NAME_ON, (stateOn? 1:0));
		mRulesDb.update(RulesEntry.TABLE_NAME, values, RulesEntry.COLUMN_NAME_CONTACT_LOOKUP + "='" + lookup + "'", null);
	}
	
	public void deleteContact(String lookup) {
		if (isInDb(lookup)) {
			mRulesDb.delete(RulesEntry.TABLE_NAME, RulesEntry.COLUMN_NAME_CONTACT_LOOKUP + "=?", new String[] {lookup});
		}
	}
}
