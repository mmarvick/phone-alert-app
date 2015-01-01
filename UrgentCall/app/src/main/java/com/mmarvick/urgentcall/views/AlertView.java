package com.mmarvick.urgentcall.views;

import java.util.ArrayList;
import java.util.List;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.Alert;
import com.mmarvick.urgentcall.data.DbContract;
import com.mmarvick.urgentcall.widgets.EditTextStringPrompt;
import com.mmarvick.urgentcall.widgets.FilterPrompt;
import com.mmarvick.urgentcall.widgets.OnOptionsChangedListener;
import com.mmarvick.urgentcall.widgets.OnStringValueUpdatedListener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

import butterknife.ButterKnife;
import butterknife.InjectView;

public abstract class AlertView extends RelativeLayout {
	protected Context mContext;
	protected Fragment mFragment;
	protected View mView;
	protected View mPreView;
	protected View mPostView;
	protected OnDeleteListener mOnDeleteListener;	
	protected Alert mAlert;
	protected ArrayList<View> expandable;
	
	protected RelativeLayoutBugHack layoutFilterBy;
	
	protected ToggleButton toggleButtonAlertOn;	
	
	protected ImageButton imageButtonFilterBy;
	protected ImageButton imageButtonVibrate;
	protected ImageButton imageButtonRing;
	protected ImageButton imageButtonTone;
	protected ImageButton imageButtonExpand;
	protected ImageButton imageButtonDelete;

    @InjectView(R.id.imageButtonShare)
    protected ImageButton imageButtonShare;
	
	protected TextView textViewAlertName;
	protected TextView textViewFilterBy;	
	protected TextView textViewTone;
	protected TextView textViewSettings;
	
	protected SeekBar seekBarVolume;
	
	protected boolean mExpanded;
	
	public AlertView(Context context, Fragment fragment) {
		super(context, null);
		mContext = context;
		mFragment = fragment;
		
		LayoutInflater inflater = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).cloneInContext(new ContextThemeWrapper(context, R.style.AppThemeLight));
		mView = inflater.inflate(R.layout.view_alert, this);
        ButterKnife.inject(this, mView);
        inflatePreView();
		inflatePostView();
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
	protected abstract View getLastImageButton();
	
	public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
		mOnDeleteListener = onDeleteListener;
	}	
	
	public void promptTitle() {
		EditTextStringPrompt titlePrompt = new EditTextStringPrompt(mContext, 0,
				mAlert.getTitle(), "Rename alert");
		
		titlePrompt.setOnStringValueUpdatedListener(new OnStringValueUpdatedListener() {
			
			@Override
			public void onStringValueUpdated(String value) {
				updateAlertTitle(value);
				
			}
		});	
		
		titlePrompt.show();	
	}
	
	public void updateAlertTitle(String value) {
		mAlert.setTitle(value);
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
		FilterPrompt filterPrompt = new FilterPrompt(mContext, mFragment, mAlert, "Filter by");
		
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
            if (allowed.size() == 0) {
                filterByText = "Nobody (allowed contacts were removed)";
            } else if (allowed.size() <= 3) {
				filterByText += getNames(allowed);
			} else {
				filterByText = allowed.size() + " Allowed Contacts";
			}
		} else {
			List<String> blocked = mAlert.getBlockedContactNames();
			filterByText = "Everyone but";
            if (blocked.size() == 0) {
                filterByText = "Everyone";
            } else if (blocked.size() <= 2) {
				filterByText += "\n" + getNames(blocked);
			} else {
				filterByText += "\n" + blocked.size() + " Blocked Contacts";
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
		Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);

		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, mAlert.getTone());
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Settings.System.DEFAULT_ALARM_ALERT_URI);	
		
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select an alert sound");
		
		mFragment.startActivityForResult(intent, this.getId());
	}
	
	public void updateAlertTone(Intent data) {
		Uri tone = (Uri) data.getExtras().get(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
		mAlert.setTone(tone);
		updateViewTone();
	}
	
	public void updateViewTone() {
		textViewTone.setText(mAlert.getToneName(mContext));
	}	
	
	public void promptDelete() {
		AlertDialog.Builder deleteAlertBuilder = new AlertDialog.Builder(mContext);
		deleteAlertBuilder
			.setMessage("Do you really want to delete this alert?")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					delete();
					
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					
				}
			})
			.create().show();
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
			imageButtonExpandParams.addRule(ALIGN_BOTTOM, layoutFilterBy.getId());
		}
		
		updateViewDelete();
		
	}	
	
	public void updateViewDelete() {
		if (!getResources().getBoolean(R.bool.paid_version)) {
			if (mExpanded) {
				imageButtonDelete.setVisibility(INVISIBLE);
			} else {
				imageButtonDelete.setVisibility(GONE);
			}
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
			names += contacts.get(i);
			if (i < contacts.size() - 1) {
				names += "\n";
			}
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
		textViewTone = (TextView) mView.findViewById(R.id.textViewTone);
		textViewSettings = (TextView) mView.findViewById(R.id.textViewSettings);
		seekBarVolume = (SeekBar) mView.findViewById(R.id.seekBarVolume);
		layoutFilterBy = (RelativeLayoutBugHack) mView.findViewById(R.id.relativeLayoutCallFrom);
	}
	
	public void setExpandable() {
		expandable = new ArrayList<View>();
		expandable.add(imageButtonVibrate);
		expandable.add(imageButtonRing);
		expandable.add(imageButtonTone);
		expandable.add(imageButtonDelete);
        expandable.add(imageButtonShare);
		expandable.add(seekBarVolume);
		expandable.add(textViewTone);
		expandable.add(textViewSettings);
	}
	
	public void initView() {
		updateViewTitle();
		updateViewOnState();
		updateViewFilterBy();
		updateViewVibrate();
		updateViewRingAndVolume();
		updateViewTone();
		updateViewExpand();
		updateViewDelete();
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
				updateViewRingAndVolume();
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
				promptTitle();
				
			}
		});
	}
	
	public interface OnDeleteListener {
		
		public void onDelete(View v);
		
	}	
}
