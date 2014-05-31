package com.mmarvick.urgentcall.widgets;

import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.PrefHelper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class EditTextStringPrompt {
	private String title;
	private Context context;
	private int minLength;
	
	public EditTextStringPrompt(final Context context, final int minLength, final String name, final String def, final String title) {
		this.minLength = minLength;
		this.title = title;
		this.context = context;
		
		LayoutInflater li = LayoutInflater.from(context);
		View promptView = li.inflate(R.layout.dialog_edit_text,  null);
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setView(promptView);
		
		final EditText userInput = (EditText) promptView.findViewById(R.id.edit_text_prompt_editText);
		String setText = PrefHelper.getMessageToken(context);
		
		//Set initial EditText text and move cursor to the end
		userInput.setText(setText);
		userInput.setSelection(setText.length());
		

		
		alertDialogBuilder
			.setTitle(title)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String value = userInput.getText().toString();
					if (value.length() < minLength) {
						alertLength();
					} else {
						PrefHelper.setMessageToken(context, value);
					}

				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
		
		AlertDialog dialog = alertDialogBuilder.create();
		
		//Show the keyboard when opened
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		
		dialog.show();
		
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(userInput, InputMethodManager.SHOW_IMPLICIT);

	}
	
	private void alertLength() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		
		alertDialogBuilder
			.setTitle("Too short")
			.setMessage(title + " must be at least " + minLength + " characters long.")
			.setCancelable(false)
			.setPositiveButton("OK", null);
		
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

}
