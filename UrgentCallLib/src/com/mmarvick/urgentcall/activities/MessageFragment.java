package com.mmarvick.urgentcall.activities;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.PrefHelper;
import com.mmarvick.urgentcall.data.RulesDbContract.RulesEntry;
import com.mmarvick.urgentcall.data.RulesDbHelper;
import com.mmarvick.urgentcall.widgets.EditTextStringPrompt;
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
	private TextView mTextViewMsgHeading;
	private TextView mTextViewMsgKey;
	private TextView mTextViewTrigger;
	private TextView mTextViewFrom;
	private TextView mTextViewTurnedOff;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_msg, container, false);
		mButtonMsgState = (Button) view.findViewById(R.id.button_msg_state);
		mTextViewMsgHeading = (TextView) view.findViewById(R.id.textView_msg_heading);
		mTextViewMsgKey = (TextView) view.findViewById(R.id.textView_msg_key);
		mTextViewTrigger = (TextView) view.findViewById(R.id.textView_msg_trigger);
		mTextViewFrom = (TextView) view.findViewById(R.id.textView_msg_from);
		mTextViewTurnedOff = (TextView) view.findViewById(R.id.textView_msg_turned_off);

		mButtonMsgState.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (PrefHelper.getState(getMainActivity(), RulesEntry.MSG_STATE) == Constants.URGENT_CALL_STATE_OFF) {
					int backupState = PrefHelper.getBackupState(getMainActivity(), RulesEntry.MSG_STATE);
					PrefHelper.setState(getMainActivity(), RulesEntry.MSG_STATE, backupState);
				} else {
					PrefHelper.saveBackupState(getMainActivity(), RulesEntry.MSG_STATE);
					PrefHelper.setState(getMainActivity(), RulesEntry.MSG_STATE, Constants.URGENT_CALL_STATE_OFF);
				}
				
				getMainActivity().updateSettings();
				
			}
		});
		
		mTextViewMsgKey.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new EditTextStringPrompt(getMainActivity(), Constants.MSG_MESSAGE_MIN,
						Constants.MSG_MESSAGE, Constants.MSG_MESSAGE_DEFAULT, Constants.MSG_MESSAGE_TITLE);
				
			}
			
		});
		
		mTextViewFrom.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				StateListsPrompt msgStatePrompt = new StateListsPrompt(getMainActivity(), RulesEntry.MSG_STATE,
						getMainActivity().getString(R.string.state_change_dialog_title_msg));
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
		setText();
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
	
	private void setText() {
		RulesDbHelper dbHelper;
		int state = PrefHelper.getState(getMainActivity(), RulesEntry.MSG_STATE);
		
		showTextViews();
		
		mTextViewMsgKey.setText(PrefHelper.getMessageToken(getMainActivity()));
		
		String fromMessage = "";
		switch(state) {
		case (Constants.URGENT_CALL_STATE_ON):
			fromMessage = getMainActivity().getString(R.string.message_text_anyone);
			break;
		case (Constants.URGENT_CALL_STATE_WHITELIST):
			dbHelper = new RulesDbHelper(getMainActivity());
			fromMessage += getMainActivity().getString(R.string.message_text_whitelist_before);
			fromMessage += dbHelper.getCount(RulesEntry.MSG_STATE, RulesEntry.STATE_ON);
			fromMessage += getMainActivity().getString(R.string.message_text_whitelist_after);
			dbHelper.close();
			break;
		case (Constants.URGENT_CALL_STATE_BLACKLIST):
			dbHelper = new RulesDbHelper(getMainActivity());
			fromMessage += getMainActivity().getString(R.string.message_text_blacklist_before);
			fromMessage += dbHelper.getCount(RulesEntry.MSG_STATE, RulesEntry.STATE_OFF);
			fromMessage += getMainActivity().getString(R.string.message_text_blacklist_after);
			dbHelper.close();
			break;
		}	
		
		mTextViewFrom.setText(fromMessage);
	}
	
	private void showTextViews() {
		int state = PrefHelper.getState(getMainActivity(), RulesEntry.MSG_STATE);
		if (state == Constants.URGENT_CALL_STATE_ON || state == Constants.URGENT_CALL_STATE_WHITELIST
				|| state == Constants.URGENT_CALL_STATE_BLACKLIST) {
			mTextViewMsgHeading.setVisibility(View.VISIBLE);
			mTextViewMsgKey.setVisibility(View.VISIBLE);
			mTextViewTrigger.setVisibility(View.VISIBLE);
			mTextViewFrom.setVisibility(View.VISIBLE);
			mTextViewTurnedOff.setVisibility(View.INVISIBLE);
		} else {
			mTextViewMsgHeading.setVisibility(View.INVISIBLE);
			mTextViewMsgKey.setVisibility(View.INVISIBLE);
			mTextViewTrigger.setVisibility(View.INVISIBLE);
			mTextViewFrom.setVisibility(View.INVISIBLE);
			mTextViewTurnedOff.setVisibility(View.VISIBLE);
		}
	}
}