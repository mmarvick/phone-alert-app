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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class HomeFragment extends TabFragment {
	private MainNewActivity mActivity;
	private LinearLayout mLayoutMsgState;
	private LinearLayout mLayoutRCState;
	private LinearLayout mLayoutSCState;
	private TextView mTextMsgState;
	private TextView mTextRCState;
	private TextView mTextSCState;
	private Button mButtonAppState;

	@Override
	public void onAttach(Activity activity) {
		mActivity = (MainNewActivity) activity;
		super.onAttach(activity);
	}		
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		Log.e("Run", "onCreateView");
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		mLayoutMsgState = (LinearLayout) view.findViewById(R.id.linearLayout_home_msg);
		mLayoutRCState = (LinearLayout) view.findViewById(R.id.linearLayout_home_rc);
		mLayoutSCState = (LinearLayout) view.findViewById(R.id.linearLayout_home_sc);
		mTextMsgState = (TextView) view.findViewById(R.id.textView_home_msg_state);
		mTextRCState = (TextView) view.findViewById(R.id.textView_home_rc_state);
		mTextSCState = (TextView) view.findViewById(R.id.textView_home_sc_state);
		mButtonAppState = (Button) view.findViewById(R.id.button_home_state);
		
		mLayoutMsgState.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mActivity.setTab(MainNewActivity.TAB_MSG);
			}
		});
		
		mLayoutRCState.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mActivity.setTab(MainNewActivity.TAB_CALL);
			}
		});
		
		mLayoutSCState.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mActivity.setTab(MainNewActivity.TAB_CALL);
			}
		});		
		
		mButtonAppState.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (PrefHelper.isSnoozing(mActivity.getApplicationContext())) {
					mActivity.endSnooze();
				}
				else if (PrefHelper.getState(mActivity.getApplicationContext(), Constants.OVERALL_STATE) == Constants.URGENT_CALL_STATE_OFF) {
					PrefHelper.setState(mActivity.getApplicationContext(), Constants.OVERALL_STATE, Constants.URGENT_CALL_STATE_ON);
				} else {
					PrefHelper.setState(mActivity.getApplicationContext(), Constants.OVERALL_STATE, Constants.URGENT_CALL_STATE_OFF);
				}
				setButtonState();
			}
		});
		setText();
		return view;
    }
	
	public void settingsUpdated() {
		setText();
	}
	
	@Override
	public void onResume() {
		setText();
		super.onResume();
	}
	
	public void setText() {
		Log.e("Text", "Was set");
		setStateText(mTextMsgState, RulesEntry.MSG_STATE);
		setStateText(mTextRCState, RulesEntry.REPEATED_CALL_STATE);
		setStateText(mTextSCState, RulesEntry.SINGLE_CALL_STATE);
		setButtonState();
	}
	
	public void setStateText(TextView textView, String alertType) {
		if (textView != null) {
			if (PrefHelper.getState(mActivity.getApplicationContext(), alertType) == Constants.URGENT_CALL_STATE_OFF) {
				textView.setText(mActivity.getString(R.string.status_nocaps_off));
				textView.setTextColor(Color.RED);
			} else {
				textView.setText(mActivity.getString(R.string.status_nocaps_on));
				textView.setTextColor(Color.GREEN);
			}
		}
	}
	
	private void setButtonState() {
		if (PrefHelper.isSnoozing(mActivity.getApplicationContext())) {
			mButtonAppState.setText(mActivity.getString(R.string.status_allcaps_snoozing));
			mButtonAppState.setTextColor(Color.RED);			
		}
		else if (PrefHelper.getState(mActivity.getApplicationContext(), Constants.OVERALL_STATE) == Constants.URGENT_CALL_STATE_OFF) {
			mButtonAppState.setText(mActivity.getString(R.string.status_allcaps_off));
			mButtonAppState.setTextColor(Color.RED);
		} else {
			mButtonAppState.setText(mActivity.getString(R.string.status_allcaps_on));
			mButtonAppState.setTextColor(Color.GREEN);
		}
	}
}
