package com.mmarvick.urgentcall.data.base;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Settings;
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
	
	/** The id corresponding to the row of the alert */
	protected long mRuleId;

    protected String mTitle;
    protected boolean mOnState;
    protected int mFilterBy;
    protected boolean mRing;
    protected boolean mVibrate;
    protected int mVolume;
    protected String mToneString;
    protected List<String> mAllowedContacts;
    protected List<String> mBlockedContacts;

    public Alert() {
        mTitle = getAlertTypeName();
        mOnState = true;
        mFilterBy = DbContract.ENTRY_FILTER_BY_EVERYONE;
        mRing = true;
        mVibrate = false;
        mVolume = 1000;
        mToneString = Settings.System.DEFAULT_ALARM_ALERT_URI.toString();
        mAllowedContacts = new ArrayList<>();
        mBlockedContacts = new ArrayList<>();
    }


	
	/** Get the name of the alert for a specific alert type
	 * @return the name of the type of alert
	 */
	protected abstract String getAlertTypeName();
	
	/** Get the identifier of the alert for a specific alert type
	 * @return the name of the type of alert
	 */
	public abstract String getAlertType();

    /**
     * Get the share text for an alert
     * @return the text for sharing
     */
    public abstract String getShareText(Context context);

    /**
     * Get the sharing subject for an alert
     * @return the subject for sharing
     */
    public abstract String getShareSubject(Context context);

	/** Gets the id of the alert
	 * @return id the alert id
	 */
	public long getId() {
		return mRuleId;
	}

    public void setId(long id) {
        mRuleId = id;
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
		if (volume < 100) {
			volume = 100;
		} else if (volume > 1000) {
			volume = 1000;
		}
		mVolume = volume;
	}	
	
	/** Gets the ringtone to play's uri
	 * @return the uri of the ringtone
	 */
	public Uri getToneUri() {
		return Uri.parse(getToneString());
	}

    /** Gets the ringtone to play's uri as a String
     * @return the uri of the ringtone as a String
     */
    public String getToneString() {
        return mToneString;
    }

	/** Gets the ringtone's name as a String
	 * @return the name of the ringtone as a String
	 */	
	public String getToneName(Context context) {
		if (getToneString().equals(Settings.System.DEFAULT_ALARM_ALERT_URI.toString())) {
			return "Default Alarm Sound";
		}

		return RingtoneManager.getRingtone(context, getToneUri()).getTitle(context);
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
	public void setTone(String tone) {
		mToneString = tone;
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

    public void setAllowedContacts(List<String> contacts) {
        mAllowedContacts = contacts;
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

    public void setBlockedContacts(List<String> contacts) {
        mBlockedContacts = contacts;
    }
	
	/** Returns a list of the names for all contacts on the "allow
	 * list." Note that having an "allow list" does not necessarily mean
	 * <code>getFilterBy()</code> returns a
	 * <code>DbContract.ENTRY_FILTER_BY_ALLOWED_ONLY</code> value.
	 * @return list of "allow list" contact names
	 */
	public List<String> getAllowedContactNames(Context context) {
		return getContactNames(context, getAllowedContacts());
	}

	/** Returns a list of the names for all contacts on the "block
	 * list." Note that having an "block list" does not necessarily mean
	 * <code>getFilterBy()</code> returns a
	 * <code>DbContract.ENTRY_FILTER_BY_BLOCKED_IGNORED</code> value.
	 * @return list of "block list" contact names
	 */	
	public List<String> getBlockedContactNames(Context context) {
		return getContactNames(context, getBlockedContacts());
	}	
	
	/** Returns a list of the names for a list of contact lookups.
	 * @return list of contact names
	 */		
	//TODO: Revise this so that it doesn't keep opening the database!
	// preferably, make getNameFromLookup use this function instead
	public List<String> getContactNames(Context context, List<String> lookups) {
		List<String> names = new ArrayList<>(lookups.size());
		for (Uri contactUri : getContactUris(context, lookups)) {

            Cursor cursor = context.getContentResolver().query(contactUri,
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

    private List<Uri> getContactUris(Context context, List<String> lookups) {
        List<Uri> uris = new ArrayList<>(lookups.size());
        for (String lookup : lookups) {
            uris.add(getContactUri(context, lookup));
        }
        return uris;
    }

    private Uri getContactUri(Context context, String lookup) {
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookup);
        return ContactsContract.Contacts.lookupContact(context.getContentResolver(), lookupUri);
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
	public void addContact(String lookup, int list) {
        if (list == DbContract.ENTRY_LIST_ALLOW_LIST) {
            mAllowedContacts.add(lookup);
        } else if (list == DbContract.ENTRY_LIST_BLOCK_LIST) {
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
        if (list == DbContract.ENTRY_LIST_ALLOW_LIST) {
            mAllowedContacts.remove(lookup);
        } else if (list == DbContract.ENTRY_LIST_BLOCK_LIST) {
            mBlockedContacts.remove(lookup);
        }
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
	protected boolean meetsContactCriteria(Context context, String phoneNumber) {
		if (getFilterBy() == DbContract.ENTRY_FILTER_BY_EVERYONE) {
			return true;
		}
		
		String lookup = getLookupFromPhoneNumber(context, phoneNumber);
		
		if (getFilterBy() == DbContract.ENTRY_FILTER_BY_ALLOWED_ONLY) {
			if (lookup != null && isContactInDatabase(context, phoneNumber, getAllowedContacts())) {
				return true;
			} else {
				return false;
			}
		}
		
		else {
			if (lookup == null) {
				return true;
			} else if (isContactInDatabase(context, phoneNumber, getBlockedContacts())) {
				return false;
			} else {
				return true;
			}
		}
	}

    protected boolean isContactInDatabase(Context context, String phoneNumber, List<String> lookups) {
        String lookupToMatch = getLookupFromPhoneNumber(context, phoneNumber);
        List<Uri> contactUris = getContactUris(context, lookups);
        for (Uri contactUri : contactUris) {
            Cursor cursor = context.getContentResolver().query(contactUri,
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
	private String getLookupFromPhoneNumber(Context context, String phoneNumber) {
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		Cursor cursor = context.getContentResolver().query(uri,
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

    public String getNameFromLookup(Context context, String lookup) {
        List<String> contacts = new ArrayList<String>();
        contacts.add(lookup);

        List<String> names = getContactNames(context, contacts);
        return names.get(0);
    }
}
