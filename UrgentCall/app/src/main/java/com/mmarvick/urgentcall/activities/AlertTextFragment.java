package com.mmarvick.urgentcall.activities;

import java.util.List;

import com.mmarvick.urgentcall.data.base.Alert;
import com.mmarvick.urgentcall.data.text.TextAlert;
import com.mmarvick.urgentcall.views.AlertView;
import com.mmarvick.urgentcall.views.TextAlertView;

public class AlertTextFragment extends AlertFragment {
	
	protected List<? extends Alert> getAlerts() {
		return TextAlert.getAlerts(getActivity());
	}
	
	protected AlertView createAlertView() {
		return new TextAlertView(getActivity(), this);
	}
	
	protected Alert createAlert() {
		return new TextAlert(getActivity());
	}

}
