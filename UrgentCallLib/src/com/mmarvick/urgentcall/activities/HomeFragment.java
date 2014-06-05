package com.mmarvick.urgentcall.activities;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.PrefHelper;
import com.mmarvick.urgentcall.data.RulesDbContract.RulesEntry;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeFragment extends TabFragment {
	private LinearLayout mLayoutMsgState;
	private LinearLayout mLayoutRCState;
	private LinearLayout mLayoutSCState;
	private LinearLayout mLayoutAllOff;
	private TextView mTextMsgState;
	private TextView mTextRCState;
	private TextView mTextSCState;
	private TextView mTextAllOffState;
	private TextView mTextSnoozeFor;
	private TextView mTextSnoozeTime;
	
	private Button mButtonAppState;		
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		Log.e("Run", "onCreateView");
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		mLayoutMsgState = (LinearLayout) view.findViewById(R.id.linearLayout_home_msg);
		mLayoutRCState = (LinearLayout) view.findViewById(R.id.linearLayout_home_rc);
		mLayoutSCState = (LinearLayout) view.findViewById(R.id.linearLayout_home_sc);
		mLayoutAllOff = (LinearLayout) view.findViewById(R.id.linearLayout_home_all_off);
		mTextMsgState = (TextView) view.findViewById(R.id.textView_home_msg_state);
		mTextRCState = (TextView) view.findViewById(R.id.textView_home_rc_state);
		mTextSCState = (TextView) view.findViewById(R.id.textView_home_sc_state);
		mTextAllOffState = (TextView) view.findViewById(R.id.textView_home_all_off_state);
		mTextSnoozeFor = (TextView) view.findViewById(R.id.textView_home_snooze_for);
		mTextSnoozeTime = (TextView) view.findViewById(R.id.textView_home_snooze_time);
		mButtonAppState = (Button) view.findViewById(R.id.button_home_state);
		
		mLayoutMsgState.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getMainActivity().setTab(MainActivity.TAB_MSG);
			}
		});
		
		mLayoutRCState.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getMainActivity().setTab(MainActivity.TAB_RC);
			}
		});
		
		mLayoutSCState.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getMainActivity().setTab(MainActivity.TAB_SC);
			}
		});		
		
		mButtonAppState.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (PrefHelper.isSnoozing(getMainActivity().getApplicationContext())) {
					getMainActivity().endSnooze();
				}
				else if (PrefHelper.getState(getMainActivity().getApplicationContext(), Constants.APP_STATE) == Constants.URGENT_CALL_STATE_OFF) {
					PrefHelper.setState(getMainActivity().getApplicationContext(), Constants.APP_STATE, Constants.URGENT_CALL_STATE_ON);
				} else {
					PrefHelper.setState(getMainActivity().getApplicationContext(), Constants.APP_STATE, Constants.URGENT_CALL_STATE_OFF);
				}
				
				getMainActivity().updateSettings();
			}
		});
		
		fragUpdateSettings();
		return view;
    }
	
	@Override
	public void fragUpdateSettings() {
		showTextViews();
		setStateText(mTextMsgState, RulesEntry.MSG_STATE);
		setStateText(mTextRCState, RulesEntry.RC_STATE);
		setStateText(mTextSCState, RulesEntry.SC_STATE);
		setSnoozeTime();
		
		mTextAllOffState.setTextColor(Color.RED);
		
		setButtonState();
		super.fragUpdateSettings();
	}
	
	public void setStateText(TextView textView, String alertType) {		
		if (textView != null) {
			if (PrefHelper.getState(getMainActivity().getApplicationContext(), alertType) == Constants.URGENT_CALL_STATE_OFF) {
				textView.setText(getMainActivity().getString(R.string.status_nocaps_off));
				textView.setTextColor(Color.RED);
			} else {
				textView.setText(getMainActivity().getString(R.string.status_nocaps_on));
				textView.setTextColor(Color.GREEN);
			}
		}
	}
	
	private void showTextViews() {
		int state = PrefHelper.getState(getMainActivity(), Constants.APP_STATE);
		if (state == Constants.URGENT_CALL_STATE_OFF || PrefHelper.isSnoozing(getMainActivity())) {
			mLayoutMsgState.setVisibility(View.INVISIBLE);
			mLayoutRCState.setVisibility(View.INVISIBLE);
			mLayoutSCState.setVisibility(View.INVISIBLE);
			mLayoutAllOff.setVisibility(View.VISIBLE);			
		} else {
			mLayoutMsgState.setVisibility(View.VISIBLE);
			mLayoutRCState.setVisibility(View.VISIBLE);
			mLayoutSCState.setVisibility(View.VISIBLE);
			mLayoutAllOff.setVisibility(View.INVISIBLE);
		}
		
		if (PrefHelper.isSnoozing(getMainActivity())) {
			mTextSnoozeFor.setVisibility(View.VISIBLE);
			mTextSnoozeTime.setVisibility(View.VISIBLE);
		} else {
			mTextSnoozeFor.setVisibility(View.INVISIBLE);
			mTextSnoozeTime.setVisibility(View.INVISIBLE);			
		}
	}	
	
	private void setButtonState() {
		if (PrefHelper.isSnoozing(getMainActivity().getApplicationContext())) {
			mButtonAppState.setText(getMainActivity().getString(R.string.status_allcaps_snoozing));
			mButtonAppState.setTextColor(Color.RED);			
		}
		else if (PrefHelper.getState(getMainActivity().getApplicationContext(), Constants.APP_STATE) == Constants.URGENT_CALL_STATE_OFF) {
			mButtonAppState.setText(getMainActivity().getString(R.string.status_allcaps_off));
			mButtonAppState.setTextColor(Color.RED);
		} else {
			mButtonAppState.setText(getMainActivity().getString(R.string.status_allcaps_on));
			mButtonAppState.setTextColor(Color.GREEN);
		}
	}
	
	public void setSnoozeTime() {
		if (PrefHelper.isSnoozing(getMainActivity())) {
			long time = PrefHelper.snoozeRemaining(getMainActivity());
			long allsec = time / 1000;
			long sec = allsec % 60;
			long min = (allsec % 3600) / 60;
			long hour = allsec / 3600;
			String minText = ((min<10 & hour > 0) ? "0" : "") + min + ":";
			String secText = ((sec<10) ? "0" : "") + sec;
			String hourText = ((hour == 0) ? "" : hour + ":");
			String clock = hourText + minText + secText;
			mTextSnoozeTime.setText(clock);
		}
	}
	
}
