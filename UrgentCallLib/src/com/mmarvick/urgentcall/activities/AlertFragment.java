package com.mmarvick.urgentcall.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.Alert;
import com.mmarvick.urgentcall.views.AlertView;
import com.mmarvick.urgentcall.views.AlertView.OnDeleteListener;
import com.mmarvick.urgentcall.widgets.UpgradeDialog;

public abstract class AlertFragment extends Fragment {
	protected List<AlertView> alertViews;
	protected View mView;
	protected int nextId;
	protected ContactListFragment mContactListFragment;
	
	protected abstract List<? extends Alert> getAlerts();
	protected abstract AlertView createAlertView();
	protected abstract Alert createAlert();
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		setHasOptionsMenu(true);
		
		View fragmentView = inflater.inflate(R.layout.fragment_alert, container, false);
		mView = fragmentView.findViewById(R.id.linearLayoutForAlerts);
		
		alertViews = new ArrayList<AlertView>();
		nextId = 1000;
		
		for (Alert alert : getAlerts()) {
			addAlert(alert);
			
		}
		
		return fragmentView;
    }
	
	protected void addNewAlert() {
		if (getResources().getBoolean(R.bool.paid_version)) {
			addAlert(createAlert());
		} else {
			UpgradeDialog.upgradeDialog(getActivity(), getString(R.string.upgrade_body_add_alert));
		}
	}
	
	protected void addAlert(Alert alert) {
		AlertView alertView = createAlertView();
		alertView.setAlert(alert);
		alertView.setId(nextId++);
		alertView.setOnDeleteListener(new OnDeleteListener() {
			
			@Override
			public void onDelete(View v) {
				removeView(v);
				
			}
		});
		alertViews.add(alertView);
		((ViewGroup) mView).addView(alertView);		
	}		
	
	public void setContactListFragment(ContactListFragment contactListFragment) {
		mContactListFragment = contactListFragment;
	}

	
	private void removeView(View v) {
		alertViews.remove(v);
		((ViewGroup) mView).removeView(v);
	}
	
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.alert_fragment_actions, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_add_alert) {
			addNewAlert();
			return true;
		}
		
		return false;
	}	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mContactListFragment != null && requestCode == ContactListFragment.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			mContactListFragment.contactAdded(data);
			
		}
		for (AlertView alertView : alertViews) {
			if (alertView.getId() == requestCode && resultCode == Activity.RESULT_OK) {
				alertView.updateAlertTone(data);
			}
			
		}
	}
}
