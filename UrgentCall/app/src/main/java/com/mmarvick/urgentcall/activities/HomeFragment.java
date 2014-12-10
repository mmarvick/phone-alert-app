package com.mmarvick.urgentcall.activities;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.PrefHelper;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class HomeFragment extends Fragment {
	private View mView;
	
	private TextView mTextSnoozeFor;
	private TextView mTextSnoozeTime;
	private TextView mTextSafelySilence;
	
	private Button mButtonAppState;		
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

		mView = inflater.inflate(R.layout.fragment_home, container, false);

		findViews();
		initCallbacks();
		initViews();
		
		((MainActivity) getActivity()).setHomeFragment(this);
		
		return mView;
    }
	
	private void toggleState() {
		if (PrefHelper.isSnoozing(getActivity().getApplicationContext())) {
			((MainActivity) getActivity()).endSnooze();
		}
		else if (PrefHelper.getState(getActivity().getApplicationContext(), Constants.APP_STATE) == Constants.URGENT_CALL_STATE_OFF) {
			PrefHelper.setState(getActivity().getApplicationContext(), Constants.APP_STATE, Constants.URGENT_CALL_STATE_ON);
		} else {
			PrefHelper.setState(getActivity().getApplicationContext(), Constants.APP_STATE, Constants.URGENT_CALL_STATE_OFF);
		}
		
		((MainActivity) getActivity()).disableEnableWhenOff();
		updateViewState();		
	}
	
	public void updateViewState() {
		if (PrefHelper.isSnoozing(getActivity().getApplicationContext())) {
			mTextSnoozeFor.setVisibility(View.VISIBLE);
			mTextSnoozeTime.setVisibility(View.VISIBLE);
			
			mTextSafelySilence.setVisibility(View.INVISIBLE);
			mButtonAppState.setText(getActivity().getString(R.string.status_allcaps_snoozing));
			mButtonAppState.setTextColor(Color.RED);	
			
		} else {
			mTextSnoozeFor.setVisibility(View.INVISIBLE);
			mTextSnoozeTime.setVisibility(View.INVISIBLE);	
			
			if (PrefHelper.getState(getActivity().getApplicationContext(), Constants.APP_STATE) == Constants.URGENT_CALL_STATE_OFF) {
				mTextSafelySilence.setVisibility(View.INVISIBLE);
				mButtonAppState.setText(getActivity().getString(R.string.status_allcaps_off));
				mButtonAppState.setTextColor(Color.RED);
			} else {
				mTextSafelySilence.setVisibility(View.VISIBLE);
				mButtonAppState.setText(getActivity().getString(R.string.status_allcaps_on));
				mButtonAppState.setTextColor(Color.GREEN);
			}
		}
	}
	
	public void updateViewSnooze() {
		if (PrefHelper.isSnoozing(getActivity())) {
			long time = PrefHelper.snoozeRemaining(getActivity());
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
	
	private void findViews() {
		mTextSnoozeFor = (TextView) mView.findViewById(R.id.textView_home_snooze_for);
		mTextSnoozeTime = (TextView) mView.findViewById(R.id.textView_home_snooze_time);
		mTextSafelySilence = (TextView) mView.findViewById(R.id.textView_home_safely_silence); 
		mButtonAppState = (Button) mView.findViewById(R.id.button_home_state);			
	}
	
	private void initCallbacks() {
		mButtonAppState.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				toggleState();
				
			}
		});
	}
	
	private void initViews() {
		updateViewState();
		updateViewSnooze();
	}
	
}