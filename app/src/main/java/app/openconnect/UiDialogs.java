package app.openconnect;

import android.content.Context;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public final class UiDialogs {

    private UiDialogs() {
    }

    public static MaterialAlertDialogBuilder builder(Context context) {
        return new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_OpenConnect_MaterialAlertDialog);
    }
}
