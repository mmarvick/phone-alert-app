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
import android.widget.EditText;

public class EditTextIntPrompt {
	private int min;
	private int max;
	private String title;
	private Context context;
	
	private AlertDialog.Builder alertDialogBuilder;
	private OnOptionsChangedListener mOnOptionsChangedListener;
	private EditText userInput;
	
	public EditTextIntPrompt(final Context context, final int min, final int max, final String name, final int def, final String title) {
		this.min = min;
		this.max = max;
		this.title = title;
		this.context = context;
		
		LayoutInflater li = LayoutInflater.from(context);
		View promptView = li.inflate(R.layout.dialog_edit_text,  null);
		
		alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setView(promptView);
		
		userInput = (EditText) promptView.findViewById(R.id.edit_text_prompt_editText);
		userInput.setInputType(InputType.TYPE_CLASS_NUMBER);
		String setText = "" + PrefHelper.getRepeatedCallValue(context, name, def);
		
		//Set initial EditText text and move cursor to the end
		userInput.setText(setText);
		userInput.setSelection(setText.length());
		
		//Set max length of the EditText
		userInput.setFilters(new InputFilter[] {new InputFilter.LengthFilter((int) Math.floor(Math.log10(max) + 1))});

		
		alertDialogBuilder
			.setTitle(title)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					int value;
					
					try {
						value = Integer.parseInt(userInput.getText().toString());
						if (value < min) {
							value = min;
							alertRange(value);
						} else if (value > max) {
							value = max;
							alertRange(value);
						}
						PrefHelper.setRepeatedCallValue(context, name, value);					
					} catch (NumberFormatException e) {
						
					}

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
		
		//Show the keyboard when opened
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		
		dialog.show();
		
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(userInput, InputMethodManager.SHOW_IMPLICIT);
	}
	
	private void alertRange(int value) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		
		alertDialogBuilder
			.setTitle("Out of range")
			.setMessage(title + " must be between " + min + " and " + max + ". The value is being set to " + value + ".")
			.setCancelable(false)
			.setPositiveButton("OK", null);
		
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	
	public void setOnOptionsChangedListener(OnOptionsChangedListener listener) {
		mOnOptionsChangedListener = listener;
	}	

}
