package com.mmarvick.urgentcall.activities;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.OldPrefHelper;
import com.mmarvick.urgentcall.data.OldDbContractDatabase.RulesEntryOld;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeFragment extends Fragment {
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
	private TextView mTextSafelySilence;
	
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
		mTextSafelySilence = (TextView) view.findViewById(R.id.textView_home_safely_silence); 
		mButtonAppState = (Button) view.findViewById(R.id.button_home_state);	
		
		mButtonAppState.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (OldPrefHelper.isSnoozing(getActivity().getApplicationContext())) {
					//getActivity().endSnooze();
				}
				else if (OldPrefHelper.getState(getActivity().getApplicationContext(), Constants.APP_STATE) == Constants.URGENT_CALL_STATE_OFF) {
					OldPrefHelper.setState(getActivity().getApplicationContext(), Constants.APP_STATE, Constants.URGENT_CALL_STATE_ON);
				} else {
					OldPrefHelper.setState(getActivity().getApplicationContext(), Constants.APP_STATE, Constants.URGENT_CALL_STATE_OFF);
				}
				
				//getActivity().updateSettings();
			}
		});
		
		return view;
    }
	
	public void setStateText(TextView textView, String alertType) {		
		if (textView != null) {
			if (OldPrefHelper.getState(getActivity().getApplicationContext(), alertType) == Constants.URGENT_CALL_STATE_OFF) {
				textView.setText(getActivity().getString(R.string.status_nocaps_off));
				textView.setTextColor(Color.RED);
			} else {
				textView.setText(getActivity().getString(R.string.status_nocaps_on));
				textView.setTextColor(Color.GREEN);
			}
		}
	}
	
	private void showTextViews() {
		int state = OldPrefHelper.getState(getActivity(), Constants.APP_STATE);
		if (state == Constants.URGENT_CALL_STATE_OFF || OldPrefHelper.isSnoozing(getActivity())) {
			mTextSafelySilence.setVisibility(View.INVISIBLE);
			mLayoutMsgState.setVisibility(View.INVISIBLE);
			mLayoutRCState.setVisibility(View.INVISIBLE);
			mLayoutSCState.setVisibility(View.INVISIBLE);
			mLayoutAllOff.setVisibility(View.VISIBLE);
		} else {
			mTextSafelySilence.setVisibility(View.VISIBLE);	
			mLayoutMsgState.setVisibility(View.VISIBLE);
			mLayoutRCState.setVisibility(View.VISIBLE);
			mLayoutSCState.setVisibility(View.VISIBLE);	
			mLayoutAllOff.setVisibility(View.INVISIBLE);
		}
		
		if (OldPrefHelper.isSnoozing(getActivity())) {
			mTextSnoozeFor.setVisibility(View.VISIBLE);
			mTextSnoozeTime.setVisibility(View.VISIBLE);
		} else {
			mTextSnoozeFor.setVisibility(View.INVISIBLE);
			mTextSnoozeTime.setVisibility(View.INVISIBLE);			
		}
	}	
	
	private void setButtonState() {
		if (OldPrefHelper.isSnoozing(getActivity().getApplicationContext())) {
			mButtonAppState.setText(getActivity().getString(R.string.status_allcaps_snoozing));
			mButtonAppState.setTextColor(Color.RED);			
		}
		else if (OldPrefHelper.getState(getActivity().getApplicationContext(), Constants.APP_STATE) == Constants.URGENT_CALL_STATE_OFF) {
			mButtonAppState.setText(getActivity().getString(R.string.status_allcaps_off));
			mButtonAppState.setTextColor(Color.RED);
		} else {
			mButtonAppState.setText(getActivity().getString(R.string.status_allcaps_on));
			mButtonAppState.setTextColor(Color.GREEN);
		}
	}
	
	public void setSnoozeTime() {
		if (OldPrefHelper.isSnoozing(getActivity())) {
			long time = OldPrefHelper.snoozeRemaining(getActivity());
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