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

package app.openconnect;

import app.openconnect.core.ProfileManager;
import app.openconnect.fragments.ConnectionEditorFragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowInsetsController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

public class ConnectionEditorActivity extends AppCompatActivity {

    private String mName = "";
    private String mUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subpage_host);

        getWindow().setStatusBarColor(getColor(R.color.oc_surface_background));
        getWindow().setNavigationBarColor(getColor(R.color.oc_surface_background));
        WindowInsetsController insetsController = getWindow().getInsetsController();
        if (insetsController != null) {
            int appearance = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                    | WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS;
            insetsController.setSystemBarsAppearance(appearance, appearance);
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_oc_back);
        toolbar.setNavigationIconTint(getColor(R.color.oc_text_primary));
        toolbar.setOverflowIcon(AppCompatResources.getDrawable(this, R.drawable.ic_oc_more_vertical));
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        setSupportActionBar(toolbar);

        ConnectionEditorFragment frag = new ConnectionEditorFragment();
        mUUID = getIntent().getStringExtra(getPackageName() + ".profileUUID");
        mName = getIntent().getStringExtra(getPackageName() + ".profileName");
        Bundle args = new Bundle();
        args.putString("profileUUID", mUUID);
        frag.setArguments(args);
        setTitle(getString(R.string.edit_profile_title, mName));

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, frag)
                .commit();

        // On Android 15+, edge-to-edge is enabled by default.
        // Apply system bar insets as padding to the content view so
        // PreferenceFragment content doesn't extend behind status/nav bars.
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.subpage_root), (v, insets) -> {
                    v.setPadding(0, insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                            0, insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom);
                    return insets;
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vpnpreferences_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        if(item.getItemId() == R.id.remove_vpn)
            askProfileRemoval();
        return super.onOptionsItemSelected(item);
    }

    public void setProfileName(String name) {
        mName = name;
        setTitle(getString(R.string.edit_profile_title, mName));
    }

    private void askProfileRemoval() {
        UiDialogs.builder(this)
                .setTitle("Confirm deletion")
                .setMessage(getString(R.string.remove_vpn_query, mName))
                .setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ProfileManager.delete(mUUID);
                finish();
            }
        })
                .setNegativeButton(android.R.string.no,null)
                .show();
    }
}
