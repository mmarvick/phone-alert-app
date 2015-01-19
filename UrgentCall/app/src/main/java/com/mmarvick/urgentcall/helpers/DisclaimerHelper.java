package com.mmarvick.urgentcall.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.mmarvick.urgentcall.R;

/**
 * Created by michael on 1/19/15.
 */
public class DisclaimerHelper {
    // SETTINGS FOR THE DISCLAIMER VERSION MOST RECENTLY AGREED TO
    public final static String DISCLAIMER_VERSION = "DISCLAIMER_VERSION";
    public final static String DISCLAIMER_VIEWED = "DISCLAIMER_VIEWED";
    public final static int DISCLAIMER_DEFAULT = 0;

    public static boolean disclaimerIsApproved(Context context) {
        if (disclaimerIsCurrent(context)) {
            return true;
        } else if (getDisclaimerVersion(context) == DISCLAIMER_DEFAULT) {
            return false;
        } else {
            return !hasDisclaimerBeenViewed(context);
        }
    }

    public static boolean disclaimerIsCurrent(Context context) {
        int current = context.getResources().getInteger(R.integer.disclaimer_version);
        return (getDisclaimerVersion(context) == current);
    }

    private static int getDisclaimerVersion(Context context) {
        SharedPreferences prefs = PrefHelper.getPrefs(context);
        return prefs.getInt(DISCLAIMER_VERSION, DISCLAIMER_DEFAULT);
    }

    private static boolean hasDisclaimerBeenViewed(Context context) {
        SharedPreferences prefs = PrefHelper.getPrefs(context);
        return prefs.getBoolean(DISCLAIMER_VIEWED, false);
    }

    public static void disclaimerAgreed(Context context) {
        SharedPreferences prefs = PrefHelper.getPrefs(context);
        SharedPreferences.Editor edit = prefs.edit();
        int version = context.getResources().getInteger(R.integer.disclaimer_version);
        edit.putInt(DISCLAIMER_VERSION, version);
        edit.putBoolean(DISCLAIMER_VIEWED, false);
        edit.commit();
    }

    public static void disclaimerViewed(Context context) {
        SharedPreferences prefs = PrefHelper.getPrefs(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(DISCLAIMER_VIEWED, true);
        edit.commit();
    }
}

