package com.mmarvick.urgentcall.views;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.AlertCall;
import com.mmarvick.urgentcall.data.AlertText;
import com.mmarvick.urgentcall.widgets.EditTextIntPrompt;
import com.mmarvick.urgentcall.widgets.OnIntValueUpdatedListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.TextView;

public class TextAlertView extends AlertView {
	
	private ImageButton imageButtonMessagePhrase;
	private ImageButton imageButtonMessageAlertDuration;
	
	private TextView textViewMessagePhrase;
	private TextView textViewMessageAlertDuration;

	public TextAlertView(Context context) {
		super(context, null);
	}
	
	public TextAlertView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}	

	@Override
	protected void inflatePreView() {
		ViewStub stub = (ViewStub) mView.findViewById(R.id.viewStubPre);
		stub.setLayoutResource(R.layout.view_text_alert_req);
		mPreView = stub.inflate();
	}

	@Override
	protected void inflatePostView() {
		ViewStub stub = (ViewStub) mView.findViewById(R.id.viewStubPost);
		stub.setLayoutResource(R.layout.view_text_alert_post);
		mPostView = stub.inflate();
	}
	
	private void promptMessagePhrase() {
		
	}
	
	private void updateAlertMessagePhrase() {
		
		updateViewMessagePhrase();
	}
	
	private void updateViewMessagePhrase() {
		
	}
	
	private void promptDuration() {
		EditTextIntPrompt durationPrompt = new EditTextIntPrompt(mContext, 1, 60,
				getAlertText().getAlertDuration(), "Seconds alert will last");
		
		durationPrompt.setOnIntValueUpdatedListener(new OnIntValueUpdatedListener() {
			
			@Override
			public void onIntValueUpdated(int value) {
				updateAlertDuration(value);
				
			}
		});	
		
		durationPrompt.show();		
	}
	
	private void updateAlertDuration(int duration) {
		getAlertText().setAlertDuration(duration);
		
		updateViewDuration();
	}
	
	private void updateViewDuration() {
		int duration = getAlertText().getAlertDuration();
		String durationText = duration + " Second";
		if (duration > 1) {
			durationText += "s"; 
		}
		
		textViewMessageAlertDuration.setText(durationText);
	}
	
	@Override
	public void findViews() {
		super.findViews();
		
		imageButtonMessagePhrase = (ImageButton) mPreView.findViewById(R.id.imageButtonMessagePhrase);
		imageButtonMessageAlertDuration = (ImageButton) mPostView.findViewById(R.id.imageButtonMessageDuration);
		
		textViewMessagePhrase = (TextView) mPreView.findViewById(R.id.textViewMessagePhrase);
		textViewMessageAlertDuration = (TextView) mPostView.findViewById(R.id.textViewMessageDuration);
		
	}
	
	@Override
	public void setExpandable() {
		super.setExpandable();
		expandable.add(imageButtonMessageAlertDuration);
		expandable.add(textViewMessageAlertDuration);
	}
	
	@Override
	public void initView() {
		super.initView();
		updateViewMessagePhrase();
		updateViewDuration();
	}
	
	@Override
	public void initCallbacks() {
		super.initCallbacks();
		
		imageButtonMessagePhrase.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				promptMessagePhrase();
				
			}
		});
		
		imageButtonMessageAlertDuration.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				promptDuration();
				
			}
		});
	}
	
	protected AlertText getAlertText() {
		return (AlertText) mAlert;
	}	

}
