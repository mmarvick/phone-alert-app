package com.mmarvick.urgentcall.views;


import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.AlertCall;
import com.mmarvick.urgentcall.widgets.EditTextIntPrompt;
import com.mmarvick.urgentcall.widgets.OnIntValueUpdatedListener;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CallAlertView extends AlertView {
	
	private ImageButton imageButtonCallQty;
	private ImageButton imageButtonCallTime;
	
	private TextView textViewCallQty;
	private TextView textViewCallTime;
	private TextView textViewCallTimeWithin;
	
	public CallAlertView(Context context, Fragment fragment) {
		super(context, fragment);
	}
	
	public void promptCallQty() {
		EditTextIntPrompt callNumberPrompt = new EditTextIntPrompt(mContext, Constants.CALL_QTY_MIN, Constants.CALL_QTY_MAX,
				getAlertCall().getCallQty(), Constants.CALL_QTY_TITLE);
		
		callNumberPrompt.setOnIntValueUpdatedListener(new OnIntValueUpdatedListener() {
			
			@Override
			public void onIntValueUpdated(int value) {
				updateAlertCallQty(value);
				
			}
		});	
		
		callNumberPrompt.show();
	}
	
	public void updateAlertCallQty(int callQty) {
		getAlertCall().setCallQty(callQty);
		updateViewCallQty();
	}
	
	public void updateViewCallQty() {
		int callQty = getAlertCall().getCallQty();
		
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
				getAlertCall().getCallTime(), Constants.CALL_MIN_TITLE);
		
		callTimePrompt.setOnIntValueUpdatedListener(new OnIntValueUpdatedListener() {
			
			@Override
			public void onIntValueUpdated(int value) {
				updateAlertCallTime(value);
				
			}
		});	
		
		callTimePrompt.show();		
	}
	
	public void updateAlertCallTime(int callTime) {
		getAlertCall().setCallTime(callTime);
		updateViewCallTime();
	}
	
	public void updateViewCallTime() {
		int callTime = getAlertCall().getCallTime();
		String callTimeText = callTime + " Minute";
		if (callTime != 1) {
			callTimeText += "s";
		}
		textViewCallTime.setText(callTimeText);
	}
	

	protected void inflatePreView() {
		ViewStub stub = (ViewStub) mView.findViewById(R.id.viewStubPre);
		stub.setLayoutResource(R.layout.view_call_alert_req);
		mPreView = stub.inflate();
	}
	
	protected void inflatePostView() {
		// DO NOTHING
	}
	
	protected View getLastImageButton() {
		return imageButtonTone;
	}
	
	@Override
	public void findViews() {
		super.findViews();
		
		imageButtonCallQty = (ImageButton) mPreView.findViewById(R.id.imageButtonCallNum);
		imageButtonCallTime = (ImageButton) mPreView.findViewById(R.id.imageButtonCallTime);

		textViewCallQty = (TextView) mPreView.findViewById(R.id.textViewCallNum);
		textViewCallTime = (TextView) mPreView.findViewById(R.id.textViewCallTime); 
		textViewCallTimeWithin = (TextView) mPreView.findViewById(R.id.textViewCallTimeWithin);
	}
	
	@Override
	public void initView() {
		super.initView();
		updateViewCallQty();
		updateViewCallTime();
		
	}
	
	@Override
	public void initCallbacks() {
		super.initCallbacks();
		
		toggleButtonAlertOn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				updateAlertOnState(isChecked);
				
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

	}
	
	protected AlertCall getAlertCall() {
		return (AlertCall) mAlert;
	}

}

