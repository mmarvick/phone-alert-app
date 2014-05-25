package com.mmarvick.urgentcall;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.util.Log;

public class MinMaxEditTextPreference extends EditTextPreference {
	public final String NAMESPACE = "http://schemas.android.com/apk/lib/com.mmarvick.urgentcall";
	public final String MIN = "min";
	public final String MAX = "max";
	public final int DEFAULT_MIN = Integer.MIN_VALUE;
	public final int DEFAULT_MAX = Integer.MAX_VALUE;
	
	private int min = DEFAULT_MIN;
	private int max = DEFAULT_MAX;

	public MinMaxEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setMinMax(attrs);
	}

	public MinMaxEditTextPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setMinMax(attrs);
	}	
	
	public MinMaxEditTextPreference(Context context) {
		super(context);
	}
	
	private void setMinMax(AttributeSet attrs) {
		min = attrs.getAttributeIntValue(NAMESPACE, MIN, DEFAULT_MIN);
		max = attrs.getAttributeIntValue(NAMESPACE, MAX, DEFAULT_MAX);
		Log.e("MIN:", "" + min);
	}
	
	@Override
	public void setText(String text) {
		int value;
		
		try {
			value = Integer.parseInt(text);
		} catch (NumberFormatException e) {
			return;
		}
		
		if (value < min || value > max) {
			return;
		}
		
		super.setText(text);
	}

}
