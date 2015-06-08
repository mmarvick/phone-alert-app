package com.mmarvick.urgentcall.data.text;

import java.util.ArrayList;
import java.util.List;

import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.base.Alert;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AlertText extends Alert {
	/** A string identifying the alert type of a text alert */
	public static final String ALERT_TYPE = "TEXT_ALERT";
	
	/** The default name of a text alert */
	public static final String ALERT_TEXT_TYPE_NAME = "Text Alert";
	
	/** Returns a list of all text alerts in the database
	 * @param context the context
	 * @return a list of AlertText objects representing each currently stored
	 * text alert
	 */
	public static List<AlertText> getAlerts(Context context) {
		DbOpenHelperText dbOpenHelper = new DbOpenHelperText(context);
		SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
		List<AlertText> textAlerts = new ArrayList<AlertText>();
		
		Cursor ruleCursor = database.query(DbContractTextRule.TextRuleEntry.TABLE_NAME,
				new String[] {DbContractTextRule.TextRuleEntry._ID},
				null, null, null, null, null);
		
		ruleCursor.moveToFirst();
		while (!ruleCursor.isAfterLast()) {
			long alertId = ruleCursor.getInt(ruleCursor.getColumnIndex(DbContractTextRule.TextRuleEntry._ID));
			textAlerts.add(new AlertText(context, alertId));
			ruleCursor.moveToNext();
		}
		
		ruleCursor.close();
		
		return textAlerts;
	}		

	/** Constructor for an AlertText not currently in the database, with all
	 * initial values generated as defaults. Also adds the Alert to the
	 * database.
	 * 
	 * @param context the current context
	 */
	public AlertText(Context context) {
		this(context, null, false);
	}

	/** Constructor for an AlertText not currently in the database, with all
	 * initial values generated as defaults. Also adds the Alert to the
	 * database.
	 * 
	 * @param context the current context
	 * @param db a writable database for call rules
	 * @param isInitial <code>true</code> if is the initial rule created when
	 * the application runs for the first time; <code>false</code> if not 
	 */	
	public AlertText(Context context, SQLiteDatabase db, boolean isInitial) {
		super(context, db, isInitial);
		addPhrase("Urgent!", db);
	}	
	
	/** Constructor for an AlertText already in the database.
	 * 
	 * @param context (required) context of the call alert
	 * @param id (required) row number of the call alert
	 * @throws IndexOutOfBoundsException no row exists in the call alert table
	 * with that id
	 */
	public AlertText(Context context, long id) {
		super(context, id);
	}
	
	/** Gets the alert duration in seconds for the alert
	 * @return the duration in seconds that an alert lasts
	 */
	public int getAlertDuration() {
        Cursor ruleCursor = getRuleCursor();
		return ruleCursor.getInt(ruleCursor.getColumnIndex(DbContractTextRule.TextRuleEntry.COLUMN_ALERT_DURATION));
	}
	
	/** Sets the alert duration for the alert in seconds, and saves it to the
	 * database
	 * @param alertDuration the duration in seconds that an alert lasts
	 */
	public void setAlertDuration(int alertDuration) {
		ContentValues newValues = new ContentValues();
		newValues.put(DbContractTextRule.TextRuleEntry.COLUMN_ALERT_DURATION, alertDuration);
		updateRuleTable(newValues);
	}
	
	/** Returns the first phrase that will trigger a text alert. Not for
	 * long-term use.
	 * @return String phrase
	 */
	public String getSinglePhrase() {
        List<String> phrases = getPhrases(null);

		if (phrases.size() > 0) {
			return phrases.get(0);
		} else {
			return null;
		}
	}

    /** Returns the single alert phrase with quotes around it **/
    public String getQuotedSinglePhrase() {
        return "\u201C" + getSinglePhrase() + "\u201D";
    }

    /** Returns true if should trigger on any message() */
    public boolean isPhraseEmpty() {
        return getSinglePhrase().equals("");
    }
	
	/** Removes all phrases from the list of acceptable phrases for a text
	 * alert, and replaces it with the passed phrase. Not for long-term use.
	 * @param s the phrase to allow text alerts with
	 */
	public void setSinglePhrase(String s) {
		ArrayList<String> oldPhrases = new ArrayList<String>(getPhrases(null));
		for (String phrase : oldPhrases) {
			removePhrase(phrase);
		}
		addPhrase(s);
	}

    /** Returns a list of the lookup values for all phrases that will trigger
	 * a text alert.
     * @param db - a database to use, can be null
	 * @return list of phrases
	 */	
	public List<String> getPhrases(SQLiteDatabase db) {
        if (db == null) {
            db = getReadableDatabase();
        }
        List<String> phrases = new ArrayList<String>();
        Cursor phrasesCursor = db.query(getPhraseTableName(),
                new String[]{DbContractTextRule.TextRulePhraseEntry.COLUMN_TEXT_PHRASE},
                DbContractTextRule.TextRulePhraseEntry.COLUMN_ALERT_RULE_ID + " = " + mRuleId,
                null, null, null, null);

        phrasesCursor.moveToFirst();

        while (!phrasesCursor.isAfterLast()) {
            String phrase = phrasesCursor.getString(phrasesCursor.getColumnIndex(DbContractTextRule.TextRulePhraseEntry.COLUMN_TEXT_PHRASE));
            phrases.add(phrase);
            phrasesCursor.moveToNext();
        }

        phrasesCursor.close();

		return phrases;
	}
	
	/** Adds a phrase to the list of phrases that will trigger a text alert and
	 * saves it in the database. It will automatically trim whitespace from the
	 * front and back of the phrase, and will not add the phrase if it already
	 * exists.
	 * @param phrase the phrase to add
	 */
	public void addPhrase(String phrase) {	
		addPhrase(phrase, null);
	}
	
	/** Adds a phrase to the list of phrases that will trigger a text alert and
	 * saves it in the database. It will automatically trim whitespace from the
	 * front and back of the phrase, and will not add the phrase if it already
	 * exists.
	 * @param phrase the phrase to add
	 * @param db (optional) a writable database for the alert rules
	 */
	public void addPhrase(String phrase, SQLiteDatabase db) {
		String trimmedPhrase = phrase.trim();
		
		boolean alreadyHas = false;
		for (String existingPhrase : getPhrases(db)) {
			if (existingPhrase.toLowerCase().trim().equals(trimmedPhrase.toLowerCase())) {
				alreadyHas = true;
			}
		}
		
		if (!alreadyHas) {
			boolean needToClose = false;
			
			if (db == null) {
				db = getWritableDatabase();
				needToClose = true;
			}
			ContentValues phraseValues = new ContentValues();
			phraseValues.put(DbContractTextRule.TextRulePhraseEntry.COLUMN_ALERT_RULE_ID, mRuleId);
			phraseValues.put(DbContractTextRule.TextRulePhraseEntry.COLUMN_TEXT_PHRASE, trimmedPhrase);
			db.insert(getPhraseTableName(), null, phraseValues);
			
			if (needToClose) {
				db.close();
			}
		}
	}
	
	/** Removes a phrase from the list of phrases that will trigger a text alert
	 * as well as the database. It is case-sensitive and does not trim white space.
	 * @param phrase phrase to remove
	 */
	public void removePhrase(String phrase) {
		SQLiteDatabase database = getWritableDatabase();
		database.delete(getPhraseTableName(), 
				DbContractTextRule.TextRulePhraseEntry.COLUMN_ALERT_RULE_ID + " = ? AND " +
						DbContractTextRule.TextRulePhraseEntry.COLUMN_TEXT_PHRASE + " = ?",
				new String[] {"" + mRuleId, phrase});
		database.close();
	}

	/** Checks to see if all the criteria of this alert have been met by the
	 * contact that's texting.
	 * @param phoneNumber the phone number that is calling
	 * @param message the text of the incoming SMS
	 * @return <code>true</code> if all criteria are met;
	 * <code>false</code> otherwise
	 */
	public boolean shouldAlert(String phoneNumber, String message) {
		return (getOnState() && meetsPhraseCriteria(message) && meetsContactCriteria(phoneNumber));
	}	
	
	/** Checks to see if a contact fulfills the phrase criteria of the
	 * alert. If the message includes one of the phrases, the criteria is met.
	 * @param message the text of the incoming SMS
	 * @return <code>true<code> if the phrase criteria is met;
	 * <code>false</code> if not
	 */	
	private boolean meetsPhraseCriteria(String message) {
		for (String phrase : getPhrases(null)) {
			if (message.toLowerCase().contains(phrase.toLowerCase().trim())) {
				return true;
			}
		}
		return false;
	}

	/** Initializes the remaining information that is specific to text alerts,
	 * and stores into the ContentValues as key-value pairs to be saved
	 * to the database.
	 * @param ruleValues the repository of key-value pairs to save in the database
	 */
	protected void initializeAndStoreRemainingRuleData(ContentValues ruleValues) {
		int alertDuration = 10;
		ruleValues.put(DbContractTextRule.TextRuleEntry.COLUMN_ALERT_DURATION, alertDuration);
	}

	/** {@inheritDoc} */
	protected void performRemainingDropCommands(SQLiteDatabase db) {
		db.delete(getPhraseTableName(), DbContractTextRule.TextRulePhraseEntry.COLUMN_ALERT_RULE_ID + " = " + mRuleId, null);

	}

	/** {@inheritDoc} */
	protected String getAlertTypeName() {
		return ALERT_TEXT_TYPE_NAME;
	}	

	/** {@inheritDoc} */
	protected String getRuleTableName() {
		return DbContractTextRule.TextRuleEntry.TABLE_NAME;
	}
	
	/** {@inheritDoc} */
	protected String getContactTableName() {
		return DbContractTextRule.TextRuleContactEntry.TABLE_NAME;
	}
	
	protected String getPhraseTableName() {
		return DbContractTextRule.TextRulePhraseEntry.TABLE_NAME;
	}
	
	/** {@inheritDoc} */
	protected SQLiteDatabase getReadableDatabase() {
		DbOpenHelperText dbOpenHelper = new DbOpenHelperText(mContext);
		return dbOpenHelper.getReadableDatabase();
	}
	
	/** {@inheritDoc} */
	protected SQLiteDatabase getWritableDatabase() {
		DbOpenHelperText dbOpenHelper = new DbOpenHelperText(mContext);
		return dbOpenHelper.getWritableDatabase();		
	}
	
	/** {@inheritDoc} */
	public String getAlertType() {
		return ALERT_TYPE;
	}


    /** {@inheritDoc} */
    public String getShareText() {
        String shareText = "";
        if (isPhraseEmpty()) {
            shareText += mContext.getString(R.string.share_msg_once_1);
        } else {
            shareText += mContext.getString(R.string.share_msg_1) + getQuotedSinglePhrase();
            shareText += mContext.getString(R.string.share_msg_2);
        }
        shareText += mContext.getString(R.string.share_app_alert_url);
        return shareText;
    }

    /** {@inheritDoc} */
    public String getShareSubject() {
        if (isPhraseEmpty()) {
            return mContext.getString(R.string.share_msg_once_subject);
        } else {
            return mContext.getString(R.string.share_msg_subject);
        }
    }
}
