package com.mmarvick.urgentcall.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class TabFragment extends Fragment {
	private MainActivity mActivity;
	
	@Override
	public void onAttach(Activity activity) {
		mActivity = (MainActivity) activity;
		super.onAttach(activity);
	}
	
	public MainActivity getMainActivity() {
		return mActivity;
	}
	
	public abstract void fragUpdateSettings();
}
