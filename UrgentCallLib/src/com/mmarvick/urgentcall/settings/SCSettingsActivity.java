package com.mmarvick.urgentcall.settings;

import android.os.Bundle;

import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.RulesDbContract.RulesEntry;

public class SCSettingsActivity extends AlertSettingsActivity {
	protected void onCreate(Bundle savedInstanceState) {
		xml = R.xml.pref_sc;
		alertType = RulesEntry.SC_STATE;
		super.onCreate(savedInstanceState);
	}
}
