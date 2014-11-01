package com.mmarvick.urgentcall.settings;

import android.os.Bundle;

import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.DbContractOldDatabase.RulesEntryOld;

public class SCSettingsActivity extends AlertSettingsActivity {
	protected void onCreate(Bundle savedInstanceState) {
		xml = R.xml.pref_sc;
		alertType = RulesEntryOld.SC_STATE;
		super.onCreate(savedInstanceState);
	}
}
