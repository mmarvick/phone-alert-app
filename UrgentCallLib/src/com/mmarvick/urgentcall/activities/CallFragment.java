package com.mmarvick.urgentcall.activities;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.PrefHelper;
import com.mmarvick.urgentcall.data.RulesDbContract.RulesEntry;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.TextView;

public class CallFragment extends TabFragment {

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View fragmentView = inflater.inflate(R.layout.fragment_call, container, false);
		return fragmentView;
    }
	
	@Override
	public void fragUpdateSettings() {
		super.fragUpdateSettings();
	}

}
