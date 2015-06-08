package com.mmarvick.urgentcall.data.base;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;

import com.mmarvick.urgentcall.data.call.DbOpenHelperCall;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael on 6/7/2015.
 */
public abstract class AlertStore<E extends Alert> {
    protected List<E> mAlerts;

    protected AlertStore(Context context) {
        readAlertsFromDb(context);
    }

    public List<E> getAlerts() {
        return mAlerts;
    };

    protected abstract E getNewAlert();

    public E getAlert(long id) {
        for (E alert : mAlerts) {
            if (alert.getId() == id) {
                return alert;
            }
        }

        return null;
    }

    public void addAlert(Context context, E alert) {
        SQLiteDatabase db = getWritableDatabase(context);
        long id = db.insert(getRuleTableName(), null, getRuleValues(alert));
        alert.setId(id);
        db.close();
        mAlerts.add(alert);
    }

    public void updateAlert(Context context, E alert) {
        SQLiteDatabase db = getWritableDatabase(context);
        db.update(getRuleTableName(),
                getRuleValues(alert),
                DbContract.RuleEntry._ID + " =  " + alert.getId(),
                null);

        db.delete(getContactTableName(),
                DbContract.RuleContactEntry.COLUMN_ALERT_RULE_ID + " = ?",
                new String[] {Long.toString(alert.getId())});

        for (String contact : alert.getAllowedContacts()) {
            ContentValues values = new ContentValues();
            values.put(DbContract.RuleContactEntry.COLUMN_ALERT_RULE_ID, alert.mRuleId);
            values.put(DbContract.RuleContactEntry.COLUMN_LOOKUP, contact);
            values.put(DbContract.RuleContactEntry.COLUMN_LIST, DbContract.ENTRY_LIST_ALLOW_LIST);
            db.insert(getContactTableName(), null, values);
        }

        for (String contact : alert.getBlockedContacts()) {
            ContentValues values = new ContentValues();
            values.put(DbContract.RuleContactEntry.COLUMN_ALERT_RULE_ID, alert.mRuleId);
            values.put(DbContract.RuleContactEntry.COLUMN_LOOKUP, contact);
            values.put(DbContract.RuleContactEntry.COLUMN_LIST, DbContract.ENTRY_LIST_BLOCK_LIST);
            db.insert(getContactTableName(), null, values);
        }

        db.close();
    }

    private ContentValues getRuleValues(E alert) {
        ContentValues ruleValues = new ContentValues();
        ruleValues.put(DbContract.RuleEntry.COLUMN_TITLE, alert.getTitle());
        ruleValues.put(DbContract.RuleEntry.COLUMN_ON_STATE, alert.getOnState());
        ruleValues.put(DbContract.RuleEntry.COLUMN_FILTER_BY, alert.getFilterBy());
        ruleValues.put(DbContract.RuleEntry.COLUMN_RING, alert.getRing());
        ruleValues.put(DbContract.RuleEntry.COLUMN_VIBRATE, alert.getVibrate());
        ruleValues.put(DbContract.RuleEntry.COLUMN_VOLUME, alert.getVolume());
        ruleValues.put(DbContract.RuleEntry.COLUMN_TONE, alert.getToneString());
        addRemainingParameters(ruleValues, alert);

        return ruleValues;
    }

    public void deleteAlert(Context context, E alert) {
        SQLiteDatabase db = getReadableDatabase(context);
        db.delete(getRuleTableName(), DbContract.RuleEntry._ID + " = " + alert.getId(), null);
        db.delete(getContactTableName(), DbContract.RuleContactEntry.COLUMN_ALERT_RULE_ID + " = " + alert.getId(), null);
        performRemainingDropCommands(db, alert);
        db.close();
        mAlerts.remove(alert);
    }

    protected E readAlertFromDb(Context context, long ruleId) {
        E alert = getNewAlert();
        alert.setId(ruleId);

        Cursor cursor = getRuleCursor(context, ruleId);
        alert.setTitle(cursor.getString(cursor.getColumnIndex(DbContract.RuleEntry.COLUMN_TITLE)));
        alert.setOnState(DbContract.intToBoolean(cursor.getInt(cursor.getColumnIndex(DbContract.RuleEntry.COLUMN_ON_STATE))));
        alert.setFilterBy(cursor.getInt(cursor.getColumnIndex(DbContract.RuleEntry.COLUMN_FILTER_BY)));
        alert.setRing(DbContract.intToBoolean(cursor.getInt(cursor.getColumnIndex(DbContract.RuleEntry.COLUMN_RING))));
        alert.setVibrate(DbContract.intToBoolean(cursor.getInt(cursor.getColumnIndex(DbContract.RuleEntry.COLUMN_VIBRATE))));
        alert.setVolume(cursor.getInt(cursor.getColumnIndex(DbContract.RuleEntry.COLUMN_VOLUME)));
        alert.setTone(cursor.getString(cursor.getColumnIndex(DbContract.RuleEntry.COLUMN_TONE)));
        cursor.close();

        alert.setAllowedContacts(getContacts(context, ruleId, DbContract.ENTRY_LIST_ALLOW_LIST));
        alert.setBlockedContacts(getContacts(context, ruleId, DbContract.ENTRY_LIST_BLOCK_LIST));

        return alert;
    }

    private Cursor getRuleCursor(Context context, long ruleId) {
        SQLiteDatabase database = getReadableDatabase(context);
        Cursor ruleCursor = database.query(getRuleTableName(),
                null,
                DbContract.RuleEntry._ID + " =  " + ruleId,
                null, null, null, null);

        if (!ruleCursor.moveToFirst()) {
            throw new IndexOutOfBoundsException();
        }

        return ruleCursor;
    }

    /** Drops any additional information from the database that is alert-specific
     * (not common to all types of alerts).
     */
    protected abstract void performRemainingDropCommands(SQLiteDatabase db, E alert);

    /** Gets the name of the table corresponding to each alert
     * @return the rule table name
     */
    protected abstract String getRuleTableName();

    /** Gets the name of the table corresponding to the contacts that an alert
     * applies to.
     * @return the rule contact table name
     */
    protected abstract String getContactTableName();

    /** Gets a readable database for tables corresponding to the alert
     * @return the readable database */
    protected abstract SQLiteDatabase getReadableDatabase(Context context);

    /** Gets a writable database for tables corresponding to the alert
     * @return the writable database */
    protected abstract SQLiteDatabase getWritableDatabase(Context context);

    protected abstract void addRemainingParameters(ContentValues ruleValues, Alert alert);

    private void readAlertsFromDb(Context context) {
        DbOpenHelperCall dbOpenHelper = new DbOpenHelperCall(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        mAlerts = new ArrayList<>();

        Cursor ruleCursor = database.query(getRuleTableName(),
                new String[] {DbContract.RuleEntry._ID},
                null, null, null, null, null);

        ruleCursor.moveToFirst();
        while (!ruleCursor.isAfterLast()) {
            long alertId = ruleCursor.getInt(ruleCursor.getColumnIndex(DbContract.RuleEntry._ID));
            mAlerts.add(readAlertFromDb(context, alertId));
            ruleCursor.moveToNext();
        }

        ruleCursor.close();
    }



    /** Returns a list of the lookup values for all contacts on the block
     * or allow list.
     * @param listType the list type, either <code>DbContract.ENTRY_LIST_ALLOW_LIST</code>
     *             for allowed contacts, or <code>DbContract.ENTRY_LIST_BLOCK_LIST</code>
     *             for blocked contacts
     * @return list of contacts
     */
    private List<String> getContacts(Context context, long ruleId, int listType) {
        SQLiteDatabase database = getReadableDatabase(context);
        List<String> contacts = new ArrayList<String>();
        List<String> lookupsToRemove = new ArrayList<String>();

        Cursor contactsCursor = database.query(getContactTableName(),
                new String[] {DbContract.RuleContactEntry.COLUMN_LIST, DbContract.RuleContactEntry.COLUMN_LOOKUP},
                DbContract.RuleContactEntry.COLUMN_ALERT_RULE_ID + " = " + ruleId,
                null, null, null, null);

        contactsCursor.moveToFirst();

        while (!contactsCursor.isAfterLast()) {
            String lookup = contactsCursor.getString(contactsCursor.getColumnIndex(DbContract.RuleContactEntry.COLUMN_LOOKUP));
            if (contactsCursor.getInt(contactsCursor.getColumnIndex(DbContract.RuleContactEntry.COLUMN_LIST)) == listType) {
                Uri lookupUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookup);
                Uri res = ContactsContract.Contacts.lookupContact(context.getContentResolver(), lookupUri);
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
                    DbContract.RuleContactEntry.COLUMN_ALERT_RULE_ID + " = ? AND " +
                            DbContract.RuleContactEntry.COLUMN_LIST + " = ? AND " +
                            DbContract.RuleContactEntry.COLUMN_LOOKUP + " = ?",
                    new String[]{"" + ruleId, "" + listType, lookupToRemove});

        }

        database.close();

        return contacts;
    }
}
