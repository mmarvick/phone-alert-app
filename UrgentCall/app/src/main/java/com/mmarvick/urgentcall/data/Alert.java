package com.mmarvick.urgentcall.data;

import java.util.ArrayList;
import java.util.List;

import com.mmarvick.urgentcall.data.DbContract.RuleContactEntry;
import com.mmarvick.urgentcall.data.DbContract.RuleEntry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;

/** Object that represents one alert (call or text) rule, keeps track of
 * information associated with it, and saves changes to the database when updated.
 */
public abstract class Alert {
	/** The column for a contact's contact in the phone's contacts database */
	private final String CONTACT_LOOKUP = ContactsContract.Contacts.LOOKUP_KEY;
	
	/** The column for a contact's name in the phone's contacts database */
	private final String CONTACT_NAME = ContactsContract.Contacts.DISPLAY_NAME;	
	
	/** The context of the alert */
	protected Context mContext;
	
	/** The id corresponding to the row of the alert */
	protected long mRuleId;
	
	/** Constructor for an Alert not currently in the database, with all
	 * initial values generated as defaults. Also adds the Alert to the
	 * database.
	 * 
	 * @param context the current context
	 */	
	public Alert(Context context) {
		this(context, null, false);
	}
	
	/** Constructor for an Alert not currently in the database, with all
	 * initial values generated as defaults. Also adds the Alert to the
	 * database.
	 * 
	 * @param context the current context
	 * @param db a writable database for call rules
	 * @param isInitial <code>true</code> if is the initial rule created when
	 * the application runs for the first time; <code>false</code> if not 
	 */	
	public Alert(Context context, SQLiteDatabase db, boolean isInitial) {
		mContext = context;

        String title;

		if (!isInitial) {
			title = getNewAlertName();
		} else {
			title = getAlertTypeName();
		}
		
		boolean onState = true;
		int filterBy = DbContract.ENTRY_FILTER_BY_EVERYONE;
		boolean ring = true;
		boolean vibrate = false;
		int volume = 1000;
		String tone = Settings.System.DEFAULT_ALARM_ALERT_URI.toString();
		
		ContentValues ruleValues = new ContentValues();
		ruleValues.put(RuleEntry.COLUMN_TITLE, title);
		ruleValues.put(RuleEntry.COLUMN_ON_STATE, onState);
		ruleValues.put(RuleEntry.COLUMN_FILTER_BY, filterBy);
		ruleValues.put(RuleEntry.COLUMN_RING, ring);
		ruleValues.put(RuleEntry.COLUMN_VIBRATE, vibrate);
		ruleValues.put(RuleEntry.COLUMN_VOLUME, volume);
		ruleValues.put(RuleEntry.COLUMN_TONE, tone);
		
		initializeAndStoreRemainingRuleData(ruleValues);
		
		boolean closeWhenDone = false;
		
		if (db == null) {
			db = getWritableDatabase();
			closeWhenDone = true;
		}
		mRuleId = db.insert(getRuleTableName(), null, ruleValues);
		
		if (closeWhenDone) {
			db.close();
		}
	}
	
	/** Constructor for an Alert already in the database.
	 * 
	 * @param context (required) context of the alert
	 * @param id (required) row number of the alert
	 * @throws IndexOutOfBoundsException no row exists in the alert table
	 * with that id
	 */
	public Alert(Context context, long id) {
		mContext = context;
        mRuleId = id;
	}

    protected Cursor getRuleCursor() {
        SQLiteDatabase database = getReadableDatabase();
        Cursor ruleCursor = database.query(getRuleTableName(),
                null,
                RuleEntry._ID + " =  " + mRuleId,
                null, null, null, null);

        if (!ruleCursor.moveToFirst()) {
            throw new IndexOutOfBoundsException();
        }

        return ruleCursor;
    }
	
	/** Gets a readable database for tables corresponding to the alert
	 * @return the readable database */
	protected abstract SQLiteDatabase getReadableDatabase();
	
	/** Gets a writable database for tables corresponding to the alert
	 * @return the writable database */
	protected abstract SQLiteDatabase getWritableDatabase();
	
	/** Gets the name of the table corresponding to each alert
	 * @return the rule table name
	 */
	protected abstract String getRuleTableName();
	
	/** Gets the name of the table corresponding to the contacts that an alert
	 * applies to.
	 * @return the rule contact table name
	 */
	protected abstract String getContactTableName();

	/** Initializes the remaining information that is alert-specific (not common to
	 * all types of alerts), and stores into the ContentValues as key-value
	 * pairs to be saved to the database.
	 * @param ruleValues the repository of key-value pairs to save in the database
	 */	
	protected abstract void initializeAndStoreRemainingRuleData(ContentValues ruleValues);
	
	/** Drops any additional information from the database that is alert-specific
	 * (not common to all types of alerts).
	 */
	protected abstract void performRemainingDropCommands(SQLiteDatabase db);
	
	/** Get the name of the alert for a specific alert type
	 * @return the name of the type of alert
	 */
	protected abstract String getAlertTypeName();
	
	/** Get the identifier of the alert for a specific alert type
	 * @return the name of the type of alert
	 */
	public abstract String getAlertType();

	/** Removes an alert and all data associated with it. You should
	 * set all references to this Alert to null after deleting.
	 */	
	public void delete() {
		SQLiteDatabase db = getReadableDatabase();
		db.delete(getRuleTableName(), RuleEntry._ID + " = " + mRuleId, null);
		db.delete(getContactTableName(), RuleContactEntry.COLUMN_ALERT_RULE_ID + " = " + mRuleId, null);
		performRemainingDropCommands(db);
		db.close();
	}
	
	/** Gets the id of the alert
	 * @return id the alert id
	 */
	public long getId() {
		return mRuleId;
	}
	
	/** Gets the alert title
	 * @return the alert title
	 */
	public String getTitle() {
        Cursor ruleCursor = getRuleCursor();
		return ruleCursor.getString(ruleCursor.getColumnIndex(RuleEntry.COLUMN_TITLE));
	}
	
	/** Sets the alert title and saves it to the database
	 * @param title the alert title
	 */
	public void setTitle(String title) {
		ContentValues newValues = new ContentValues();
		newValues.put(RuleEntry.COLUMN_TITLE, title);
		updateRuleTable(newValues);
	}
	
	/** Gets the boolean state of whether the alert is on or off
	 * @return <code>true</code> if the alert is on; 
	 * <code>false</code> if it is off
	 */
	public boolean getOnState() {
        Cursor ruleCursor = getRuleCursor();
        return DbContract.intToBoolean(ruleCursor.getInt(ruleCursor.getColumnIndex(RuleEntry.COLUMN_ON_STATE)));
	}
	
	/** Sets the boolean state of whether the alert is on or off
	 * @param onState <code>true</code> if the alert is on
	 * <code> false if it is off
	 */
	public void setOnState(boolean onState) {
		ContentValues newValues = new ContentValues();
		newValues.put(RuleEntry.COLUMN_ON_STATE, onState);
		updateRuleTable(newValues);
	}	
	
	/** Gets the filter property of whether the alert is for everyone,
	 * allowed callers only, or all but blocked callers
	 * @return <code>DbContract.ENTRY_FILTER_BY_EVERYONE</code> if for everyone,
	 * <code>DbContract.ENTRY_FILTER_BY_ALLOWED_ONLY</code> if for allowed only,
	 * <code>DbContract.ENTRY_FILTER_BY_BLOCKED_IGNORED</code> if for all but blocked
	 */
	public int getFilterBy() {
        Cursor ruleCursor = getRuleCursor();
        return ruleCursor.getInt(ruleCursor.getColumnIndex(RuleEntry.COLUMN_FILTER_BY));
	}
	
	/** Sets the filter property of whether the alert is for everyone,
	 * allowed callers only, or all but blocked callers, and saves it to
	 * the database
	 * @param filterBy <code>DbContract.ENTRY_FILTER_BY_EVERYONE</code> if for everyone;
	 * <code>DbContract.ENTRY_FILTER_BY_ALLOWED_ONLY</code> if for allowed only;
	 * <code>DbContract.ENTRY_FILTER_BY_BLOCKED_IGNORED</code> if for all but blocked
	 */
	public void setFilterBy(int filterBy) {
		ContentValues newValues = new ContentValues();
		newValues.put(RuleEntry.COLUMN_FILTER_BY, filterBy);
		updateRuleTable(newValues);
	}		
	
	/** Gets the boolean flag for whether the alarm should cause the phone to ring,
	 * and saves it to the database.
	 * @return <code>true</code> if it should ring; <code>false</code> if not
	 */
	public boolean getRing() {
        Cursor ruleCursor = getRuleCursor();
        return DbContract.intToBoolean(ruleCursor.getInt(ruleCursor.getColumnIndex(RuleEntry.COLUMN_RING)));
	}

	/** Sets the boolean flag for whether the alarm should cause the phone to ring,
	 * and saves it to the database.
	 * @param ring <code>true</code> if it should ring; <code>false</code> if not
	 */	
	public void setRing(boolean ring) {
		ContentValues newValues = new ContentValues();
		newValues.put(RuleEntry.COLUMN_RING, ring);
		updateRuleTable(newValues);
	}	
	
	/** Gets the boolean flag for whether the alarm should cause the phone to vibrate
	 * @return <code>true</code> if it should vibrate; <code>false</code> if not
	 */	
	public boolean getVibrate() {
        Cursor ruleCursor = getRuleCursor();
        return  DbContract.intToBoolean(ruleCursor.getInt(ruleCursor.getColumnIndex(RuleEntry.COLUMN_VIBRATE)));
	}

	/** Sets the boolean flag for whether the alarm should cause the phone to vibrate,
	 * and saves it to the database.
	 * @param vibrate <code>true</code> if it should vibrate; <code>false</code> if not
	 */		
	public void setVibrate(boolean vibrate) {
		ContentValues newValues = new ContentValues();
		newValues.put(RuleEntry.COLUMN_VIBRATE, vibrate);
		updateRuleTable(newValues);
	}		

	/** Gets the volume as an integer from 0 through 1000. Note, this may be
	 * positive even if <code>getRing()</code> returns a <code>false</code>
	 * value, in which case it stores the most recent ring value.
	 * @return volume as an integer from 0 through 1000
	 */
	public int getVolume() {
        Cursor ruleCursor = getRuleCursor();
        return ruleCursor.getInt(ruleCursor.getColumnIndex(RuleEntry.COLUMN_VOLUME));
	}

	/** Sets the volume as an integer from 0 through 1000, and saves it to the
	 * database. Note, this does not automatically set <code>setRing(true)</code>.
	 * @param volume volume as an integer from 0 through 1000
	 */	
	public void setVolume(int volume) {
		if (volume < 100) {
			volume = 100;
		} else if (volume > 1000) {
			volume = 1000;
		}
		ContentValues newValues = new ContentValues();
		newValues.put(RuleEntry.COLUMN_VOLUME, volume);
		updateRuleTable(newValues);
	}	
	
	/** Gets the ringtone to play's uri
	 * @return the uri of the ringtone
	 */
	public Uri getTone() {
		return Uri.parse(getToneString());
	}

    /** Gets the ringtone to play's uri as a String
     * @return the uri of the ringtone as a String
     */
    private String getToneString() {
        Cursor ruleCursor = getRuleCursor();
        return ruleCursor.getString(ruleCursor.getColumnIndex(RuleEntry.COLUMN_TONE));
    }

	/** Gets the ringtone's name as a String
	 * @return the name of the ringtone as a String
	 */	
	public String getToneName(Context context) {
		if (getToneString().equals(Settings.System.DEFAULT_ALARM_ALERT_URI.toString())) {
			return "Default Alarm Sound";
		}
		return RingtoneManager.getRingtone(context, getTone()).getTitle(context);
	}

    /** Sets the ringtone to play's uri
     * @param tone the uri of the ringtone
     */
	public void setTone(Uri tone) {
		setTone(tone.toString());
	}
	
	/** Sets the ringtone to play's uri as a String
	 * @param tone the uri of the ringtone as a String
	 */
	private void setTone(String tone) {
		ContentValues newValues = new ContentValues();
		newValues.put(RuleEntry.COLUMN_TONE, tone);
		updateRuleTable(newValues);
	}
	
	/** Returns a list of the lookup values for all contacts on the "allow
	 * list." Note that having an "allow list" does not necessarily mean
	 * <code>getFilterBy()</code> returns a
	 * <code>DbContract.ENTRY_FILTER_BY_ALLOWED_ONLY</code> value.
	 * @return list of "allow list" contacts
	 */
	public List<String> getAllowedContacts() {
        return getContacts(DbContract.ENTRY_LIST_ALLOW_LIST);
	}

	/** Returns a list of the lookup values for all contacts on the "block
	 * list." Note that having an "block list" does not necessarily mean
	 * <code>getFilterBy()</code> returns a
	 * <code>DbContract.ENTRY_FILTER_BY_BLOCKED_IGNORED</code> value.
	 * @return list of "block list" contacts
	 */	
	public List<String> getBlockedContacts() {
		return getContacts(DbContract.ENTRY_LIST_BLOCK_LIST);
	}

    /** Returns a list of the lookup values for all contacts on the block
     * or allow list.
     * @param list the list type, either <code>DbContract.ENTRY_LIST_ALLOW_LIST</code>
     *             for allowed contacts, or <code>DbContract.ENTRY_LIST_BLOCK_LIST</code>
     *             for blocked contacts
     * @return list of contacts
     */
    private List<String> getContacts(int list) {
        SQLiteDatabase database = getReadableDatabase();
        List<String> contacts = new ArrayList<String>();
        List<String> lookupsToRemove = new ArrayList<String>();

        Cursor contactsCursor = database.query(getContactTableName(),
                new String[] {RuleContactEntry.COLUMN_LIST, RuleContactEntry.COLUMN_LOOKUP},
                RuleContactEntry.COLUMN_ALERT_RULE_ID + " = " + mRuleId,
                null, null, null, null);

        contactsCursor.moveToFirst();

        while (!contactsCursor.isAfterLast()) {
            String lookup = contactsCursor.getString(contactsCursor.getColumnIndex(RuleContactEntry.COLUMN_LOOKUP));
            if (contactsCursor.getInt(contactsCursor.getColumnIndex(RuleContactEntry.COLUMN_LIST)) == list) {
                Uri lookupUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookup);
                Uri res = ContactsContract.Contacts.lookupContact(mContext.getContentResolver(), lookupUri);
                if (res != null) {
                    contacts.add(lookup);
                } else {
                    lookupsToRemove.add(lookup);
                }

            }
            contactsCursor.moveToNext();
        }

        contactsCursor.close();

        for (String lookupToRemove : lookupsToRemove) {

            database.delete(getContactTableName(),
                    RuleContactEntry.COLUMN_ALERT_RULE_ID + " = ? AND " +
                            RuleContactEntry.COLUMN_LIST + " = ? AND " +
                            RuleContactEntry.COLUMN_LOOKUP + " = ?",
                    new String[]{"" + mRuleId, "" + list, lookupToRemove});

        }

        database.close();

        return contacts;
    }
	
	/** Returns a list of the names for all contacts on the "allow
	 * list." Note that having an "allow list" does not necessarily mean
	 * <code>getFilterBy()</code> returns a
	 * <code>DbContract.ENTRY_FILTER_BY_ALLOWED_ONLY</code> value.
	 * @return list of "allow list" contact names
	 */
	public List<String> getAllowedContactNames() {
		return getContactNames(getAllowedContacts());
	}

	/** Returns a list of the names for all contacts on the "block
	 * list." Note that having an "block list" does not necessarily mean
	 * <code>getFilterBy()</code> returns a
	 * <code>DbContract.ENTRY_FILTER_BY_BLOCKED_IGNORED</code> value.
	 * @return list of "block list" contact names
	 */	
	public List<String> getBlockedContactNames() {
		return getContactNames(getBlockedContacts());
	}	
	
	/** Returns a list of the names for a list of contact lookups.
	 * @return list of contact names
	 */		
	//TODO: Revise this so that it doesn't keep opening the database!
	// preferably, make getNameFromLookup use this function instead
	public List<String> getContactNames(List<String> lookups) {
		List<String> names = new ArrayList<>(lookups.size());
		for (Uri contactUri : getContactUris(lookups)) {

            Cursor cursor = mContext.getContentResolver().query(contactUri,
                    null, null, null, null);

            if (cursor.getCount() == 0) {
                throw new IllegalArgumentException("Couldn't find contact from uri");
            }

            cursor.moveToFirst();
            String name = cursor.getString(cursor.getColumnIndex(CONTACT_NAME));
            cursor.close();
            names.add(name);
        }
		return names;
	}

    private List<Uri> getContactUris(List<String> lookups) {
        List<Uri> uris = new ArrayList<>(lookups.size());
        for (String lookup : lookups) {
            uris.add(getContactUri(lookup));
        }
        return uris;
    }

    private Uri getContactUri(String lookup) {
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookup);
        return ContactsContract.Contacts.lookupContact(mContext.getContentResolver(), lookupUri);
    }
	
	/** Adds a contact to the "allow list" or "block list" and adds it to
	 * the alert rule contact database. Note, you may also want to set
	 * <code>setFilterBy(DbContract.ENTRY_FILTER_BY_ALLOWED_ONLY</code> to
	 * only allow "allow list" contacts to cause an alert, or
	 * <code>setFilterBy(DbContract.ENTRY_FILTER_BY_BLOCKED_IGNORED</code> to
	 * not allow "block list" contacts to cause an alert.
	 * @param lookup the lookup value of the contact in the phone's contact database
	 * @param list <code>DbContract.ENTRY_LIST_ALLOW_LIST</code> to add to the allow list;
	 * <code>DbContract.ENTRY_LIST_BLOCK_LIST</code> to add to the block list
	 * @return <code>true</code> if added; <code>false</code>
	 */
	public boolean addContact(String lookup, int list) {
		if (list == DbContract.ENTRY_LIST_ALLOW_LIST && !getAllowedContacts().contains(lookup) ||
				list == DbContract.ENTRY_LIST_BLOCK_LIST && !getBlockedContacts().contains(lookup)) {
			long success;
			
			SQLiteDatabase database = getWritableDatabase();
			ContentValues contact = new ContentValues();
			contact.put(RuleContactEntry.COLUMN_ALERT_RULE_ID, mRuleId);
			contact.put(RuleContactEntry.COLUMN_LOOKUP, lookup);
			contact.put(RuleContactEntry.COLUMN_LIST, list);		
			success = database.insert(getContactTableName(), null, contact);
			database.close();
			
			if (success == -1) {
				return false;
			}

			return true;
		}
		
		return false;
	}

	/** Removes a contact from the "allow list" or "block list" and removes it
	 * from the alert rule contact database. Note, you may also want to set
	 * <code>setFilterBy(DbContract.ENTRY_FILTER_BY_ALLOWED_ONLY</code> to
	 * only allow "allow list" contacts to cause an alert, or
	 * <code>setFilterBy(DbContract.ENTRY_FILTER_BY_BLOCKED_IGNORED</code> to
	 * not allow "block list" contacts to cause an alert.
	 * @param lookup the lookup value of the contact in the phone's contact database
	 * @param list <code>DbContract.ENTRY_LIST_ALLOW_LIST</code> to remove from
	 * the allow list; <code>DbContract.ENTRY_LIST_BLOCK_LIST</code> to remove
	 * from the block list
	 */	
	public void removeContact(String lookup, int list) {
		SQLiteDatabase database = getWritableDatabase();
		database.delete(getContactTableName(), 
				RuleContactEntry.COLUMN_ALERT_RULE_ID + " = ? AND " +
						RuleContactEntry.COLUMN_LIST + " = ? AND " +
						RuleContactEntry.COLUMN_LOOKUP + " = ?",
				new String[] {"" + mRuleId, "" + list, lookup});
		database.close();

	}
	
	/** Updates the database rule for the passed parameters
	 * @param newValues the key-value pairs for all parameters to update
	 */
	protected void updateRuleTable(ContentValues newValues) {
		SQLiteDatabase database = getWritableDatabase();
		database.update(getRuleTableName(),
				newValues,
				RuleEntry._ID + " =  " + mRuleId, null);
		database.close();
	}
	
	/** Gets the name of a new alert
	 * @return the alert name
	 */
	private String getNewAlertName() {
		String base = "New " + getAlertTypeName();
		
		ArrayList<String> existingNames = new ArrayList<String>();
		
		SQLiteDatabase database = getReadableDatabase();
		Cursor ruleCursor = database.query(getRuleTableName(),
				new String[] {RuleEntry.COLUMN_TITLE},
				null, null, null, null, null);
		ruleCursor.moveToFirst();
		
		while (!ruleCursor.isAfterLast()) {
			existingNames.add(ruleCursor.getString(ruleCursor.getColumnIndex(RuleEntry.COLUMN_TITLE)));
			ruleCursor.moveToNext();
		}
		
		ruleCursor.close();
		
		if (!existingNames.contains(base) && !existingNames.contains(base + " #1")) {
			return base;
		}
		
		int count = 2;
		
		String newName;
		
		do {
			newName = base + " #" + count;
			count++;
		} while (existingNames.contains(newName));
		
		return newName;
	}

	/** Checks to see if a contact fulfills the contact criteria of the alert.
	 * If the filter is set to everyone, then any passed value will pass. If
	 * the filter is set to allow list only, then only phone numbers on the list
	 * will pass. If the filter is set to block only block list contacts, then
	 * any number not on the list will pass.
	 * @param phoneNumber the phone number of the contact
	 * @return <code>true<code> if the contact criteria is met;
	 * <code>false</code> if not
	 */
	protected boolean meetsContactCriteria(String phoneNumber) {
		if (getFilterBy() == DbContract.ENTRY_FILTER_BY_EVERYONE) {
			return true;
		}
		
		String lookup = getLookupFromPhoneNumber(phoneNumber);
		
		if (getFilterBy() == DbContract.ENTRY_FILTER_BY_ALLOWED_ONLY) {
			if (lookup != null && isContactInDatabase(phoneNumber, getAllowedContacts())) {
				return true;
			} else {
				return false;
			}
		}
		
		else {
			if (lookup == null) {
				return true;
			} else if (isContactInDatabase(phoneNumber, getBlockedContacts())) {
				return false;
			} else {
				return true;
			}
		}
	}

    protected boolean isContactInDatabase(String phoneNumber, List<String> lookups) {
        String lookupToMatch = getLookupFromPhoneNumber(phoneNumber);
        List<Uri> contactUris = getContactUris(lookups);
        for (Uri contactUri : contactUris) {
            Cursor cursor = mContext.getContentResolver().query(contactUri,
                    null, null, null, null);

            if (cursor.getCount() == 0) {
                throw new IllegalArgumentException("Couldn't find contact from uri");
            }

            cursor.moveToFirst();
            String contactLookup = cursor.getString(cursor.getColumnIndex(CONTACT_LOOKUP));
            cursor.close();

            Log.e("MATCH", "contactLookup: " + contactLookup);
            Log.e("MATCH", "lookupToMatch: " + lookupToMatch);

            if (contactLookup.equals(lookupToMatch)) {
                return true;
            }
        }

        return false;
    }

	/** Gets the lookup in the phone's contact list from a phone number
	 * @param phoneNumber the phone number
	 * @return the lookup key
	 */
	private String getLookupFromPhoneNumber(String phoneNumber) {
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		Cursor cursor = mContext.getContentResolver().query(uri,
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

    public String getNameFromLookup(String lookup) {
        List<String> contacts = new ArrayList<String>();
        contacts.add(lookup);

        List<String> names = getContactNames(contacts);
        return names.get(0);
    }

}
