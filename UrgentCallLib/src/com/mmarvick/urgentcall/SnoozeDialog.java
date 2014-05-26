package com.mmarvick.urgentcall;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.TimePicker;

public class SnoozeDialog extends TimePickerDialog {

	public SnoozeDialog(Context context, OnTimeSetListener callBack,
			int hourOfDay, int minute, boolean is24HourView) {
		super(context, callBack, hourOfDay, minute, is24HourView);
		setTitle("Snooze for...");
	}

	@Override
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		super.onTimeChanged(view, hourOfDay, minute);
		setTitle("Snooze for...");
	}
	
}
