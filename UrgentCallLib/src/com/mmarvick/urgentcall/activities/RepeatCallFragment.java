package com.mmarvick.urgentcall.activities;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.PrefHelper;
import com.mmarvick.urgentcall.data.RulesDbHelper;
import com.mmarvick.urgentcall.data.RulesDbContract.RulesEntry;
import com.mmarvick.urgentcall.widgets.OnOptionsChangedListener;
import com.mmarvick.urgentcall.widgets.StateListsPrompt;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.TextView;

public class RepeatCallFragment extends TabFragment {
	private Button mButtonRCState;
	private TextView mTextViewCallNumber;
	private TextView mTextViewCallTime;
	private TextView mTextViewFrom;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_call_repeat, container, false);
		mButtonRCState = (Button) view.findViewById(R.id.button_rc_state);
		mTextViewCallNumber = (TextView) view.findViewById(R.id.textView_rc_call_number);
		mTextViewCallTime = (TextView) view.findViewById(R.id.textView_rc_call_time);
		mTextViewFrom = (TextView) view.findViewById(R.id.textView_rc_from);
		mButtonRCState.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				StateListsPrompt rcStatePrompt = new StateListsPrompt(getMainActivity(), RulesEntry.RC_STATE, getMainActivity());
				rcStatePrompt.setOnOptionsChangedListener(new OnOptionsChangedListener() {
						
						@Override
						public void onOptionsChanged() {
							getMainActivity().updateSettings();
							
						}
					});
				rcStatePrompt.show();
			}
		});		
		
		fragUpdateSettings();		
		return view;
    }
	
	@Override
	public void fragUpdateSettings() {
		setButtonState();
		setText();
		super.fragUpdateSettings();
	}
	
	private void setButtonState() {
		if (PrefHelper.getState(getMainActivity().getApplicationContext(), RulesEntry.RC_STATE) == Constants.URGENT_CALL_STATE_OFF) {
			mButtonRCState.setText(getMainActivity().getString(R.string.status_allcaps_off));
			mButtonRCState.setTextColor(Color.RED);
		} else {
			mButtonRCState.setText(getMainActivity().getString(R.string.status_allcaps_on));
			mButtonRCState.setTextColor(Color.GREEN);
		}
	}
	
	private void setText() {
		RulesDbHelper dbHelper;
		int state = PrefHelper.getState(getMainActivity(), RulesEntry.RC_STATE);
		
		mTextViewCallNumber.setText("" + PrefHelper.getRepeatedCallQty(getMainActivity()));;
		mTextViewCallTime.setText("" + PrefHelper.getRepeatedCallMins(getMainActivity()));
		
		String fromMessage = "";
		switch(state) {
		case (Constants.URGENT_CALL_STATE_ON):
			fromMessage = getMainActivity().getString(R.string.rc_text_anyone);
			break;
		case (Constants.URGENT_CALL_STATE_WHITELIST):
			dbHelper = new RulesDbHelper(getMainActivity());
			fromMessage += getMainActivity().getString(R.string.rc_text_whitelist_before);
			fromMessage += dbHelper.getCount(RulesEntry.RC_STATE, RulesEntry.STATE_ON);
			fromMessage += getMainActivity().getString(R.string.rc_text_whitelist_after);
			dbHelper.close();
			break;
		case (Constants.URGENT_CALL_STATE_BLACKLIST):
			dbHelper = new RulesDbHelper(getMainActivity());
			fromMessage += getMainActivity().getString(R.string.rc_text_blacklist_before);
			fromMessage += dbHelper.getCount(RulesEntry.RC_STATE, RulesEntry.STATE_OFF);
			fromMessage += getMainActivity().getString(R.string.rc_text_blacklist_after);
			dbHelper.close();
			break;
		}
		
		mTextViewFrom.setText(fromMessage);
	}

}
