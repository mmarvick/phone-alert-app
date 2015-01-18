package com.mmarvick.urgentcall.helpers;

import android.content.Context;
import android.content.Intent;

import com.mmarvick.urgentcall.R;

/**
 * Helper for sharing alerts
 */
public class ShareHelper {
    public static void share(Context context, String subject, String message) {
        String intentPickerTitle = context.getString(R.string.share_by);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        context.startActivity(Intent.createChooser(shareIntent, intentPickerTitle));
    }
}
