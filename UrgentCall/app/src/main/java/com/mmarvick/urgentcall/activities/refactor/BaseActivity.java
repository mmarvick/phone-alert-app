package com.mmarvick.urgentcall.activities.refactor;

import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.mmarvick.urgentcall.R;

/**
 * Created by michael on 5/15/16.
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.dark_red));
        }
    }

    public void setSupportActionBar(@IdRes int resId) {
        Toolbar toolbar = (Toolbar) findViewById(resId);
        if (toolbar == null) {
            return;
        }

        setSupportActionBar(toolbar);
    }
}
