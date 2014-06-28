package com.mmarvick.urgentcall.widgets;

import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.PrefHelper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView.FindListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;

public class SliderPrompt {
	private String title;
	private Context context;
	private SeekBar seekBar;
	private ImageView icon;
	private int max;
	private int iconResource;
	
	private AlertDialog.Builder alertDialogBuilder;
	private OnOptionsChangedListener mOnOptionsChangedListener;
	
	public SliderPrompt(final Context context, final int max, final String name, final int def, final String title, int iconResource) {
		this.title = title;
		this.context = context;
		this.max = max;
		
		LayoutInflater li = LayoutInflater.from(context);
		View promptView = li.inflate(R.layout.dialog_slider,  null);
		
		seekBar = (SeekBar) promptView.findViewById(R.id.slider_prompt_seekBar);
		icon = (ImageView) promptView.findViewById(R.id.slider_prompt_icon);
		
		seekBar.setMax(max);
		seekBar.setProgress(PrefHelper.getIntValue(context, name, def));
		
		icon.setImageResource(iconResource);
		
		alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setView(promptView);

		
		alertDialogBuilder
			.setTitle(title)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					PrefHelper.setIntValue(context, name, seekBar.getProgress());
					if (mOnOptionsChangedListener != null) mOnOptionsChangedListener.onOptionsChanged();
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});

	}
	
	public void show() {
		AlertDialog dialog = alertDialogBuilder.create();
		
		dialog.show();
	}
	
	public void setOnOptionsChangedListener(OnOptionsChangedListener listener) {
		mOnOptionsChangedListener = listener;
	}	

}
