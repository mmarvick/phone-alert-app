package com.mmarvick.urgentcall.views;


import com.mmarvick.urgentcall.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class CallAlertView extends LinearLayout {
	public CallAlertView(Context context) {
		this(context, null);
	}
	
	public CallAlertView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_call_alert, this);
	}
	
}

