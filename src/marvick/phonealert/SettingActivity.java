package marvick.phonealert;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingActivity extends Activity {

	public final String SETTING_CALL_QTY = "callQty";
	public final String SETTING_CALL_TIME = "callTime";
	private EditText mCallQty;
	private EditText mCallTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		loadData();
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		final Editor editor = prefs.edit();
		mCallQty = (EditText) findViewById(R.id.call_qty_value);
		mCallTime = (EditText) findViewById(R.id.call_time_value);
		Button saveButton = (Button) findViewById(R.id.save_button);
		mCallQty.setText("" + prefs.getInt(SETTING_CALL_QTY, 3));
		mCallTime.setText("" + prefs.getInt(SETTING_CALL_TIME, 15));
		
		mCallQty.addTextChangedListener(new TextWatcher() {
			
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
		
		mCallTime.addTextChangedListener(new TextWatcher() {
			
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
	
	private void loadData() {
		Intent intent = getIntent();
		if (intent.getData() != null) {
			Toast.makeText(getApplicationContext(), intent.getData().toString(), Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(getApplicationContext(), "Not a new user", Toast.LENGTH_LONG).show();
		}
	}

}
