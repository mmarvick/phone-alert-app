package com.mmarvick.urgentcall.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mmarvick.urgentcall.R;

public class TestFragment extends TabFragment {
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_test, container, false);
		return view;
    }
	
	@Override
	public void fragUpdateSettings() {

	}
	
	
	

}
