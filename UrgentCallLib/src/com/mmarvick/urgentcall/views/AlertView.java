package com.mmarvick.urgentcall.views;

import java.util.ArrayList;
import java.util.List;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.Alert;
import com.mmarvick.urgentcall.data.DbContract;
import com.mmarvick.urgentcall.widgets.FilterPrompt;
import com.mmarvick.urgentcall.widgets.OnOptionsChangedListener;

import android.content.Context;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public abstract class AlertView extends RelativeLayout {
	protected Context mContext;
	protected View mView;
	protected View mPreView;
	protected View mPostView;
	protected OnDeleteListener mOnDeleteListener;	
	protected Alert mAlert;
	protected ArrayList<View> expandable;
	
	protected ToggleButton toggleButtonAlertOn;	
	
	protected ImageButton imageButtonFilterBy;
	protected ImageButton imageButtonVibrate;
	protected ImageButton imageButtonRing;
	protected ImageButton imageButtonTone;
	protected ImageButton imageButtonExpand;
	protected ImageButton imageButtonDelete;	
	
	protected TextView textViewAlertName;
	protected TextView textViewFilterBy;	
	
	protected SeekBar seekBarVolume;
	
	protected boolean mExpanded;	
	
	public AlertView(Context context) {
		this(context, null);
	}
	
	public AlertView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = inflater.inflate(R.layout.view_alert, this);
		inflatePreView();
		inflatePostView();
		
		mContext = context;
	}
	
	public void setAlert(Alert alert) {
		mAlert = alert;
		mExpanded = false;
		findViews();
		setExpandable();
		initCallbacks();
		initView();
	}	
	
	protected abstract void inflatePreView();
	protected abstract void inflatePostView();
	
	public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
		mOnDeleteListener = onDeleteListener;
	}	
	
	public void promptTitle() {
		//TODO
	}
	
	public void updateAlertTitle() {
		//TODO
		updateViewTitle();
	}
	
	public void updateViewTitle() {
		textViewAlertName.setText(mAlert.getTitle());
	}

	public void updateAlertOnState(boolean onState) {
		mAlert.setOnState(onState);
		updateViewOnState();
	}
	
	public void updateViewOnState() {
		toggleButtonAlertOn.setChecked(mAlert.getOnState());
	}
	
	public void promptFilterBy() {
		FilterPrompt filterPrompt = new FilterPrompt(mContext, mAlert, "Filter by");
		
		filterPrompt.setOnOptionsChangedListener(new OnOptionsChangedListener() {
			
			@Override
			public void onOptionsChanged() {
				updateAlertFilterBy();
				
			}
		});
		
		filterPrompt.show();
	}
	
	public void updateAlertFilterBy() {
		updateViewFilterBy();
	}
	
	public void updateViewFilterBy() {
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
	
	private void toggleAlertVibrate() {
		mAlert.setVibrate(!mAlert.getVibrate());
		
		Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
		if (mAlert.getVibrate()) {
			vibrator.vibrate(400);
		} else {
			vibrator.vibrate(50);
		}
		updateViewVibrate();
	}
	
	private void updateViewVibrate() {
		if (mAlert.getVibrate()) {
			imageButtonVibrate.setImageResource(R.drawable.ic_action_vibrate);
		} else {
			imageButtonVibrate.setImageResource(R.drawable.ic_action_vibrate_off);
		}
	}
	
	private void toggleAlertRing() {
		mAlert.setRing(!mAlert.getRing());
		updateViewRingAndVolume();
	}
	
	private void updateAlertVolume(int progress) {
		mAlert.setVolume(progress);
	}
	
	private void updateViewRingAndVolume() {
		seekBarVolume.setMax(Constants.ALERT_VOLUME_MAX);
		if (mAlert.getRing()) {
			imageButtonRing.setImageResource(R.drawable.ic_action_volume_on);
			seekBarVolume.setEnabled(true);
			seekBarVolume.setProgress(mAlert.getVolume());
		} else {
			imageButtonRing.setImageResource(R.drawable.ic_action_volume_muted);
			seekBarVolume.setEnabled(false);
			seekBarVolume.setProgress(0);			
		}		
		
	}	
	
	private void promptTone() {
		// TODO
	}
	
	private void updateAlertTone() {
		// TODO
	}
	
	private void updateViewTone() {
		// TODO
	}	
	
	public void promptDelete() {
		delete();
	}
	
	private void toggleExpand() {
		mExpanded = !mExpanded;
		updateViewExpand();
	}
	
	private void updateViewExpand() {
		
		for (View v : expandable) {
			if (mExpanded) {
				v.setVisibility(VISIBLE);
			} else {
				v.setVisibility(GONE);
			}
		}
		
		RelativeLayout.LayoutParams imageButtonExpandParams = (RelativeLayout.LayoutParams) imageButtonExpand.getLayoutParams();
		
		if (mExpanded) {
			imageButtonExpandParams.addRule(ALIGN_BOTTOM, R.id.imageButtonDelete);
		} else {
			imageButtonExpandParams.addRule(ALIGN_BOTTOM, R.id.imageButtonCallFrom);
		}
	}	
	
	
	public void delete() {
		mAlert.delete();
		
		if (mOnDeleteListener != null) {
			mOnDeleteListener.onDelete(this);
		}
	}
	
	private String getNames(List<String> contacts) {
		String names = "";
		for (int i = 0; i < contacts.size(); i++) {
			names += "\n" + contacts.get(i);
		}
		return names;
	}	
	
	
	public void findViews() {
		toggleButtonAlertOn = (ToggleButton) mView.findViewById(R.id.toggleButtonAlertOn);
		imageButtonFilterBy = (ImageButton) mView.findViewById(R.id.imageButtonCallFrom);
		imageButtonVibrate = (ImageButton) mView.findViewById(R.id.imageButtonVibrate);
		imageButtonRing = (ImageButton) mView.findViewById(R.id.imageButtonRing);
		imageButtonTone = (ImageButton) mView.findViewById(R.id.imageButtonTone);
		imageButtonExpand = (ImageButton) mView.findViewById(R.id.imageButtonExpand);
		imageButtonDelete = (ImageButton) mView.findViewById(R.id.imageButtonDelete);
		textViewAlertName = (TextView) mView.findViewById(R.id.textViewAlertName);
		textViewFilterBy = (TextView) mView.findViewById(R.id.textViewCallFrom);
		seekBarVolume = (SeekBar) mView.findViewById(R.id.seekBarVolume);
	}
	
	public void setExpandable() {
		expandable = new ArrayList<View>();
		expandable.add(imageButtonVibrate);
		expandable.add(imageButtonRing);
		expandable.add(imageButtonTone);
		expandable.add(imageButtonDelete);
		expandable.add(seekBarVolume);
	}
	
	public void initView() {
		updateViewTitle();
		updateViewOnState();
		updateViewFilterBy();
		updateViewVibrate();
		updateViewRingAndVolume();
		updateViewExpand();
	}
	
	public void initCallbacks() {
		toggleButtonAlertOn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				updateAlertOnState(isChecked);
				
			}
		});
		
		
		imageButtonFilterBy.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				promptFilterBy();
				
			}
		});
		
		imageButtonVibrate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				toggleAlertVibrate();
			}
		});
		
		imageButtonRing.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				toggleAlertRing();
				
			}
		});
		
		
		imageButtonTone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				promptTone();
				
			}
		});
		
		
		imageButtonExpand.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				toggleExpand();
				
			}
		});	

		imageButtonDelete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				promptDelete();
				
			}
		});
		
		seekBarVolume.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// DO NOTHING
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// DO NOTHING
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					updateAlertVolume(progress);
				}
			}
		});
		
		textViewAlertName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO
				
			}
		});
	}
	
	
	
	
	public interface OnDeleteListener {
		
		public void onDelete(View v);
		
	}	
}
