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

package app.openconnect.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import app.openconnect.FileSelect;
import app.openconnect.R;

public class InlineFileTab extends Fragment
{
	private EditText mInlineData;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState == null) {
			mInlineData.setText(((FileSelect)getActivity()).getInlineData());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{

		View v = inflater.inflate(R.layout.file_dialog_inline, container, false);
		mInlineData =(EditText) v.findViewById(R.id.inlineFileData);
		Button useInlineButton = (Button) v.findViewById(R.id.inline_use_button);
		useInlineButton.setOnClickListener(view ->
				((FileSelect) getActivity()).saveInlineData(mInlineData.getText().toString()));
		return v;
	}

	public void setData(String data) {
		if(mInlineData!=null)
			mInlineData.setText(data);

	}

}
