/*
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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import app.openconnect.R;
import app.openconnect.core.OpenConnectManagementThread;
import app.openconnect.core.OpenVpnService;
import app.openconnect.core.VPNConnector;

import org.infradead.libopenconnect.LibOpenConnect;

public class StatusFragment extends Fragment {

	private View mView;
	private VPNConnector mConn;

	private CommonMenu mDropdown;
	private Button mDisconnectButton;
	private View mConnectionRows;
	private View mConnectionTimeCard;
	private View mConnectionStatusDot;
	private TextView mConnectionStateView;
	private TextView mConnectionDetailView;
	private TextView mConnectionTimeView;
	private TextView mTxPrimaryValue;
	private TextView mTxSecondaryValue;
	private TextView mRxPrimaryValue;
	private TextView mRxSecondaryValue;
	private TextView mServerNameValue;
	private TextView mServerNameSecondaryValue;
	private TextView mLocalIp4Value;
	private TextView mLocalIp4SecondaryValue;
	private TextView mLocalIp4NetmaskValue;
	private TextView mLocalIp4NetmaskSecondaryValue;
	private TextView mLocalIp6Value;
	private TextView mLocalIp6SecondaryValue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);

    	mView = inflater.inflate(R.layout.status, container, false);
    	mDisconnectButton = (Button)mView.findViewById(R.id.disconnect_button);
		mConnectionRows = mView.findViewById(R.id.connection_rows);
		mConnectionTimeCard = mView.findViewById(R.id.connection_time_card);
		mConnectionStatusDot = mView.findViewById(R.id.connection_status_dot);
		mConnectionStateView = (TextView)mView.findViewById(R.id.connection_state);
		mConnectionDetailView = (TextView)mView.findViewById(R.id.connection_detail);
		mConnectionTimeView = (TextView)mView.findViewById(R.id.connection_time);
		mTxPrimaryValue = (TextView)mView.findViewById(R.id.tx_primary_value);
		mTxSecondaryValue = (TextView)mView.findViewById(R.id.tx_secondary_value);
		mRxPrimaryValue = (TextView)mView.findViewById(R.id.rx_primary_value);
		mRxSecondaryValue = (TextView)mView.findViewById(R.id.rx_secondary_value);
		mServerNameValue = (TextView)mView.findViewById(R.id.server_name_value);
		mServerNameSecondaryValue = (TextView)mView.findViewById(R.id.server_name_secondary_value);
		mLocalIp4Value = (TextView)mView.findViewById(R.id.local_ip4_value);
		mLocalIp4SecondaryValue = (TextView)mView.findViewById(R.id.local_ip4_secondary_value);
		mLocalIp4NetmaskValue = (TextView)mView.findViewById(R.id.local_ip4_netmask_value);
		mLocalIp4NetmaskSecondaryValue = (TextView)mView.findViewById(R.id.local_ip4_netmask_secondary_value);
		mLocalIp6Value = (TextView)mView.findViewById(R.id.local_ip6_value);
		mLocalIp6SecondaryValue = (TextView)mView.findViewById(R.id.local_ip6_secondary_value);

    	mDisconnectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mConn.service == null) {
					return;
				}
				if (mConn.service.getConnectionState() ==
						OpenConnectManagementThread.STATE_DISCONNECTED) {
					mConn.service.startReconnectActivity(requireActivity());
				} else {
					mConn.service.stopVPN();
				}
			}
    	});

    	mConn = new VPNConnector(requireContext(), false) {
			@Override
			public void onUpdate(OpenVpnService service) {
				updateUI(service);
			}
    	};

    	return mView;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		mDropdown = new CommonMenu(requireActivity(), menu, true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDropdown.onOptionsItemSelected(item)) {
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

    @Override
    public void onDestroyView() {
    	if (mConn != null) {
    		mConn.unbind();
    	}
    	super.onDestroyView();
    }

	private void setPrimaryAndSecondaryValue(TextView primaryView, TextView secondaryView,
			String primaryValue, String secondaryValue) {
		primaryView.setText(primaryValue);
		if (secondaryValue == null || secondaryValue.length() == 0) {
			secondaryView.setVisibility(View.GONE);
		} else {
			secondaryView.setVisibility(View.VISIBLE);
			secondaryView.setText(secondaryValue);
		}
	}

    private void updateUI(OpenVpnService service) {
		int state = service.getConnectionState();
		boolean connected = (state == OpenConnectManagementThread.STATE_CONNECTED);
		String disabled = getString(R.string.disabled);

		mConnectionStateView.setText(service.getConnectionStateName());

		if (connected) {
			mConnectionStatusDot.setVisibility(View.VISIBLE);
			mConnectionDetailView.setText(getString(R.string.state_connected_to, service.profile.getName()));
			mConnectionTimeCard.setVisibility(View.VISIBLE);
			mConnectionTimeView.setText(OpenVpnService.formatElapsedTime(service.startTime.getTime()));
			mConnectionRows.setVisibility(View.VISIBLE);

			if (mConn.statsValid) {
				setPrimaryAndSecondaryValue(mTxPrimaryValue, mTxSecondaryValue,
						OpenVpnService.humanReadableByteCount(mConn.deltaStats.txBytes, true) + "/s",
						OpenVpnService.humanReadableByteCount(mConn.newStats.txBytes, false));
				setPrimaryAndSecondaryValue(mRxPrimaryValue, mRxSecondaryValue,
						OpenVpnService.humanReadableByteCount(mConn.deltaStats.rxBytes, true) + "/s",
						OpenVpnService.humanReadableByteCount(mConn.newStats.rxBytes, false));
			} else {
				setPrimaryAndSecondaryValue(mTxPrimaryValue, mTxSecondaryValue, "--", null);
				setPrimaryAndSecondaryValue(mRxPrimaryValue, mRxSecondaryValue, "--", null);
			}

			setPrimaryAndSecondaryValue(mServerNameValue, mServerNameSecondaryValue,
					service.serverName != null ? service.serverName : disabled, null);

			LibOpenConnect.IPInfo ip = service.ipInfo;
			if (ip != null && ip.addr != null && ip.netmask != null) {
				setPrimaryAndSecondaryValue(mLocalIp4Value, mLocalIp4SecondaryValue, ip.addr, null);
				setPrimaryAndSecondaryValue(mLocalIp4NetmaskValue, mLocalIp4NetmaskSecondaryValue, ip.netmask, null);
			} else {
				setPrimaryAndSecondaryValue(mLocalIp4Value, mLocalIp4SecondaryValue, disabled, null);
				setPrimaryAndSecondaryValue(mLocalIp4NetmaskValue, mLocalIp4NetmaskSecondaryValue, disabled, null);
			}

			setPrimaryAndSecondaryValue(mLocalIp6Value, mLocalIp6SecondaryValue,
					(ip != null && ip.netmask6 != null) ? ip.netmask6 : disabled, null);
		} else {
			String profileName = service.getReconnectName();
			mConnectionDetailView.setText(profileName != null
					? getString(R.string.reconnect_to, profileName)
					: service.getConnectionStateName());
			mConnectionStatusDot.setVisibility(View.GONE);
			mConnectionTimeCard.setVisibility(View.GONE);
			mConnectionRows.setVisibility(View.GONE);
		}

		// Check explicitly for "disconnected" so the user can cancel connections-in-progress
		if (state == OpenConnectManagementThread.STATE_DISCONNECTED) {
			String profileName = service.getReconnectName();
			if (profileName != null) {
				mDisconnectButton.setVisibility(View.VISIBLE);
				mDisconnectButton.setText(getString(R.string.reconnect_to, profileName));
			} else {
				mDisconnectButton.setVisibility(View.INVISIBLE);
			}
		} else {
			mDisconnectButton.setVisibility(View.VISIBLE);
			mDisconnectButton.setText(R.string.disconnect);
		}
    }
}
