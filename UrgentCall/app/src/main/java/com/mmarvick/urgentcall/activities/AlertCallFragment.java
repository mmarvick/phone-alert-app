package com.mmarvick.urgentcall.activities;

import java.util.List;

import com.mmarvick.urgentcall.data.base.Alert;
import com.mmarvick.urgentcall.data.call.CallAlert;
import com.mmarvick.urgentcall.data.call.CallAlertStore;
import com.mmarvick.urgentcall.views.AlertView;
import com.mmarvick.urgentcall.views.CallAlertView;

public class AlertCallFragment extends AlertFragment {
	
	protected List<? extends Alert> getAlerts() {
        CallAlertStore callAlertStore = CallAlertStore.getInstance(getActivity());
        return callAlertStore.getAlerts();
	}
	
	protected AlertView createAlertView() {
		return new CallAlertView(getActivity(), this);
	}
	
	protected Alert createAlert() {
		return new CallAlert(getActivity());
	}

}
