package com.mmarvick.urgentcall.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends android.support.v4.view.ViewPager {

	private boolean mEnabled = true;
	
	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mEnabled) {
			return super.onTouchEvent(event);
		} else {
			return true;
		}
	}
	
	@Override
	public void setCurrentItem(int item) {
		if (mEnabled) {
			super.setCurrentItem(item);
		}
	}
	
	public void setScrollable(boolean enabled) {
		mEnabled = enabled;
	}
}
