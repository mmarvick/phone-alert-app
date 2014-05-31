package com.mmarvick.urgentcall.activities;

import android.app.Activity;
import android.support.v4.app.Fragment;

public class TabFragment extends Fragment {
	private MainNewActivity mActivity;
	private boolean mCanUpdate = false;

	public boolean isUpdatable() {
		return mCanUpdate;
	}
	
	public void setUpdatable(boolean canUpdate) {
		mCanUpdate = canUpdate;
	}
	
	public void fragUpdateSettings() {
		setUpdatable(true);
	}
	
	@Override
	public void onAttach(Activity activity) {
		mActivity = (MainNewActivity) activity;
		super.onAttach(activity);
	}
	
	public MainNewActivity getMainActivity() {
		return mActivity;
	}
}
