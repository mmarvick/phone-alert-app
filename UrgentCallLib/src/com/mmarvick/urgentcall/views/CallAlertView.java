package com.mmarvick.urgentcall.views;


import java.util.List;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.AlertCall;
import com.mmarvick.urgentcall.data.DbContract;
import com.mmarvick.urgentcall.data.DbContract.RuleEntry;
import com.mmarvick.urgentcall.widgets.EditTextIntPrompt;
import com.mmarvick.urgentcall.widgets.FilterPrompt;
import com.mmarvick.urgentcall.widgets.OldEditTextIntPrompt;
import com.mmarvick.urgentcall.widgets.OnIntValueUpdatedListener;
import com.mmarvick.urgentcall.widgets.OnOptionsChangedListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class CallAlertView extends RelativeLayout {
	Context mContext;
	View mView;
	
	AlertCall mAlert;
	
	ToggleButton toggleButtonAlertOn;
	
	ImageButton imageButtonCallQty;
	ImageButton imageButtonCallTime;
	ImageButton imageButtonFilterBy;
	
	TextView textViewAlertName;
	TextView textViewCallQty;
	TextView textViewCallTime;
	TextView textViewCallTimeWithin;
	TextView textViewFilterBy;
	
	public CallAlertView(Context context) {
		this(context, null);
	}
	
	public CallAlertView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = inflater.inflate(R.layout.view_call_alert, this);
		mContext = context;
	}
	
	public void addAlert(AlertCall alert) {
		mAlert = alert;
		findViews();
		initCallbacks();
		initValues();
	}

	public void updateOnState(boolean onState) {
		mAlert.setOnState(onState);
		setOnState();
	}
	
	public void setOnState() {
		toggleButtonAlertOn.setChecked(mAlert.getOnState());
	}
	
	public void promptCallQty() {
		EditTextIntPrompt callNumberPrompt = new EditTextIntPrompt(mContext, Constants.CALL_QTY_MIN, Constants.CALL_QTY_MAX,
				mAlert.getCallQty(), Constants.CALL_QTY_TITLE);
		
		callNumberPrompt.setOnIntValueUpdatedListener(new OnIntValueUpdatedListener() {
			
			@Override
			public void onIntValueUpdated(int value) {
				updateCallQty(value);
				
			}
		});	
		
		callNumberPrompt.show();
	}
	
	public void updateCallQty(int callQty) {
		mAlert.setCallQty(callQty);
		setCallQty();
	}
	
	public void setCallQty() {
		int callQty = mAlert.getCallQty();
		
		if (callQty == 1) {
			textViewCallQty.setText("A Single Call");
			textViewCallTime.setVisibility(GONE);
			imageButtonCallTime.setVisibility(GONE);
			textViewCallTimeWithin.setVisibility(GONE);
		} else {
			textViewCallQty.setText(callQty + " Calls");
			textViewCallTime.setVisibility(VISIBLE);
			imageButtonCallTime.setVisibility(VISIBLE);
			textViewCallTimeWithin.setVisibility(VISIBLE);
		}
	}
	
	public void promptCallTime() {
		EditTextIntPrompt callTimePrompt = new EditTextIntPrompt(mContext, Constants.CALL_MIN_MIN, Constants.CALL_MIN_MAX,
				mAlert.getCallTime(), Constants.CALL_MIN_TITLE);
		
		callTimePrompt.setOnIntValueUpdatedListener(new OnIntValueUpdatedListener() {
			
			@Override
			public void onIntValueUpdated(int value) {
				updateCallTime(value);
				
			}
		});	
		
		callTimePrompt.show();		
	}
	
	public void updateCallTime(int callTime) {
		mAlert.setCallTime(callTime);
		setCallTime();
	}
	
	public void setCallTime() {
		int callTime = mAlert.getCallTime();
		String callTimeText = callTime + " Minute";
		if (callTime != 1) {
			callTimeText += "s";
		}
		textViewCallTime.setText(callTimeText);
	}
	
	public void promptFilterBy() {
		FilterPrompt filterPrompt = new FilterPrompt(mContext, mAlert, "Filter by");
		
		filterPrompt.setOnOptionsChangedListener(new OnOptionsChangedListener() {
			
			@Override
			public void onOptionsChanged() {
				updateFilterBy();
				
			}
		});
		
		filterPrompt.show();
	}
	
	public void updateFilterBy() {
		setFilterBy();
	}
	
	public void setFilterBy() {
		int filterBy = mAlert.getFilterBy();
		String filterByText = "";
		
		
		if (filterBy == DbContract.ENTRY_FILTER_BY_EVERYONE) {
			filterByText = "Everyone";
		} else if (filterBy == DbContract.ENTRY_FILTER_BY_ALLOWED_ONLY) {
			List<String> allowed = mAlert.getAllowedContactNames();
			if (allowed.size() <= 3) {
				filterByText += getNames(allowed);
			} else {
				filterByText = allowed.size() + " Allowed Contacts";
			}
		} else {
			List<String> blocked = mAlert.getBlockedContactNames();
			filterByText = "Everyone Except";
			if (blocked.size() <= 2) {
				filterByText += getNames(blocked);
			} else {
				filterByText = "\n" + blocked.size() + " Blocked Contacts";
			}
		}
		
		textViewFilterBy.setText(filterByText);
	}
	
	private String getNames(List<String> contacts) {
		String names = "";
		for (int i = 0; i < contacts.size(); i++) {
			names += "\n" + contacts.get(i);
		}
		return names;
	}
	
	public void rename() {
		Toast.makeText(CallAlertView.this.getContext(), "Hello!", Toast.LENGTH_LONG).show();
	}
	
	public void findViews() {
		toggleButtonAlertOn = (ToggleButton) mView.findViewById(R.id.toggleButtonAlertOn);
		imageButtonCallQty = (ImageButton) mView.findViewById(R.id.imageButtonCallNum);
		imageButtonCallTime = (ImageButton) mView.findViewById(R.id.imageButtonCallTime);
		imageButtonFilterBy = (ImageButton) mView.findViewById(R.id.imageButtonCallFrom);
		textViewAlertName = (TextView) mView.findViewById(R.id.textViewAlertName);
		textViewCallQty = (TextView) mView.findViewById(R.id.textViewCallNum);
		textViewCallTime = (TextView) mView.findViewById(R.id.textViewCallTime); 
		textViewCallTimeWithin = (TextView) mView.findViewById(R.id.textViewCallTimeWithin);
		textViewFilterBy = (TextView) mView.findViewById(R.id.textViewCallFrom);
	}
	
	public void initValues() {
		setOnState();
		setCallQty();
		setCallTime();
		setFilterBy();
	}
	
	public void initCallbacks() {
		toggleButtonAlertOn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				updateOnState(isChecked);
				
			}
		});
		
		imageButtonCallQty.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				promptCallQty();
				
			}
		});
		
		imageButtonCallTime.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				promptCallTime();
				
			}
		});	
		
		imageButtonFilterBy.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				promptFilterBy();
				
			}
		});
		
		textViewAlertName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				rename();
				
			}
		});
	}

	
}

