/*
 * Adapted from OpenVPN for Android
 * Copyright (c) 2012-2013, Arne Schwabe
 * Copyright (c) 2013, Kevin Cernekee
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * In addition, as a special exception, the copyright holders give
 * permission to link the code of portions of this program with the
 * OpenSSL library.
 */

package app.openconnect.fragments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import app.openconnect.ConnectionEditorActivity;
import app.openconnect.R;
import app.openconnect.UiDialogs;
import app.openconnect.VpnProfile;
import app.openconnect.api.GrantPermissionsActivity;
import app.openconnect.core.FragCache;
import app.openconnect.core.OpenConnectManagementThread;
import app.openconnect.core.OpenVpnService;
import app.openconnect.core.ProfileManager;
import app.openconnect.core.VPNConnector;

public class VPNProfileList extends Fragment {

	private static final String TAG = "OpenConnect";

	private static final int MENU_ADD_PROFILE = 1;

	private ArrayAdapter<VpnProfile> mArrayadapter;
	private CommonMenu mDropdown;
	private ListView mListView;

	private AlertDialog mDialog;
	private EditText mDialogEntry;
	private Button mReconnectButton;
	private View mReconnectBox;

	private VPNConnector mConn;

	private class VPNArrayAdapter extends ArrayAdapter<VpnProfile> {

		public VPNArrayAdapter(Context context, int resource, int textViewResourceId) {
			super(context, resource, textViewResourceId);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View v = super.getView(position, convertView, parent);
			final VpnProfile profile = mArrayadapter.getItem(position);

			TextView badgeView = (TextView) v.findViewById(R.id.vpn_item_badge);
			if (badgeView != null && profile != null) {
				badgeView.setText(getBadgeLabel(profile));
			}

			View titleview = v.findViewById(R.id.vpn_list_item_left);
			titleview.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startVPN(profile);
				}
			});

			View settingsview = v.findViewById(R.id.quickedit_settings);
			settingsview.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					editVPN(profile);
				}
			});

			return v;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mDialog != null) {
			FragCache.put("VPNProfileList", "mDialogEntry", mDialogEntry.getText().toString());
			mDialog.dismiss();
			mDialog = null;
		} else {
			FragCache.put("VPNProfileList", "mDialogEntry", null);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.vpn_profile_list, container, false);

		mListView = (ListView) v.findViewById(android.R.id.list);
		mArrayadapter = new VPNArrayAdapter(requireContext(), R.layout.vpn_list_item, R.id.vpn_item_title);
		mListView.setAdapter(mArrayadapter);

		mReconnectBox = v.findViewById(R.id.reconnect_box);
		mReconnectButton = (Button)v.findViewById(R.id.reconnect_button);
		mReconnectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mConn.service != null &&
						mConn.service.getConnectionState() ==
						OpenConnectManagementThread.STATE_DISCONNECTED) {
					mConn.service.startReconnectActivity(requireActivity());
				}
			}
		});

		mConn = new VPNConnector(requireContext(), false) {
			@Override
			public void onUpdate(OpenVpnService service) {
				String profileName = service.getReconnectName();
				if (profileName != null) {
					mReconnectButton.setText(getString(R.string.reconnect_to, profileName));
					mReconnectBox.setVisibility(View.VISIBLE);
				} else {
					mReconnectBox.setVisibility(View.GONE);
				}
			}
		};

		return v;
	}

	@Override
	public void onDestroyView() {
		if (mConn != null) {
			mConn.unbind();
		}
		super.onDestroyView();
	}

	private String getBadgeLabel(VpnProfile profile) {
		String name = profile != null ? profile.getName() : null;
		if (name == null || name.length() == 0) {
			return "?";
		}
		return name.substring(0, 1).toUpperCase(Locale.getDefault());
	}

	class VpnProfileNameComperator implements Comparator<VpnProfile> {

		@Override
		public int compare(VpnProfile lhs, VpnProfile rhs) {
			return lhs.mName.compareTo(rhs.mName);
		}

	}

	@Override
	public void onResume() {
		super.onResume();

		// always refresh this on resume, as the list may have changed
		List<VpnProfile> allvpn = new ArrayList<VpnProfile>(ProfileManager.getProfiles());
		Collections.sort(allvpn);

		mArrayadapter.clear();
		mArrayadapter.addAll(allvpn);
		mArrayadapter.notifyDataSetChanged();

		updateListVisibility(allvpn.size());

		String s = FragCache.get("VPNProfileList", "mDialogEntry");
		if (s != null) {
			onAddProfileClicked(s);
		}
	}

	private void updateListVisibility(int count) {
		View rootView = getView();
		View emptyView = rootView != null ? rootView.findViewById(android.R.id.empty) : null;
		if (count > 0) {
			if (mListView != null) mListView.setVisibility(View.VISIBLE);
			if (emptyView != null) emptyView.setVisibility(View.GONE);
		} else {
			if (mListView != null) mListView.setVisibility(View.GONE);
			if (emptyView != null) emptyView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.add(Menu.NONE, MENU_ADD_PROFILE, Menu.NONE, R.string.menu_add_profile)
			.setIcon(R.drawable.ic_oc_add)
			.setAlphabeticShortcut('a')
			.setTitleCondensed(requireActivity().getString(R.string.add))
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		mDropdown = new CommonMenu(requireActivity(), menu, false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int itemId = item.getItemId();
		if (itemId == MENU_ADD_PROFILE) {
			onAddProfileClicked("");
			return true;
		} else if (mDropdown.onOptionsItemSelected(item)) {
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	private void handleNewVPNEntry() {
		String name = mDialogEntry.getText().toString();

		mDialog.dismiss();
		mDialog = null;

		name = name.replaceAll("\\s", "");
		if (!name.equals("")) {
			FeedbackFragment.recordProfileAdd(requireActivity());
			editVPN(ProfileManager.create(name));
		}
	}

	private void onAddProfileClicked(String savedEntry) {
		final Context context = requireContext();
		View v = View.inflate(context, R.layout.add_new_vpn, null);

		mDialogEntry = (EditText)v.findViewById(R.id.entry);
		mDialogEntry.setText(savedEntry);

		AlertDialog.Builder builder = UiDialogs.builder(context)
			.setTitle(R.string.menu_add_profile)
			.setMessage(R.string.add_profile_hostname_prompt)
			.setView(v);

		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				handleNewVPNEntry();
			}
		});
		builder.setNegativeButton(android.R.string.cancel, null);

		EditText et = (EditText)v.findViewById(R.id.entry);
		et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
				if (actionId == EditorInfo.IME_ACTION_DONE ||
						(keyEvent != null &&
								keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
								keyEvent.getAction() == KeyEvent.ACTION_DOWN)) {
					handleNewVPNEntry();
					return true;
				} else {
					return false;
				}
			}
		});

		mDialog = builder.create();

		mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				mDialog = null;
			}
		});

		mDialog.show();

		// Block user from entering an empty hostname

		final Button okButton = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
		okButton.setEnabled(!savedEntry.equals(""));

		mDialogEntry.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
				okButton.setEnabled(mDialogEntry.getText().length() != 0);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
		});
	}

	private void editVPN(VpnProfile profile) {
		if (profile == null) {
			return;
		}
		String pfx = requireActivity().getPackageName();
		Intent vprefintent = new Intent(requireActivity(), ConnectionEditorActivity.class)
			.putExtra(pfx + ".profileUUID", profile.getUUID().toString())
			.putExtra(pfx + ".profileName", profile.getName());

		startActivity(vprefintent);
	}

	private void startVPN(VpnProfile profile) {
		if (profile == null) {
			return;
		}
		Intent intent = new Intent(requireActivity(), GrantPermissionsActivity.class);
		String pkg = requireActivity().getPackageName();

		intent.putExtra(pkg + GrantPermissionsActivity.EXTRA_UUID, profile.getUUID().toString());
		intent.setAction(Intent.ACTION_MAIN);
		startActivity(intent);
	}
}
