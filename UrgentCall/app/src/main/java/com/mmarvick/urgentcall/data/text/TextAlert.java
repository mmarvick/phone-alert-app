package com.mmarvick.urgentcall.data.text;

import java.util.ArrayList;
import java.util.List;

import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.base.Alert;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TextAlert extends Alert {
	/** A string identifying the alert type of a text alert */
	public static final String ALERT_TYPE = "TEXT_ALERT";
	
	/** The default name of a text alert */
	public static final String ALERT_TEXT_TYPE_NAME = "Text Alert";

    private int mAlertDuration;
    private List<String> mPhrases;

	public TextAlert() {
		super();
        mAlertDuration = 10;
        mPhrases = new ArrayList<>();
        mPhrases.add("Urgent!");
	}
	
	/** Gets the alert duration in seconds for the alert
	 * @return the duration in seconds that an alert lasts
	 */
	public int getAlertDuration() {
        return mAlertDuration;
	}
	
	/** Sets the alert duration for the alert in seconds, and saves it to the
	 * database
	 * @param alertDuration the duration in seconds that an alert lasts
	 */
	public void setAlertDuration(int alertDuration) {
		mAlertDuration = alertDuration;
	}
	
	/** Returns the first phrase that will trigger a text alert. Not for
	 * long-term use.
	 * @return String phrase
	 */
	public String getSinglePhrase() {
        List<String> phrases = getPhrases();

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
        mPhrases = new ArrayList<>();
        mPhrases.add("Urgent!");
	}

    /** Returns a list of the lookup values for all phrases that will trigger
	 * a text alert.
	 * @return list of phrases
	 */	
	public List<String> getPhrases() {
        return mPhrases;
	}

    public void setPhrases(List<String> phrases) {
        mPhrases = phrases;
    }
	
	/** Adds a phrase to the list of phrases that will trigger a text alert and
	 * saves it in the database. It will automatically trim whitespace from the
	 * front and back of the phrase, and will not add the phrase if it already
	 * exists.
	 * @param phrase the phrase to add
	 */
	public void addPhrase(String phrase) {
        phrase = phrase.trim();
        if (!mPhrases.contains(phrase)) {
            mPhrases.add(phrase.trim());
        }
	}
	
	/** Removes a phrase from the list of phrases that will trigger a text alert
	 * as well as the database. It is case-sensitive and does not trim white space.
	 * @param phrase phrase to remove
	 */
	public void removePhrase(String phrase) {
        mPhrases.remove(phrase);
	}

	/** Checks to see if all the criteria of this alert have been met by the
	 * contact that's texting.
	 * @param phoneNumber the phone number that is calling
	 * @param message the text of the incoming SMS
	 * @return <code>true</code> if all criteria are met;
	 * <code>false</code> otherwise
	 */
	public boolean shouldAlert(Context context, String phoneNumber, String message) {
		return (getOnState() && meetsPhraseCriteria(message) && meetsContactCriteria(context, phoneNumber));
	}	
	
	/** Checks to see if a contact fulfills the phrase criteria of the
	 * alert. If the message includes one of the phrases, the criteria is met.
	 * @param message the text of the incoming SMS
	 * @return <code>true<code> if the phrase criteria is met;
	 * <code>false</code> if not
	 */	
	private boolean meetsPhraseCriteria(String message) {
		for (String phrase : getPhrases()) {
			if (message.toLowerCase().contains(phrase.toLowerCase().trim())) {
				return true;
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	protected String getAlertTypeName() {
		return ALERT_TEXT_TYPE_NAME;
	}
	

    @Override
	public String getAlertType() {
		return ALERT_TYPE;
	}


    @Override
    public String getShareText(Context context) {
        String shareText = "";
        if (isPhraseEmpty()) {
            shareText += context.getString(R.string.share_msg_once_1);
        } else {
            shareText += context.getString(R.string.share_msg_1) + getQuotedSinglePhrase();
            shareText += context.getString(R.string.share_msg_2);
        }
        shareText += context.getString(R.string.share_app_alert_url);
        return shareText;
    }

    @Override
    public String getShareSubject(Context context) {
        if (isPhraseEmpty()) {
            return context.getString(R.string.share_msg_once_subject);
        } else {
            return context.getString(R.string.share_msg_subject);
        }
    }
}
