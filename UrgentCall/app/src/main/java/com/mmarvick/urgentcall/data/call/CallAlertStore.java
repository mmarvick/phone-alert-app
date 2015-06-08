package com.mmarvick.urgentcall.data.call;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.mmarvick.urgentcall.data.base.Alert;
import com.mmarvick.urgentcall.data.base.AlertStore;

import java.util.List;

/**
 * Holds all call alerts in a singleton store.
 */
public class CallAlertStore extends AlertStore<CallAlert> {
    private static CallAlertStore sAlertStore;

    public synchronized static CallAlertStore getInstance(Context context) {
        if (sAlertStore == null) {
            sAlertStore = new CallAlertStore(context);
        }

        return sAlertStore;
    }

    protected CallAlertStore(Context context) {
        super(context);
    }

    @Override
    protected CallAlert getNewAlert() {
        return new CallAlert();
    }

    @Override
    protected void performRemainingDropCommands(SQLiteDatabase db, CallAlert alert) {
        // Pass
    }

    /** {@inheritDoc} */
    protected String getRuleTableName() {
        return DbContractCallRule.CallRuleEntry.TABLE_NAME;
    }

    @Override
    protected String getContactTableName() {
        return DbContractCallRule.CallRuleContactEntry.TABLE_NAME;
    }

    @Override
    protected SQLiteDatabase getReadableDatabase(Context context) {
        DbOpenHelperCall dbOpenHelper = new DbOpenHelperCall(context);
        return dbOpenHelper.getReadableDatabase();
    }

    @Override
    protected SQLiteDatabase getWritableDatabase(Context context) {
        DbOpenHelperCall dbOpenHelper = new DbOpenHelperCall(context);
        return dbOpenHelper.getWritableDatabase();
    }

    @Override
    protected void addRemainingParameters(ContentValues ruleValues, Alert alert) {
        CallAlert callAlert = (CallAlert) alert;
        ruleValues.put(DbContractCallRule.CallRuleEntry.COLUMN_CALL_QTY, callAlert.getCallQty());
        ruleValues.put(DbContractCallRule.CallRuleEntry.COLUMN_CALL_TIME, callAlert.getCallTime());
    }
}
