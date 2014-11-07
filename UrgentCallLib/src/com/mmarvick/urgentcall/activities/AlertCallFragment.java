package com.mmarvick.urgentcall.activities;

import java.util.List;

import com.mmarvick.urgentcall.data.Alert;
import com.mmarvick.urgentcall.data.AlertCall;
import com.mmarvick.urgentcall.views.AlertView;
import com.mmarvick.urgentcall.views.CallAlertView;

public class AlertCallFragment extends AlertFragment {
	
	protected List<? extends Alert> getAlerts() {
		return AlertCall.getAlerts(getActivity());
	}
	
	protected AlertView createAlertView() {
		return new CallAlertView(getActivity(), this);
	}
	
	protected Alert createAlert() {
		return new AlertCall(getActivity());
	}

}
