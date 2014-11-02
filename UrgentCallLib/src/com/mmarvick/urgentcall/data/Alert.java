package com.mmarvick.urgentcall.data;

import java.util.ArrayList;
import java.util.List;

import com.mmarvick.urgentcall.data.DbContract.RuleContactEntry;
import com.mmarvick.urgentcall.data.DbContract.RuleEntry;
import com.mmarvick.urgentcall.data.DbContractCallRule.CallRuleEntry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
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
	
	/** The title of the alert */
	private String mTitle;
	
	/** The flag of whether the alert is on or off**/
	private boolean mOnState;
	
	/** The value that specifies if the alert is for everyone, only allowed
	 * users, or all but blocked users
	 */
	private int mFilterBy;
	
	/** The flat of whether to ring the phone or not */
	private boolean mRing;
	
	/** The flag of whether to vibrate the phone or not */
	private boolean mVibrate;
	
	/** The value of the volume of the ring, out of 1000 */
	private int mVolume;
	
	/** The uri of the ringtone to play stored as a String */
	private String mTone;
	
	/** A list of lookups for allowed contacts */
	private List<String> mAllowedContacts;
	
	/** A list of lookups for blocked contacts */
	private List<String> mBlockedContacts;
	
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
		
		if (!isInitial) {
			mTitle = getNewAlertName();
		} else {
			mTitle = getRuleInitialName();
		}
		
		mOnState = true;
		mFilterBy = DbContract.ENTRY_FILTER_BY_EVERYONE;
		mRing = true;
		mVibrate = false;
		mVolume = 1000;
		mAllowedContacts = new ArrayList<String>();
		mBlockedContacts = new ArrayList<String>();
		
		ContentValues ruleValues = new ContentValues();
		ruleValues.put(RuleEntry.COLUMN_TITLE, mTitle);
		ruleValues.put(RuleEntry.COLUMN_ON_STATE, mOnState);
		ruleValues.put(RuleEntry.COLUMN_FILTER_BY, mFilterBy);
		ruleValues.put(RuleEntry.COLUMN_RING, mRing);
		ruleValues.put(RuleEntry.COLUMN_VIBRATE, mVibrate);
		ruleValues.put(RuleEntry.COLUMN_VOLUME, mVolume);
		
		initializeAndStoreRemainingRuleData(ruleValues);
		
		boolean closeWhenDone = false;
		
		if (db == null) {
			db = getWritableDatabase();
			closeWhenDone = true;
		}
		mRuleId = db.insert(getTableName(), null, ruleValues);
		
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
		SQLiteDatabase database = getReadableDatabase();
		Cursor ruleCursor = database.query(getTableName(),
				null,
				RuleEntry._ID + " =  " + id,
				null, null, null, null);
		
		if (!ruleCursor.moveToFirst()) {
			throw new IndexOutOfBoundsException();
		}
		
		mRuleId = id;
		mTitle = ruleCursor.getString(ruleCursor.getColumnIndex(RuleEntry.COLUMN_TITLE));
		mOnState = DbContract.intToBoolean(ruleCursor.getInt(ruleCursor.getColumnIndex(RuleEntry.COLUMN_ON_STATE)));
		mFilterBy = ruleCursor.getInt(ruleCursor.getColumnIndex(RuleEntry.COLUMN_FILTER_BY));
		mRing = DbContract.intToBoolean(ruleCursor.getInt(ruleCursor.getColumnIndex(RuleEntry.COLUMN_RING)));
		mVibrate = DbContract.intToBoolean(ruleCursor.getInt(ruleCursor.getColumnIndex(RuleEntry.COLUMN_VIBRATE)));
		mVolume = ruleCursor.getInt(ruleCursor.getColumnIndex(RuleEntry.COLUMN_VOLUME));
		
		loadRemainingRuleData(database, ruleCursor);
		
		ruleCursor.close();
		
		Cursor contactsCursor = database.query(getContactTableName(), 
				new String[] {RuleContactEntry.COLUMN_LIST, RuleContactEntry.COLUMN_LOOKUP}, 
				RuleContactEntry.COLUMN_ALERT_RULE_ID + " = " + id, 
				null, null, null, null);
		
		mAllowedContacts = new ArrayList<String>();
		mBlockedContacts = new ArrayList<String>();
		
		contactsCursor.moveToFirst();
		
		while (!contactsCursor.isAfterLast()) {
			String lookup = contactsCursor.getString(contactsCursor.getColumnIndex(RuleContactEntry.COLUMN_LOOKUP));
			if (contactsCursor.getInt(contactsCursor.getColumnIndex(RuleContactEntry.COLUMN_LIST)) == DbContract.ENTRY_LIST_ALLOW_LIST) {
				mAllowedContacts.add(lookup);
			} else {
				mBlockedContacts.add(lookup);
			}
			contactsCursor.moveToNext();
		}
		
		contactsCursor.close();
		
		database.close();
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
	protected abstract String getTableName();
	
	/** Gets the name of the table corresponding to the contacts that an alert
	 * applies to.
	 * @return the rule contact table name
	 */
	protected abstract String getContactTableName();
	
	/** Gets the remaining information that is alert-specific (not common to
	 * all types of alerts) from the database
	 * @param database the database that has tables for the alert type
	 * @param ruleCursor the Cursor that both contains and is currently
	 * positioned at the table row to read alert data from
	 */
	protected abstract void loadRemainingRuleData(SQLiteDatabase database, Cursor ruleCursor);

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
	
	/** Get the name of the initial alert
	 * @return the name of the type of alert
	 */
	protected abstract String getRuleInitialName();	

	/** Removes an alert and all data associated with it. You should
	 * set all references to this Alert to null after deleting.
	 */	
	public void delete() {
		SQLiteDatabase db = getReadableDatabase();
		db.delete(getTableName(), RuleEntry._ID + " = " + mRuleId, null);
		db.delete(getContactTableName(), RuleContactEntry.COLUMN_ALERT_RULE_ID + " = " + mRuleId, null);
		performRemainingDropCommands(db);
		db.close();
	}
	
	/** Gets the alert title
	 * @return the alert title
	 */
	public String getTitle() {
		return mTitle;
	}
	
	/** Sets the alert title and saves it to the database
	 * @param title the alert title
	 */
	public void setTitle(String title) {
		ContentValues newValues = new ContentValues();
		newValues.put(RuleEntry.COLUMN_TITLE, title);
		updateRuleTable(newValues);
		mTitle = title;
	}
	
	/** Gets the boolean state of whether the alert is on or off
	 * @return <code>true</code> if the alert is on; 
	 * <code>false</code> if it is off
	 */
	public boolean getOnState() {
		return mOnState;
	}
	
	/** Sets the boolean state of whether the alert is on or off
	 * @param onState <code>true</code> if the alert is on
	 * <code> false if it is off
	 */
	public void setOnState(boolean onState) {
		ContentValues newValues = new ContentValues();
		newValues.put(RuleEntry.COLUMN_ON_STATE, onState);
		updateRuleTable(newValues);
		mOnState = onState;
	}	
	
	/** Gets the filter property of whether the alert is for everyone,
	 * allowed callers only, or all but blocked callers
	 * @return <code>DbContract.ENTRY_FILTER_BY_EVERYONE</code> if for everyone,
	 * <code>DbContract.ENTRY_FILTER_BY_ALLOWED_ONLY</code> if for allowed only,
	 * <code>DbContract.ENTRY_FILTER_BY_BLOCKED_IGNORED</code> if for all but blocked
	 */
	public int getFilterBy() {
		return mFilterBy;
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
		mFilterBy = filterBy;
	}		
	
	/** Gets the boolean flag for whether the alarm should cause the phone to ring,
	 * and saves it to the database.
	 * @return <code>true</code> if it should ring; <code>false</code> if not
	 */
	public boolean getRing() {
		return mRing;
	}

	/** Sets the boolean flag for whether the alarm should cause the phone to ring,
	 * and saves it to the database.
	 * @param ring <code>true</code> if it should ring; <code>false</code> if not
	 */	
	public void setRing(boolean ring) {
		ContentValues newValues = new ContentValues();
		newValues.put(RuleEntry.COLUMN_RING, ring);
		updateRuleTable(newValues);
		mRing = ring;
	}	
	
	/** Gets the boolean flag for whether the alarm should cause the phone to vibrate
	 * @return <code>true</code> if it should vibrate; <code>false</code> if not
	 */	
	public boolean getVibrate() {
		return mVibrate;
	}

	/** Sets the boolean flag for whether the alarm should cause the phone to vibrate,
	 * and saves it to the database.
	 * @param vibrate <code>true</code> if it should vibrate; <code>false</code> if not
	 */		
	public void setVibrate(boolean vibrate) {
		ContentValues newValues = new ContentValues();
		newValues.put(RuleEntry.COLUMN_VIBRATE, vibrate);
		updateRuleTable(newValues);
		mVibrate = vibrate;
	}		

	/** Gets the volume as an integer from 0 through 1000. Note, this may be
	 * positive even if <code>getRing()</code> returns a <code>false</code>
	 * value, in which case it stores the most recent ring value.
	 * @return volume as an integer from 0 through 1000
	 */
	public int getVolume() {
		return mVolume;
	}

	/** Sets the volume as an integer from 0 through 1000, and saves it to the
	 * database. Note, this does not automatically set <code>setRing(true)</code>.
	 * @param volume volume as an integer from 0 through 1000
	 */	
	public void setVolume(int volume) {
		ContentValues newValues = new ContentValues();
		newValues.put(RuleEntry.COLUMN_VOLUME, volume);
		updateRuleTable(newValues);
		mVolume = volume;
	}	
	
	/** Gets the ringtone to play's uri as a String
	 * @return the uri of the ringtone as a String
	 */
	public String getTone() {
		return mTone;
	}

	/** Sets the ringtone to play's uri as a String
	 * @param tone the uri of the ringtone as a String
	 */
	public void setTone(String tone) {
		ContentValues newValues = new ContentValues();
		newValues.put(RuleEntry.COLUMN_TONE, tone);
		updateRuleTable(newValues);
		mTone = tone;
	}
	
	/** Returns a list of the lookup values for all contacts on the "allow
	 * list." Note that having an "allow list" does not necessarily mean
	 * <code>getFilterBy()</code> returns a
	 * <code>DbContract.ENTRY_FILTER_BY_ALLOWED_ONLY</code> value.
	 * @return list of "allow list" contacts
	 */
	public List<String> getAllowedContacts() {
		return mAllowedContacts;
	}

	/** Returns a list of the lookup values for all contacts on the "block
	 * list." Note that having an "block list" does not necessarily mean
	 * <code>getFilterBy()</code> returns a
	 * <code>DbContract.ENTRY_FILTER_BY_BLOCKED_IGNORED</code> value.
	 * @return list of "block list" contacts
	 */	
	public List<String> getBlockedContacts() {
		return mBlockedContacts;
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
	 */
	public void addContact(String lookup, int list) {
		SQLiteDatabase database = getWritableDatabase();
		ContentValues contact = new ContentValues();
		contact.put(RuleContactEntry.COLUMN_ALERT_RULE_ID, mRuleId);
		contact.put(RuleContactEntry.COLUMN_LOOKUP, lookup);
		contact.put(RuleContactEntry.COLUMN_LIST, list);		
		database.insert(getContactTableName(), null, contact);
		database.close();
		
		if (list == DbContract.ENTRY_LIST_ALLOW_LIST) {
			mAllowedContacts.add(lookup);
		} else {
			mBlockedContacts.add(lookup);
		}
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
				RuleContactEntry.COLUMN_ALERT_RULE_ID + " = " + mRuleId + " AND " +
						RuleContactEntry.COLUMN_LIST + " = " + list + " AND " +
						RuleContactEntry.COLUMN_LOOKUP + " = " + lookup,
				null);
		database.close();
		
		if (list == DbContract.ENTRY_LIST_ALLOW_LIST) {
			mAllowedContacts.remove(lookup);
		} else {
			mBlockedContacts.remove(lookup);
		}		
	}
	
	/** Updates the database rule for the passed parameters
	 * @param newValues the key-value pairs for all parameters to update
	 */
	protected void updateRuleTable(ContentValues newValues) {
		SQLiteDatabase database = getWritableDatabase();
		database.update(getTableName(),
				newValues,
				RuleEntry._ID + " =  " + mRuleId, null);
		database.close();
	}
	
	/** Gets the name of a new alert
	 * @return the alert name
	 */
	private String getNewAlertName() {
		return "New " + getAlertTypeName();
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
		if (mFilterBy == DbContract.ENTRY_FILTER_BY_EVERYONE) {
			return true;
		}
		
		String lookup = getLookupFromPhoneNumber(phoneNumber);
		
		if (mFilterBy == DbContract.ENTRY_FILTER_BY_ALLOWED_ONLY) {
			if (lookup != null && mAllowedContacts.contains(lookup)) {
				return true;
			} else {
				return false;
			}
		}
		
		else {
			if (lookup == null) {
				return true;
			} else if (mBlockedContacts.contains(lookup)) {
				return false;
			} else {
				return true;
			}
		}
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

}
