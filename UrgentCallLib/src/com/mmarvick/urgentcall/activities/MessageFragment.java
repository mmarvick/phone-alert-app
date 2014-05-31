package com.mmarvick.urgentcall.activities;

import com.mmarvick.urgentcall.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MessageFragment extends TabFragment {
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_message, container, false);
    }
	
	public void settingsUpdated() {
		
	}

}
