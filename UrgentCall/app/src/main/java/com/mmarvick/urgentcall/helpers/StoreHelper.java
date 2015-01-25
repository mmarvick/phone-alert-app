package com.mmarvick.urgentcall.helpers;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.mmarvick.urgentcall.R;

/**
 * Created by michael on 1/24/15.
 */
public class StoreHelper {
    public static void goToStore(String packageName, Context context) {
        int store = context.getResources().getInteger(R.integer.store);
        Uri uri;

        if (store == context.getResources().getInteger(R.integer.store_amazon)) {
            try {
                uri = Uri.parse("amzn://apps/android?p=" + packageName);
                startActivityWithUri(context, uri);
            } catch (android.content.ActivityNotFoundException anfe) {
                uri = Uri.parse("http://www.amazon.com/gp/mas/dl/android?p=" + packageName);
                startActivityWithUri(context, uri);
            }
            Toast.makeText(context, "TO DO", Toast.LENGTH_LONG).show();
        } else {
            try {
                uri = Uri.parse(context.getString(R.string.url_play_app) + packageName);
                startActivityWithUri(context, uri);
            } catch (android.content.ActivityNotFoundException anfe) {
                uri = Uri.parse(context.getString(R.string.url_play_web) + packageName);
                startActivityWithUri(context, uri);
            }
        }



    }

    private static void startActivityWithUri(Context context, Uri uri) throws ActivityNotFoundException {
        Intent upgradeIntent = new Intent(Intent.ACTION_VIEW, uri);
        upgradeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        context.startActivity(upgradeIntent);
    }
}
