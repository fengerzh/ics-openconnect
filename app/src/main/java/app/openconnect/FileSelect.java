/*
 * Adapted from OpenVPN for Android
 * Copyright (c) 2012-2013, Arne Schwabe
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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.WindowInsetsController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import app.openconnect.fragments.FileSelectionFragment;
import app.openconnect.fragments.InlineFileTab;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

public class FileSelect extends AppCompatActivity {
	public static final String RESULT_DATA = "RESULT_PATH";
	public static final String START_DATA = "START_DATA";
	public static final String WINDOW_TITLE = "WINDOW_TILE";
	public static final String NO_INLINE_SELECTION = "app.openconnect.NO_INLINE_SELECTION";
	public static final String FORCE_INLINE_SELECTION = "app.openconnect.FORCE_INLINE_SELECTION";
	public static final String SHOW_CLEAR_BUTTON = "app.openconnect.SHOW_CLEAR_BUTTON";
	public static final String DO_BASE64_ENCODE = "app.openconnect.BASE64ENCODE";

	private static final int MAX_FILE_LEN = 32768;
	private static final String FILE_FRAGMENT_TAG = "fileExplorer";
	private static final String INLINE_FRAGMENT_TAG = "inlineEditor";
	private static final String STATE_SELECTED_TAB = "selectedTab";
	private static final int TAB_FILE_EXPLORER = 0;
	private static final int TAB_INLINE_EDITOR = 1;

	private FileSelectionFragment mFSFragment;
	private InlineFileTab mInlineFragment;
	private String mData;
	private boolean mNoInline;
	private boolean mForceInline;
	private boolean mShowClear;
	private boolean mBase64Encode;
	private int mSelectedTab = TAB_FILE_EXPLORER;
	private TabLayout mTabLayout;
	
		
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.file_dialog);

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

		mData = getIntent().getStringExtra(START_DATA);
		if(mData==null)
			mData=Environment.getExternalStorageDirectory().getPath();
		
		String title = getIntent().getStringExtra(WINDOW_TITLE);
		int titleId = getIntent().getIntExtra(WINDOW_TITLE, 0);
		if(titleId!=0) 
			title =getString(titleId);
		if(title!=null)
			setTitle(title);
		
		mNoInline = getIntent().getBooleanExtra(NO_INLINE_SELECTION, false);
		mForceInline = getIntent().getBooleanExtra(FORCE_INLINE_SELECTION, false);
		mShowClear = getIntent().getBooleanExtra(SHOW_CLEAR_BUTTON, false);
		mBase64Encode = getIntent().getBooleanExtra(DO_BASE64_ENCODE, false);

		mSelectedTab = savedInstanceState != null
				? savedInstanceState.getInt(STATE_SELECTED_TAB, TAB_FILE_EXPLORER)
				: TAB_FILE_EXPLORER;

		mFSFragment = (FileSelectionFragment) getFragmentManager().findFragmentByTag(FILE_FRAGMENT_TAG);
		if (mFSFragment == null) {
			mFSFragment = new FileSelectionFragment();
		}
		applyFileExplorerMode();

		if (!mNoInline) {
			mInlineFragment = (InlineFileTab) getFragmentManager().findFragmentByTag(INLINE_FRAGMENT_TAG);
			if (mInlineFragment == null) {
				mInlineFragment = new InlineFileTab();
			}
		} else {
			mSelectedTab = TAB_FILE_EXPLORER;
		}

		setupTabs();
		showTab(mSelectedTab);

		ViewCompat.setOnApplyWindowInsetsListener(
				findViewById(R.id.file_select_root), (v, insets) -> {
					v.setPadding(0, insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
							0, insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom);
					return insets;
				});
	}
	
	public boolean showClear() {
		if(mData == null || mData.equals(""))
			return false;
		else
			return mShowClear;
	}

	private void setupTabs() {
		mTabLayout = findViewById(R.id.file_tabs);
		mTabLayout.removeAllTabs();
		mTabLayout.addTab(mTabLayout.newTab().setText(R.string.file_explorer_tab),
				mSelectedTab == TAB_FILE_EXPLORER);
		if (!mNoInline) {
			mTabLayout.addTab(mTabLayout.newTab().setText(R.string.inline_file_tab),
					mSelectedTab == TAB_INLINE_EDITOR);
		}
		mTabLayout.setVisibility(mTabLayout.getTabCount() > 1 ? TabLayout.VISIBLE : TabLayout.GONE);
		mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				showTab(tab.getPosition());
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {
			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {
			}
		});
	}

	private void applyFileExplorerMode() {
		if (mNoInline) {
			mFSFragment.setNoInLine();
		} else if (mForceInline) {
			mFSFragment.setForceInLine();
		}
	}

	private void showTab(int position) {
		if (position == TAB_INLINE_EDITOR && mNoInline) {
			position = TAB_FILE_EXPLORER;
		}
		mSelectedTab = position;

		Fragment fragmentToShow = position == TAB_INLINE_EDITOR ? mInlineFragment : mFSFragment;
		String fragmentTag = position == TAB_INLINE_EDITOR ? INLINE_FRAGMENT_TAG : FILE_FRAGMENT_TAG;
		Fragment fragmentToHide = position == TAB_INLINE_EDITOR ? mFSFragment : mInlineFragment;

		android.app.FragmentTransaction tx = getFragmentManager().beginTransaction();
		if (fragmentToHide != null && fragmentToHide.isAdded()) {
			tx.detach(fragmentToHide);
		}
		if (fragmentToShow != null) {
			if (fragmentToShow.isAdded()) {
				tx.attach(fragmentToShow);
			} else {
				tx.add(R.id.file_fragment_container, fragmentToShow, fragmentTag);
			}
		}
		tx.commit();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SELECTED_TAB, mSelectedTab);
	}
	
	public void importFile(String path) {
		File ifile = new File(path);
		String error = null;

		try {

			String data = "";
			
			if (ifile.length() > MAX_FILE_LEN) {
				error = getString(R.string.file_too_large);
			} else {
				byte[] filedata = readBytesFromFile(ifile) ;
				if(mBase64Encode)
					data += Base64.encodeToString(filedata, Base64.DEFAULT);
				else
					data += new String(filedata);
				mData = data;

				/*
				mInlineFragment.setData(data);
				getActionBar().selectTab(inlineFileTab);
				*/
				saveInlineData(data);
			}
		} catch (FileNotFoundException e) {
			error = e.getLocalizedMessage();
		} catch (IOException e) {
			error = e.getLocalizedMessage();
		}

		if (error != null) {
			UiDialogs.builder(this)
					.setTitle(R.string.error_importing_file)
					.setMessage(getString(R.string.import_error_message) + ": " + error)
					.setPositiveButton(android.R.string.ok, null)
					.show();
		}
	}

	private byte[] readBytesFromFile(File file) throws IOException {
		InputStream input = new FileInputStream(file);

		long len= file.length();


		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) len];

		// Read in the bytes
		int offset = 0;
		int bytesRead = 0;
		while (offset < bytes.length
				&& (bytesRead=input.read(bytes, offset, bytes.length-offset)) >= 0) {
			offset += bytesRead;
		}

		input.close();
		return bytes;
	}
	
	
	public void setFile(String path) {
		Intent intent = new Intent();
		intent.putExtra(RESULT_DATA, path);
		setResult(RESULT_OK,intent);
		finish();		
	}

	public String getSelectPath() {
		if(!mData.startsWith(VpnProfile.INLINE_TAG))
			return mData;
		else
			return Environment.getExternalStorageDirectory().getPath();
	}

	public CharSequence getInlineData() {
		if(mData.startsWith(VpnProfile.INLINE_TAG))
			return mData.substring(VpnProfile.INLINE_TAG.length());
		else
			return "";
	}
	
	public void clearData() {
		Intent intent = new Intent();
		intent.putExtra(RESULT_DATA, (String)null);
		setResult(RESULT_OK,intent);
		finish();
		
	}

	public void saveInlineData(String string) {
		Intent intent = new Intent();
		
		intent.putExtra(RESULT_DATA,VpnProfile.INLINE_TAG + string);
		setResult(RESULT_OK,intent);
		finish();
		
	}
}
