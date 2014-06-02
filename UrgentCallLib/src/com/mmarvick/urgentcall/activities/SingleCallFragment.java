package com.mmarvick.urgentcall.activities;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.PrefHelper;
import com.mmarvick.urgentcall.data.RulesDbHelper;
import com.mmarvick.urgentcall.data.RulesDbContract.RulesEntry;
import com.mmarvick.urgentcall.widgets.UpgradeDialog;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SingleCallFragment extends TabFragment {
	private Button mButtonSCState;
	private TextView mTextViewIntro;
	private TextView mTextViewTrigger;
	private TextView mTextViewFrom;
	private TextView mTextViewTurnedOff;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_call_single, container, false);
		mButtonSCState = (Button) view.findViewById(R.id.button_sc_state);
		mTextViewIntro = (TextView) view.findViewById(R.id.textView_sc_intro);
		mTextViewTrigger = (TextView) view.findViewById(R.id.textView_sc_trigger);
		mTextViewFrom = (TextView) view.findViewById(R.id.textView_sc_from);
		mTextViewTurnedOff = (TextView) view.findViewById(R.id.textView_sc_turned_off);
		
		mButtonSCState.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				if (getResources().getBoolean(R.bool.paid_version)) {
					
					if (PrefHelper.getState(getMainActivity(), RulesEntry.SC_STATE) == Constants.URGENT_CALL_STATE_WHITELIST) {
						PrefHelper.setState(getMainActivity(), RulesEntry.SC_STATE, Constants.URGENT_CALL_STATE_OFF);
					} else {
						PrefHelper.setState(getMainActivity(), RulesEntry.SC_STATE, Constants.URGENT_CALL_STATE_WHITELIST);
					}
					
					getMainActivity().updateSettings();
					
				} else {
					UpgradeDialog.upgradeDialog(getMainActivity(), getString(R.string.upgrade_body_sc));
				}
			}
		});	
		
		mTextViewFrom.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
		        Intent whitelistIntent = new Intent(getMainActivity(), ContactListActivity.class);
		        whitelistIntent.putExtra(Constants.ALERT_TYPE, RulesEntry.SC_STATE);
		        whitelistIntent.putExtra(Constants.USER_STATE, RulesEntry.STATE_ON);
				getMainActivity().startActivity(whitelistIntent);
				
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
		if (PrefHelper.getState(getMainActivity().getApplicationContext(), RulesEntry.SC_STATE) == Constants.URGENT_CALL_STATE_OFF) {
			mButtonSCState.setText(getMainActivity().getString(R.string.status_allcaps_off));
			mButtonSCState.setTextColor(Color.RED);
		} else {
			mButtonSCState.setText(getMainActivity().getString(R.string.status_allcaps_on));
			mButtonSCState.setTextColor(Color.GREEN);
		}
	}
	
	private void setText() {
		RulesDbHelper dbHelper;
		int state = PrefHelper.getState(getMainActivity(), RulesEntry.SC_STATE);
		
		String fromMessage = "";
		switch(state) {
		case (Constants.URGENT_CALL_STATE_WHITELIST):
			dbHelper = new RulesDbHelper(getMainActivity());
			fromMessage += getMainActivity().getString(R.string.sc_text_whitelist_before);
			fromMessage += dbHelper.getCount(RulesEntry.SC_STATE, RulesEntry.STATE_ON);
			fromMessage += getMainActivity().getString(R.string.sc_text_whitelist_after);
			dbHelper.close();
			
			mTextViewIntro.setVisibility(View.VISIBLE);
			mTextViewTrigger.setVisibility(View.VISIBLE);
			mTextViewFrom.setVisibility(View.VISIBLE);
			mTextViewTurnedOff.setVisibility(View.INVISIBLE);
			mTextViewFrom.setText(fromMessage);
			break;
		
		case (Constants.URGENT_CALL_STATE_OFF):
			mTextViewIntro.setVisibility(View.INVISIBLE);
			mTextViewTrigger.setVisibility(View.INVISIBLE);
			mTextViewFrom.setVisibility(View.INVISIBLE);
			mTextViewTurnedOff.setVisibility(View.VISIBLE);
			break;
			
		}
		
		
	}

}
