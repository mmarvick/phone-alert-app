package com.mmarvick.urgentcall.activities;

import android.app.Activity;
import android.support.v4.app.Fragment;

public class TabFragment extends Fragment {
	private MainNewActivity mActivity;
	
	public void fragUpdateSettings() {
		mActivity.updateSettings();
	}
	
	public void settingsUpdated() {};
	
	@Override
	public void onAttach(Activity activity) {
		mActivity = (MainNewActivity) activity;
		super.onAttach(activity);
	}
	
	public MainNewActivity getMainActivity() {
		return mActivity;
	}
}
