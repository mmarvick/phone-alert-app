package com.mmarvick.urgentcall.data.text;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mmarvick.urgentcall.data.base.Alert;
import com.mmarvick.urgentcall.data.base.AlertStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael on 6/7/2015.
 */
public class TextAlertStore extends AlertStore<TextAlert> {
    private static TextAlertStore sAlertStore;

    public synchronized static TextAlertStore getInstance(Context context) {
        if (sAlertStore == null) {
            sAlertStore = new TextAlertStore(context);
        }

        return sAlertStore;
    }

    protected TextAlertStore(Context context) {
        super(context);
    }

    @Override
    public void addAlert(Context context, TextAlert alert) {
        super.addAlert(context, alert);
        updaterAlertPhrases(context, alert);
    }

    @Override
    public void updateAlert(Context context, TextAlert alert) {
        super.updateAlert(context, alert);
        updaterAlertPhrases(context, alert);
    }

    private void updaterAlertPhrases(Context context, TextAlert alert) {
        SQLiteDatabase db = getWritableDatabase(context);
        db.delete(getPhraseTableName(),
                DbContractTextRule.TextRulePhraseEntry.COLUMN_ALERT_RULE_ID + " = ?",
                new String[] {Long.toString(alert.getId())});
        db.close();

        for (String phrase : alert.getPhrases()) {
            ContentValues phraseValues = new ContentValues();
            phraseValues.put(DbContractTextRule.TextRulePhraseEntry.COLUMN_ALERT_RULE_ID, alert.getId());
            phraseValues.put(DbContractTextRule.TextRulePhraseEntry.COLUMN_TEXT_PHRASE, phrase);
            db.insert(getPhraseTableName(), null, phraseValues);
        }

        db.close();
    }

    @Override
    protected TextAlert getNewAlert() {
        return new TextAlert();
    }

    @Override
    protected void performRemainingDropCommands(SQLiteDatabase db, TextAlert alert) {
        db.delete(getPhraseTableName(), DbContractTextRule.TextRulePhraseEntry.COLUMN_ALERT_RULE_ID + " = " + alert.getId(), null);
    }

    @Override
    protected TextAlert readAlertFromDb(Context context, long ruleId) {
        TextAlert alert = super.readAlertFromDb(context, ruleId);

        SQLiteDatabase db = getReadableDatabase(context);

        List<String> phrases = new ArrayList<String>();
        Cursor phrasesCursor = db.query(getPhraseTableName(),
                new String[]{DbContractTextRule.TextRulePhraseEntry.COLUMN_TEXT_PHRASE},
                DbContractTextRule.TextRulePhraseEntry.COLUMN_ALERT_RULE_ID + " = " + ruleId,
                null, null, null, null);

        phrasesCursor.moveToFirst();

        while (!phrasesCursor.isAfterLast()) {
            String phrase = phrasesCursor.getString(phrasesCursor.getColumnIndex(DbContractTextRule.TextRulePhraseEntry.COLUMN_TEXT_PHRASE));
            phrases.add(phrase);
            phrasesCursor.moveToNext();
        }

        phrasesCursor.close();

        alert.setPhrases(phrases);
        return alert;
    }

    @Override
    protected String getRuleTableName() {
        return DbContractTextRule.TextRuleEntry.TABLE_NAME;
    }

    @Override
    protected String getContactTableName() {
        return DbContractTextRule.TextRuleContactEntry.TABLE_NAME;
    }

    protected String getPhraseTableName() {
        return DbContractTextRule.TextRulePhraseEntry.TABLE_NAME;
    }

    @Override
    protected SQLiteDatabase getReadableDatabase(Context context) {
        DbOpenHelperText dbOpenHelper = new DbOpenHelperText(context);
        return dbOpenHelper.getReadableDatabase();
    }

    @Override
    protected SQLiteDatabase getWritableDatabase(Context context) {
        DbOpenHelperText dbOpenHelper = new DbOpenHelperText(context);
        return dbOpenHelper.getWritableDatabase();
    }

    @Override
    protected void addRemainingParameters(ContentValues ruleValues, Alert alert) {
        TextAlert textAlert = (TextAlert) alert;
        ruleValues.put(DbContractTextRule.TextRuleEntry.COLUMN_ALERT_DURATION, textAlert.getAlertDuration());
    }
}
