package com.mmarvick.urgentcall.widgets;

import com.mmarvick.urgentcall.R;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.TimePicker;

public class SnoozeDialog extends TimePickerDialog {
	private String title;
	
	public SnoozeDialog(Context context, OnTimeSetListener callBack,
			int hourOfDay, int minute, boolean is24HourView) {
		super(context, callBack, hourOfDay, minute, is24HourView);
		title = context.getString(R.string.snooze_dialog_title);
		setTitle(title);
	}

	@Override
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		super.onTimeChanged(view, hourOfDay, minute);
		setTitle(title);
	}
	
}
