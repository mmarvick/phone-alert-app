package com.mmarvick.urgentcall.activities.refactor;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mmarvick.urgentcall.R;


/**
 * A placeholder fragment containing a simple view.
 */
public class CallAlertFragment extends Fragment {

    public CallAlertFragment() {

    }

    public static CallAlertFragment newInstance() {
        CallAlertFragment fragment = new CallAlertFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_call_alert, container, false);
    }
}
