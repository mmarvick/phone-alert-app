package marvick.phonealert;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingActivity extends Activity {

	public final String SETTING_CALL_QTY = "callQty";
	public final String SETTING_CALL_TIME = "callTime";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		final Editor editor = prefs.edit();
		EditText callQty = (EditText) findViewById(R.id.call_qty_value);
		EditText callTime = (EditText) findViewById(R.id.call_time_value);
		Button saveButton = (Button) findViewById(R.id.save_button);
		callQty.setText("" + prefs.getInt(SETTING_CALL_QTY, 3));
		callTime.setText("" + prefs.getInt(SETTING_CALL_TIME, 15));
		
		callQty.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}

			@Override
			public void afterTextChanged(Editable s) {
				try {
					editor.putInt(SETTING_CALL_QTY, Integer.parseInt(s.toString()));
					editor.commit();
				} catch (NumberFormatException e) {	}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		});
		
		callTime.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}

			@Override
			public void afterTextChanged(Editable s) {
				try {
					editor.putInt(SETTING_CALL_TIME, Integer.parseInt(s.toString()));
					editor.commit();
				} catch (NumberFormatException e) {	}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		});
		
		saveButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
