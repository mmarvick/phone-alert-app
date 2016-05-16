package com.mmarvick.urgentcall.activities.refactor;

import android.support.annotation.IdRes;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by michael on 5/15/16.
 */
public class ToolbarActivity extends ActionBarActivity {
    public void setSupportActionBar(@IdRes int resId) {
        Toolbar toolbar = (Toolbar) findViewById(resId);
        if (toolbar == null) {
            return;
        }

        setSupportActionBar(toolbar);
    }
}
