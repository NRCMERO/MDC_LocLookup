package com.openDC.loclookup.view.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.openDC.loclookup.controller.interfaces.ResultCallback;
import com.openDC.loclookup.controller.activities.LocationActivity;

import loclookup.opendc.com.loclookup.R;

public class Dialogs {
    /**
     * Show a dialog telling the user to turn gps on
     *
     * @param context  the context in which the dialog will be shown
     * @param callback the listener that will be used to notify user interaction
     */
    public static void showGpsNeededDialog(final Context context, final ResultCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.msg_enable_gps))
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.dismiss();
                        Toast.makeText(context, context.getString(R.string.toast_gps_needed), Toast.LENGTH_LONG).show();
                        callback.returnResultToCaller(Activity.RESULT_CANCELED, LocationActivity.RESULT_EMPTY);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
