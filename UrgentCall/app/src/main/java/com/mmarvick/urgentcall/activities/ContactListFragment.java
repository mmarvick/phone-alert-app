package com.mmarvick.urgentcall.activities;

import java.util.List;

import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.base.Alert;
import com.mmarvick.urgentcall.data.call.CallAlert;
import com.mmarvick.urgentcall.data.call.CallAlertStore;
import com.mmarvick.urgentcall.data.text.TextAlert;
import com.mmarvick.urgentcall.data.base.DbContract;
import com.mmarvick.urgentcall.data.text.TextAlertStore;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class ContactListFragment extends DialogFragment {
	private Alert mAlert;
	private int mListType;
	private List<String> mContacts;
	private ContactsArrayAdapter mAdapter;
	
	public static final int REQUEST_CODE = 0;
	public static final String ALERT_CLASS = "ALERT_CLASS";
	public static final String ALERT_CLASS_CALL = "ALERT_CLASS_CALL";
	public static final String ALERT_CLASS_TEXT = "ALERT_CLASS_TEXT";
	public static final String ALERT_ID = "ALERT_ID";
	public static final String FILTER_BY = "FILTER_BY";
	


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle args = getArguments();
		
		String alertType = args.getString(ALERT_CLASS);
		long alertId = args.getLong(ALERT_ID);
		
		if (alertType.equals(CallAlert.ALERT_TYPE)) {
			mAlert = CallAlertStore.getInstance(getActivity()).getAlert(alertId);
		} else if (alertType.equals(TextAlert.ALERT_TYPE)) {
			mAlert = TextAlertStore.getInstance(getActivity()).getAlert(alertId);
		}
		
		mListType = args.getInt(FILTER_BY);
		
		getContacts();
		
		((MainActivity) getActivity()).setContactListFragment(this);
		ListView view = new ListView(getActivity());
		
		mAdapter = new ContactsArrayAdapter(getActivity(), mContacts);
		view.setAdapter(mAdapter);
		
		View titleView = getActivity().getLayoutInflater().inflate(R.layout.dialog_title_contact_list, null);
		TextView titleViewTitle = (TextView) titleView.findViewById(R.id.textViewDialogTitle);
		titleViewTitle.setText(getTitle());
		ImageButton imageButtonAddContact = (ImageButton) titleView.findViewById(R.id.imageButtonAddPerson);
		imageButtonAddContact.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addContact();
				
			}
		});
		
		
		return new AlertDialog.Builder(getActivity())
			.setCustomTitle(titleView)
			.setPositiveButton(getActivity().getString(R.string.state_change_dialog_ok), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dismiss();
					
				}
			})
			.setView(view)
			.create();
		
	}

    public void getContacts() {
        if (mListType == DbContract.ENTRY_LIST_ALLOW_LIST) {
            mContacts = mAlert.getAllowedContacts();
        } else {
            mContacts = mAlert.getBlockedContacts();
        }

    }
	
	private void addContact() {
		Intent contactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		getActivity().startActivityForResult(contactIntent, REQUEST_CODE);		
	}
	
	public void contactAdded(Intent data) {
		Uri uri = data.getData();
		String[] projection = new String[] {ContactsContract.Contacts.LOOKUP_KEY};
		Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
		cursor.moveToFirst();
		String lookup = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
		cursor.close();
		
		if (mAlert.addContact(lookup, mListType)) {
			mAdapter.add(lookup);
		}
	}
	
	private String getTitle() {
		String titleListType = "";
		
		switch (mListType) {
		case (DbContract.ENTRY_FILTER_BY_ALLOWED_ONLY):
			titleListType = getString(R.string.list_fragment_list_allowed);
			break;
		case (DbContract.ENTRY_FILTER_BY_BLOCKED_IGNORED):
			titleListType = getString(R.string.list_fragment_list_blocked);
			break;
		}
		
		return titleListType;	
	}
	
	// Adapter for showing contacts in the ListView
	private class ContactsArrayAdapter extends ArrayAdapter<String> {
		private final Context context;
		private final List<String> values;

		public ContactsArrayAdapter(Context context, List<String> values) {
			super(context, R.layout.list_item_contact, values);
			this.context = context;
			this.values = values;
		}
		
		@Override
		public View getView(final int position, View view, ViewGroup parent) {
			
			LayoutInflater inflater = LayoutInflater.from(context);
			View rowView = inflater.inflate(R.layout.list_item_contact, null);
			
			// Set contact name
			TextView textView = (TextView) rowView.findViewById(R.id.list_contact_name_simple);
			textView.setText(mAlert.getNameFromLookup(getActivity(), values.get(position)));
			
			// Create delete action
			ImageButton imageButton = (ImageButton) rowView.findViewById(R.id.list_contact_delete_button);
			imageButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					mAlert.removeContact(values.get(position), mListType);
					remove(values.get(position));
					
				};	
			});
			
			return rowView;
		}
		
	}	
}
