package com.mmarvick.urgentcall.activities.refactor;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;

import com.mmarvick.urgentcall.R;

public class CallAlertActivity extends ToolbarActivity {
    private static final String ARG_FRAGMENT = "call_fragment";
    private CallAlertFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);
        setSupportActionBar(R.id.toolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (savedInstanceState != null) {
            fragment = (CallAlertFragment)
                    fragmentManager.getFragment(savedInstanceState, ARG_FRAGMENT);
        }

        if (fragment == null) {
            fragment = CallAlertFragment.newInstance();
        }

        if (!fragment.isAdded()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        getSupportFragmentManager().putFragment(outState, ARG_FRAGMENT, fragment);
    }
}
