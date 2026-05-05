/*
 * Copyright (c) 2014, Kevin Cernekee
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

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowInsetsController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

public class FragActivity extends AppCompatActivity {

    public static final String TAG = "OpenConnect";

    public static final String EXTRA_FRAGMENT_NAME = "app.openconnect.fragment_name";

    public static final String FRAGMENT_PREFIX = "app.openconnect.fragments.";

    @Override
    public void onCreate(Bundle savedInstanceState) {
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

        String fragName = getIntent().getStringExtra(EXTRA_FRAGMENT_NAME);
        setTitle(resolveTitle(fragName));

        if(savedInstanceState == null) {
            try {
                Fragment frag = (Fragment)Class.forName(FRAGMENT_PREFIX + fragName).newInstance();
                getFragmentManager().beginTransaction().add(R.id.fragment_container, frag).commit();
            } catch (Exception e) {
                Log.e(TAG, "unable to create fragment", e);
                finish();
            }
        }

        // Apply system bar insets as padding so content doesn't extend
        // behind status/nav bars on Android 15+ edge-to-edge.
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.subpage_root), (v, insets) -> {
                    v.setPadding(0, insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                            0, insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom);
                    return insets;
                });
    }

    private int resolveTitle(String fragName) {
        if ("AboutFragment".equals(fragName)) {
            return R.string.about_openconnect;
        } else if ("GeneralSettings".equals(fragName)) {
            return R.string.generalsettings;
        } else if ("TokenParentFragment".equals(fragName)) {
            return R.string.securid_info;
        } else if ("FeedbackFragment".equals(fragName)) {
            return R.string.report_problem;
        } else {
            return R.string.app;
        }
    }

}
