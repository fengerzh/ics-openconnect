package app.openconnect.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.view.View;
import android.widget.ListView;

import app.openconnect.R;

public abstract class StyledPreferenceFragment extends PreferenceFragment {

    protected void applyPreferenceCardLayouts() {
        PreferenceGroup screen = getPreferenceScreen();
        if (screen != null) {
            stylePreferenceGroup(screen);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        stylePreferenceListView();
    }

    private void stylePreferenceGroup(PreferenceGroup group) {
        final int count = group.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            Preference preference = group.getPreference(i);
            if (preference instanceof PreferenceCategory) {
                preference.setLayoutResource(R.layout.oc_preference_category);
            } else {
                preference.setLayoutResource(R.layout.oc_preference_item);
            }

            if (preference instanceof PreferenceGroup) {
                stylePreferenceGroup((PreferenceGroup) preference);
            }
        }
    }

    private void stylePreferenceListView() {
        View root = getView();
        if (root == null) {
            return;
        }

        ListView listView = (ListView) root.findViewById(android.R.id.list);
        if (listView == null) {
            return;
        }

        listView.setBackgroundColor(Color.TRANSPARENT);
        listView.setDivider(new ColorDrawable(Color.TRANSPARENT));
        listView.setDividerHeight(dp(12));
        listView.setSelector(android.R.color.transparent);
        listView.setClipToPadding(false);
        listView.setPadding(dp(24), dp(8), dp(24), dp(24));
    }

    private int dp(int value) {
        if (getActivity() == null) {
            return value;
        }
        return Math.round(value * getActivity().getResources().getDisplayMetrics().density);
    }
}
