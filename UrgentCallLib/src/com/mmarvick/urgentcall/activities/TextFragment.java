package com.mmarvick.urgentcall.activities;

import java.util.List;

import com.mmarvick.urgentcall.data.Alert;
import com.mmarvick.urgentcall.data.AlertText;
import com.mmarvick.urgentcall.views.AlertView;
import com.mmarvick.urgentcall.views.TextAlertView;

public class TextFragment extends AlertFragment {
	
	protected List<? extends Alert> getAlerts() {
		return AlertText.getAlerts(getActivity());
	}
	
	protected AlertView createAlertView() {
		return new TextAlertView(getActivity(), this);
	}
	
	protected Alert createAlert() {
		return new AlertText(getActivity());
	}

}
