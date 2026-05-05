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

package app.openconnect;

import android.os.Bundle;
import android.view.WindowInsetsController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import app.openconnect.core.OpenConnectManagementThread;
import app.openconnect.core.OpenVpnService;
import app.openconnect.core.VPNConnector;
import app.openconnect.fragments.*;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "OpenConnect";

    private ViewPager2 mViewPager;
    private TabLayout mTabLayout;
    private MainPagerAdapter mPagerAdapter;

    private int mConnectionState = OpenConnectManagementThread.STATE_DISCONNECTED;
    private VPNConnector mConn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setStatusBarColor(getColor(R.color.oc_surface_background));
        getWindow().setNavigationBarColor(getColor(R.color.oc_surface_background));
        WindowInsetsController insetsController = getWindow().getInsetsController();
        if (insetsController != null) {
            int appearance = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                | WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS;
            insetsController.setSystemBarsAppearance(appearance, appearance);
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setOverflowIcon(AppCompatResources.getDrawable(this, R.drawable.ic_oc_more_vertical));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mViewPager = findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_layout);

        toolbar.setNavigationOnClickListener(v -> {
            if (mViewPager.getCurrentItem() == 0) {
                toolbar.showOverflowMenu();
            } else {
                mViewPager.setCurrentItem(0, true);
            }
        });

        mPagerAdapter = new MainPagerAdapter(this);
        mViewPager.setAdapter(mPagerAdapter);

        new TabLayoutMediator(mTabLayout, mViewPager,
            (tab, position) -> {
                switch (position) {
                    case 0:
                        tab.setText(mConnectionState == OpenConnectManagementThread.STATE_DISCONNECTED
                            ? R.string.vpn_list_title : R.string.status);
                        break;
                    case 1: tab.setText(R.string.log); break;
                    case 2: tab.setText(R.string.faq); break;
                }
            }
        ).attach();

        FeedbackFragment.recordUse(this, false);
    }

    @Override
    protected void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putInt("active_tab", mViewPager.getCurrentItem());
    }

    private void updateUI(OpenVpnService service) {
        int newState = service.getConnectionState();
        service.startActiveDialog(this);

        if (mConnectionState != newState) {
            mConnectionState = newState;
            mPagerAdapter.notifyTab0Changed();
            TabLayout.Tab tab0 = mTabLayout.getTabAt(0);
            if (tab0 != null) {
                tab0.setText(newState == OpenConnectManagementThread.STATE_DISCONNECTED
                    ? R.string.vpn_list_title : R.string.status);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mConn = new VPNConnector(this, true) {
            @Override
            public void onUpdate(OpenVpnService service) {
                updateUI(service);
            }
        };
    }

    @Override
    protected void onPause() {
        mConn.stopActiveDialog();
        mConn.unbind();
        super.onPause();
    }

    private class MainPagerAdapter extends FragmentStateAdapter {

        private static final long ID_VPN_LIST = 0L;
        private static final long ID_STATUS = 1L;
        private static final long ID_LOG = 2L;
        private static final long ID_FAQ = 3L;

        private boolean mShowingStatus = false;

        public MainPagerAdapter(AppCompatActivity activity) {
            super(activity);
        }

        public void notifyTab0Changed() {
            mShowingStatus = (mConnectionState != OpenConnectManagementThread.STATE_DISCONNECTED);
            notifyItemChanged(0);
        }

        @Override
        public int getItemCount() {
            return 3;
        }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return mShowingStatus ? new StatusFragment() : new VPNProfileList();
                case 1:
                    return new LogFragment();
                case 2:
                    return new FaqFragment();
                default:
                    return new VPNProfileList();
            }
        }

        @Override
        public long getItemId(int position) {
            if (position == 0) {
                return mShowingStatus ? ID_STATUS : ID_VPN_LIST;
            }
            return position + 2L;
        }
    }
}
