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
	private SharedPreferences mPrefs;
	private Editor mEditor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		mPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		mEditor = mPrefs.edit();
		mCallQty = (EditText) findViewById(R.id.call_qty_value);
		mCallTime = (EditText) findViewById(R.id.call_time_value);
		Button mSaveButton = (Button) findViewById(R.id.save_button);
		
		loadData();
		
		mSaveButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveData();
				finish();
				
			}
		});
	}
	
	private void loadData() {
		Intent intent = getIntent();
		if (intent.getData() != null) {
			Toast.makeText(getApplicationContext(), intent.getData().toString(), Toast.LENGTH_LONG).show();
		} else {
			mCallQty.setText("" + mPrefs.getInt(SETTING_CALL_QTY, 3));
			mCallTime.setText("" + mPrefs.getInt(SETTING_CALL_TIME, 15));
		}
	}

	private void saveData() {
		Intent intent = getIntent();
		if (intent.getData() != null) {
			Toast.makeText(getApplicationContext(), intent.getData().toString(), Toast.LENGTH_LONG).show();
		} else {
			mEditor.putInt(SETTING_CALL_QTY, Integer.parseInt(mCallQty.getText().toString()));
			mEditor.putInt(SETTING_CALL_TIME, Integer.parseInt(mCallTime.getText().toString()));
			mEditor.commit();			
		}
	}
	
}
