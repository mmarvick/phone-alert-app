package marvick.phonealert;

import java.util.ArrayList;

import marvick.phonealert.RulesDbContract.RulesEntry;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;

public class RulesDbHelper {
	public static final String NAME_DEFAULT = "Default settings";
	
	SQLiteDatabase mRulesDb;
	ContentResolver mContentResolver;
	
	public RulesDbHelper(Context context) {
		RulesDbOpenHelper mDbHelper = new RulesDbOpenHelper(context);
		mRulesDb = mDbHelper.getReadableDatabase();
		mContentResolver = context.getContentResolver();
	}
	
	public String[] getContactLookups() {
		ArrayList<String> contactIDs = new ArrayList<String>();
		Cursor c = mRulesDb.rawQuery("SELECT * FROM rules", null);
		
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
			
			//TODO FIX THIS SOMEWHAT HACKINESS
			if (lookups[i].equals(RulesEntry.LOOKUP_DEFAULT)) {
				names[i] = NAME_DEFAULT;
			} else {
				Cursor cursor = mContentResolver.query(Data.CONTENT_URI,
						new String[] {Phone.DISPLAY_NAME},
						Data.LOOKUP_KEY + "=?",
						new String[] {lookups[i]}, null);
				cursor.moveToFirst();
				names[i] = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			}
		}
		
		return names;
	}
	
	public boolean isInDb(String lookup) {
		Cursor c = mRulesDb.query(RulesEntry.TABLE_NAME, null, RulesEntry.COLUMN_NAME_CONTACT_LOOKUP + "='" + lookup + "'", null, null, null, null);
		return c.getCount() > 0;
	}
	
	//TODO: This might fail based on how the number is saved in the database
	public String getLookupFromNumber(String phoneNumber) {
		Cursor cursor = mContentResolver.query(Data.CONTENT_URI,
				new String[] {Phone.LOOKUP_KEY},
				Phone.NUMBER + "=?",
				new String[] {phoneNumber}, null);
		if (cursor.getCount() == 0)
			return null;
		cursor.moveToFirst();
		return cursor.getString(cursor.getColumnIndex(Phone.LOOKUP_KEY));
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
	
	public void makeContact(String lookup, int callsAllowed, int callMins) {
		if (!isInDb(lookup)) {
			ContentValues values = new ContentValues();
			values.put(RulesEntry.COLUMN_NAME_CONTACT_LOOKUP, lookup);
			mRulesDb.insert(RulesEntry.TABLE_NAME, null, values);
		} 
		ContentValues values = new ContentValues();
		values.put(RulesEntry.COLUMN_NAME_CONTACT_LOOKUP, lookup);
		values.put(RulesEntry.COLUMN_NAME_CALLS, callsAllowed);
		values.put(RulesEntry.COLUMN_NAME_MINS, callMins);
		mRulesDb.update(RulesEntry.TABLE_NAME, values, RulesEntry.COLUMN_NAME_CONTACT_LOOKUP + "='" + lookup + "'", null);
	}
	
	public void deleteContact(String lookup) {
		if (isInDb(lookup)) {
			mRulesDb.delete(RulesEntry.TABLE_NAME, RulesEntry.COLUMN_NAME_CONTACT_LOOKUP + "=?", new String[] {lookup});
		}
	}
}
