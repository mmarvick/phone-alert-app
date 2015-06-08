package com.mmarvick.urgentcall.data.call;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds all call alerts in a singleton store.
 */
public class CallAlertStore {
    private static CallAlertStore sAlertStore;

    private static List<CallAlert> mAlerts;

    public synchronized static CallAlertStore getInstance(Context context) {
        if (sAlertStore == null) {
            sAlertStore = new CallAlertStore(context);
        }

        return sAlertStore;
    }

    private CallAlertStore(Context context) {
        readAlertsFromDb(context);
    }

    /**
     * Returns a list of all call alerts in the database
     *
     * @return a list of AlertCall objects representing each currently stored
     * call alert
     */
    public List<CallAlert> getAlerts() {
        return mAlerts;
    }

    private void readAlertsFromDb(Context context) {
        DbOpenHelperCall dbOpenHelper = new DbOpenHelperCall(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();
        mAlerts = new ArrayList<>();

        Cursor ruleCursor = database.query(DbContractCallRule.CallRuleEntry.TABLE_NAME,
                new String[] {DbContractCallRule.CallRuleEntry._ID},
                null, null, null, null, null);

        ruleCursor.moveToFirst();
        while (!ruleCursor.isAfterLast()) {
            long alertId = ruleCursor.getInt(ruleCursor.getColumnIndex(DbContractCallRule.CallRuleEntry._ID));
            mAlerts.add(new CallAlert(context, alertId));
            ruleCursor.moveToNext();
        }

        ruleCursor.close();
    }
}
