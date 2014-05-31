package com.mmarvick.urgentcall.activities;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.PrefHelper;
import com.mmarvick.urgentcall.data.RulesDbContract.RulesEntry;
import com.mmarvick.urgentcall.widgets.OnOptionsChangedListener;
import com.mmarvick.urgentcall.widgets.StateListsPrompt;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MessageFragment extends TabFragment {
	private Button mButtonMsgState;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_msg, container, false);
		mButtonMsgState = (Button) view.findViewById(R.id.button_msg_state);
		mButtonMsgState.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				StateListsPrompt msgStatePrompt = new StateListsPrompt(getMainActivity(), RulesEntry.MSG_STATE, getMainActivity());
				msgStatePrompt.setOnOptionsChangedListener(new OnOptionsChangedListener() {
						
						@Override
						public void onOptionsChanged() {
							getMainActivity().updateSettings();
							
						}
					});
				msgStatePrompt.show();
			}
		});		
		
		fragUpdateSettings();
		return view;
    }
	
	@Override
	public void fragUpdateSettings() {
		setButtonState();
		super.fragUpdateSettings();
	}
	
	private void setButtonState() {
		if (PrefHelper.getState(getMainActivity().getApplicationContext(), RulesEntry.MSG_STATE) == Constants.URGENT_CALL_STATE_OFF) {
			mButtonMsgState.setText(getMainActivity().getString(R.string.status_allcaps_off));
			mButtonMsgState.setTextColor(Color.RED);
		} else {
			mButtonMsgState.setText(getMainActivity().getString(R.string.status_allcaps_on));
			mButtonMsgState.setTextColor(Color.GREEN);
		}
	}
}
