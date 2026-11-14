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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsetsController;
import android.widget.FrameLayout;

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

    private static final String KEY_ACTIVE_TAB = "active_tab";

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

        // Page changes are tab-driven only: swiping the pager horizontally is
        // disabled so the VPN list / log / FAQ cannot be switched by accident.
        // TabLayoutMediator and setCurrentItem() still work with this flag off.
        mViewPager.setUserInputEnabled(false);

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

        if (savedInstanceState != null) {
            int savedTab = savedInstanceState.getInt(KEY_ACTIVE_TAB, 0);
            if (savedTab >= 0 && savedTab < mPagerAdapter.getItemCount()) {
                mViewPager.setCurrentItem(savedTab, false);
            }
        }

        FeedbackFragment.recordUse(this, false);
    }

    @Override
    protected void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putInt(KEY_ACTIVE_TAB, mViewPager.getCurrentItem());
    }

    private void updateUI(OpenVpnService service) {
        int newState = service.getConnectionState();
        service.startActiveDialog(this);

        // The first page is a stable host Fragment. Switching its content must not
        // change the ViewPager2 adapter's item ids: FragmentStateAdapter caches
        // Fragments by item id, and a mismatch on re-attach used to throw
        // "IllegalStateException: Design assumption violated".
        boolean showingStatus = newState != OpenConnectManagementThread.STATE_DISCONNECTED;
        for (Fragment f : getSupportFragmentManager().getFragments()) {
            if (f instanceof Tab0HostFragment) {
                ((Tab0HostFragment) f).setShowingStatus(showingStatus);
            }
        }

        if (mConnectionState != newState) {
            mConnectionState = newState;
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

        public MainPagerAdapter(AppCompatActivity activity) {
            super(activity);
        }

        @Override
        public int getItemCount() {
            return 3;
        }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new Tab0HostFragment();
                case 1:
                    return new LogFragment();
                case 2:
                    return new FaqFragment();
                default:
                    return new Tab0HostFragment();
            }
        }
    }

    /**
     * Stable host for the first ViewPager2 page.
     *
     * The list and the connection status used to be two different Fragments
     * returned by createFragment(0), selected through a changing item id.
     * FragmentStateAdapter caches Fragments by that id, so changing the id (or
     * returning ids outside the 0..getItemCount()-1 range without overriding
     * containsItem()) made view holders point at Fragments that had already
     * been collected, crashing on re-attach. Keep the page itself fixed and
     * swap only its child content instead.
     *
     * The host also owns the options menu: child Fragments declare no menu of
     * their own, so their menu callbacks are dispatched exactly once from here.
     */
    public static class Tab0HostFragment extends Fragment {

        private static final String KEY_SHOWING_STATUS = "tab0_showing_status";

        private boolean mShowingStatus = false;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
            if (savedInstanceState != null) {
                mShowingStatus = savedInstanceState.getBoolean(KEY_SHOWING_STATUS, false);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            FrameLayout host = new FrameLayout(requireContext());
            host.setId(R.id.tab0_fragment_container);
            host.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return host;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            applyChildFragment();
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putBoolean(KEY_SHOWING_STATUS, mShowingStatus);
        }

        public void setShowingStatus(boolean showingStatus) {
            if (mShowingStatus == showingStatus) {
                return;
            }
            mShowingStatus = showingStatus;
            applyChildFragment();
        }

        private void applyChildFragment() {
            if (!isAdded() || getView() == null) {
                // The view will be recreated later; onViewCreated applies the state then.
                return;
            }
            Fragment current = getChildFragmentManager()
                .findFragmentById(R.id.tab0_fragment_container);
            if ((mShowingStatus && current instanceof StatusFragment)
                    || (!mShowingStatus && current instanceof VPNProfileList)) {
                return;
            }
            if (getChildFragmentManager().isStateSaved()) {
                return;
            }
            Fragment child = mShowingStatus ? new StatusFragment() : new VPNProfileList();
            getChildFragmentManager().beginTransaction()
                .replace(R.id.tab0_fragment_container, child)
                .commitNow();
            requireActivity().invalidateOptionsMenu();
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            Fragment child = currentChild();
            if (child != null) {
                child.onCreateOptionsMenu(menu, inflater);
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            Fragment child = currentChild();
            if (child != null && child.onOptionsItemSelected(item)) {
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        private Fragment currentChild() {
            return getChildFragmentManager().findFragmentById(R.id.tab0_fragment_container);
        }
    }
}
