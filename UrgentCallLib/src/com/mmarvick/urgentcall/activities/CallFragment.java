package com.mmarvick.urgentcall.activities;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.Alert;
import com.mmarvick.urgentcall.data.AlertCall;
import com.mmarvick.urgentcall.views.CallAlertView;

public class CallFragment extends TabFragment {
	
	private List<AlertCall> alerts;
	private List<CallAlertView> alertViews;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_test, container, false);
		
		alerts = AlertCall.getAlerts(getActivity());
		alertViews = new ArrayList<CallAlertView>();
		
		for (AlertCall alert : alerts) {
			CallAlertView callAlertView = new CallAlertView(getActivity());
			callAlertView.addAlert(alert);
			alertViews.add(callAlertView);
			((ViewGroup) view).addView(callAlertView);
			
		}
		
		return view;
    }

}
