package com.mmarvick.urgentcall;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class EditTextPrompt {
	public EditTextPrompt(final Context context, final int min, final int max, final String name, final String title) {
		LayoutInflater li = LayoutInflater.from(context);
		View promptView = li.inflate(R.layout.edit_text_prompt,  null);
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setView(promptView);
		
		final EditText userInput = (EditText) promptView.findViewById(R.id.edit_text_prompt_editText);
		String setText = "" + PrefHelper.getCallValue(context, name);
		userInput.setText(setText);
		userInput.setSelection(setText.length());
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
		
		alertDialogBuilder
			.setTitle(title)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					int value;
					
					try {
						value = Integer.parseInt(userInput.getText().toString());
						if (value >= min && value <= max) {
							PrefHelper.setCallValue(context, name, value);
						}						
					} catch (NumberFormatException e) {
						
					}	
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					
				}
			});
		
		alertDialogBuilder.create().show();
	}
}
