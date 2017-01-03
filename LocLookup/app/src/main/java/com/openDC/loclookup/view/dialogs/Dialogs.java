package com.openDC.loclookup.view.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.openDC.loclookup.controller.interfaces.ResultCallback;
import com.openDC.loclookup.controller.activities.LocationActivity;
import com.openDC.loclookup.model.AppPrefs;
import com.openDC.loclookup.model.ModelUtils;
import com.openDC.loclookup.model.vo.FieldItem;

import java.util.List;

import diewald_shapeFile.shapeFile.ShapeFile;
import loclookup.opendc.com.loclookup.R;

public class Dialogs {
    private Context mContext;
    public EditMapNameDialog setMapNameDialog;
    public EditMapNameDialog editMapNameDialog;
    private FieldsDialog fieldsDialog;

    public Dialogs(Context context) {
        mContext = context;
    }

    /**
     * Show a dialog telling the user to turn gps on
     *
     * @param callback the listener that will be used to notify user interaction
     */
    public void showGpsNeededDialog(final ResultCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(mContext.getString(R.string.msg_enable_gps))
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                        mContext.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.dismiss();
                        Toast.makeText(mContext, mContext.getString(R.string.toast_gps_needed), Toast.LENGTH_LONG).show();
                        callback.returnResultToCaller(Activity.RESULT_CANCELED, LocationActivity.RESULT_EMPTY);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Callback telling the consumer when a new map name is set
     */
    public interface OnNameSetCallback {
        void onNameSet(String newName);
    }

    /**
     * Show a dialog telling the user to turn gps on
     *
     * @param initialName the initial name of the map, that is the .zip file name
     * @param callback    the listener that will be used to return the new name to the caller
     */
    public void showNameSettingDialog(String initialName, final OnNameSetCallback callback) {
        setMapNameDialog = new EditMapNameDialog(mContext, new View.OnClickListener() {
            public void onClick(View v) {
                if (v.getId() == R.id.btn_negative) {
                    setMapNameDialog.dismiss();
                } else if (v.getId() == R.id.btn_positive) {
                    String newName = v.getTag().toString();
                    if (newName.trim().isEmpty()) {
                        Toast.makeText(mContext, R.string.toast_empty_map_name, Toast.LENGTH_LONG).show();
                    } else {
                        setMapNameDialog.dismiss();
                        callback.onNameSet(newName);
                    }
                }
            }
        });
        setMapNameDialog.draw(initialName,
                R.string.title_enter_map_name,
                android.R.string.ok,
                android.R.string.cancel);
        setMapNameDialog.show();
    }

    /**
     * Shows a dialog for editing map name
     *
     * @param oldName  the old map name that will initially be displayed
     * @param callback the listener that will be used to return the new name to the caller
     */
    public void showEditMapNameDialog(final String oldName, final OnNameSetCallback callback) {
        editMapNameDialog = new EditMapNameDialog(mContext, new View.OnClickListener() {
            public void onClick(View v) {
                if (v.getId() == R.id.btn_negative) {
                    editMapNameDialog.dismiss();
                } else if (v.getId() == R.id.btn_positive) {
                    editMapNameDialog.dismiss();
                    String newName = v.getTag().toString();
                    if (newName.equals(oldName)) {
                        return;
                    }
                    String currentFields = AppPrefs.getFields(mContext, oldName);
                    AppPrefs.setFields(mContext, newName, currentFields);
                    AppPrefs.setFields(mContext, oldName, null);
                    boolean isChecked = AppPrefs.getMap(mContext) != null &&
                            AppPrefs.getMap(mContext).equals(oldName);
                    ModelUtils.renameMap(mContext, oldName, newName);
                    if (isChecked) {
                        AppPrefs.setMap(mContext, newName);
                    }
                    callback.onNameSet(newName);
                }
            }
        });
        editMapNameDialog.draw(oldName,
                R.string.title_edit_map_name,
                android.R.string.ok,
                android.R.string.cancel);
        editMapNameDialog.show();
    }

    /**
     * Shows the dialog of available fields list
     * Only the values of the selected fields will be returned to the caller app
     *
     * @param mapName the name of the map that's fields will be shown
     */
    public void showFieldsDialog(final String mapName) {
        showFieldsDialog(mapName, ModelUtils.getFields(mContext, mapName));
    }

    /**
     * Shows the dialog of available fields list
     * Only the values of the selected fields will be returned to the caller app
     *
     * @param mapName   the name of the map that's fields will be shown
     * @param shapeFile the shape file of the map that's fields will be shown
     */
    public void showFieldsDialog(final String mapName, final ShapeFile shapeFile) {
        showFieldsDialog(mapName, ModelUtils.getFields(shapeFile));
    }

    /**
     * Shows the dialog of available fields list
     * Only the values of the selected fields will be returned to the caller app
     *
     * @param mapName the name of the map that's fields will be shown
     * @param fields  the fields that will be listed in the dialog
     */
    public void showFieldsDialog(final String mapName, List<FieldItem> fields) {
        fieldsDialog = new FieldsDialog(mContext, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_negative) {
                    fieldsDialog.dismiss();
                } else if (v.getId() == R.id.btn_positive) {
                    fieldsDialog.dismiss();
                    String fields = v.getTag().toString();
                    AppPrefs.setFields(mContext, mapName, fields);
                }
            }
        });
        fieldsDialog.draw(fields,
                mapName,
                R.string.title_choose_fields,
                android.R.string.ok,
                android.R.string.cancel);
        fieldsDialog.show();
    }
}
