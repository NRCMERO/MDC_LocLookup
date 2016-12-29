package com.openDC.loclookup.controller.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.openDC.loclookup.controller.interfaces.ResultCallback;
import com.openDC.loclookup.controller.utils.LocationUtils;
import com.openDC.loclookup.controller.utils.ShapeFileUtils;
import com.openDC.loclookup.model.AppPrefs;
import com.openDC.loclookup.model.ModelUtils;
import com.openDC.loclookup.view.dialogs.Dialogs;
import com.openDC.loclookup.view.dialogs.FieldsDialog;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import diewald_shapeFile.shapeFile.ShapeFile;
import loclookup.opendc.com.loclookup.R;

public class LocationActivity extends AppCompatActivity implements
        com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback {

    public static final long UPDATE_INTERVAL = 3000;
    public static final long FASTEST_INTERVAL = 3000;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 0;
    public static final int LOCATION_REQUEST_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY;
    public static final String EXTRA_VALUE = "value";
    public static final String EXTRA_RESULT = "result";
    public static final String RESULT_EMPTY = "";
    public static final String LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String PACKAGE_ODK = "org.koboc.collect.android";
    public static final String PACKAGE_MOBENZI = "mq.root";
    public static boolean FILTER_CALLING_PACKAGES = false;

    private Context mContext = this;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private FieldsDialog fieldsDialog;
    private Dialogs dialogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        initGoogleApiClient();
        dialogs = new Dialogs(mContext);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAndStart();
    }

    /**
     * Checks the calling package and the gps availability before requesting gps location updates
     */
    private void checkAndStart() {
        if (!checkCallingPackage()) {
            return;
        }
        checkAndConnect();
    }

    /**
     * Checks whether the gps is enabled
     * show a dialog telling the user to turn the gps on if it was turned off
     */
    private void checkAndConnect() {
        if (LocationUtils.isLocationEnabled(mContext)) {
            mGoogleApiClient.connect();
        } else {
            dialogs.showGpsNeededDialog(this);
        }
    }

    /**
     * Checks whether the calling package is white-listed
     * return to the caller if not
     *
     * @return whether calling package is authorized to use this tool
     */
    private boolean checkCallingPackage() {
        if (FILTER_CALLING_PACKAGES) {
            String callingPackage = getCallingPackage();
            if (callingPackage != null &&
                    !callingPackage.equals(PACKAGE_ODK) &&
                    !callingPackage.equals(PACKAGE_MOBENZI)) {
                Toast.makeText(mContext, getString(R.string.toast_unauthorized_app), Toast.LENGTH_LONG).show();
                returnResultToCaller(RESULT_CANCELED, RESULT_EMPTY);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (isGpsPermissionGranted()) {
            startLocationUpdates();
        } else {
            Toast.makeText(mContext, getString(R.string.toast_permission_denied), Toast.LENGTH_LONG).show();
            returnResultToCaller(RESULT_CANCELED, RESULT_EMPTY);
        }
    }

    /**
     * Initializes Google Api Client
     */
    private void initGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (checkGpsPermission()) {
            startLocationUpdates();
        }
    }

    /**
     * Checks and show permission request dialog if gps permission is still needed
     *
     * @return whether gps permission is granted
     */
    private boolean checkGpsPermission() {
        if (isGpsPermissionGranted()) {
            return true;
        }
        ActivityCompat.requestPermissions(this,
                new String[]{LOCATION_PERMISSION},
                LOCATION_PERMISSION_REQUEST_CODE);
        return false;
    }

    /**
     * Checks if the gps permission has been granted by the user
     *
     * @return whether gps permission is granted
     */
    private boolean isGpsPermissionGranted() {
        return (ActivityCompat.checkSelfPermission(mContext, LOCATION_PERMISSION) ==
                PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Starts fetching user's location
     */
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(mContext, LOCATION_PERMISSION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LOCATION_REQUEST_PRIORITY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        disconnectGoogleApiClient();
        lookForLocationName(location);
    }

    /**
     * Looks for the location in the map areas
     * Returns the result to the caller application
     *
     * @param location the location to look for
     */
    private void lookForLocationName(Location location) {
        mLocation = location;
        String selectedMap = AppPrefs.getMap(mContext);
        if (selectedMap == null) {
            Toast.makeText(mContext, getString(R.string.toast_no_map), Toast.LENGTH_LONG).show();
            returnResultToCaller(RESULT_CANCELED, RESULT_EMPTY);
            return;
        }
        File mapsDir = getExternalFilesDir(ModelUtils.DIR_MAPS);
        if (mapsDir == null) {
            returnResultToCaller(RESULT_CANCELED, RESULT_EMPTY);
            return;
        }
        String mapPath = mapsDir.getPath() + File.separator + selectedMap;
        ShapeFile shapeFile = null;
        try {
            shapeFile = new ShapeFile(mapPath, selectedMap);
            shapeFile.READ();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (shapeFile == null) {
            Toast.makeText(mContext, getString(R.string.toast_unrecognized_share_file), Toast.LENGTH_LONG).show();
            returnResultToCaller(RESULT_CANCELED, RESULT_EMPTY);
            return;
        }
        String[] address = ShapeFileUtils.getLocationAddress(shapeFile, location);
        if (address.length == 0) {
            Toast.makeText(mContext, getString(R.string.toast_location_outside), Toast.LENGTH_LONG).show();
            returnResultToCaller(Activity.RESULT_CANCELED, RESULT_EMPTY);
        } else {
            ModelUtils.fixRecords(address);
            String fields = AppPrefs.getFields(mContext, selectedMap);
            if (fields == null || fields.isEmpty()) {
                showFieldsDialog(selectedMap);
            } else {
                List<String> selectedFieldKeys = Arrays.asList(fields.split("[,]"));
                String result = ModelUtils.getLocationName(selectedFieldKeys, shapeFile.getDBF_field(), address);
                returnResultToCaller(Activity.RESULT_OK, result);
            }
        }
    }

    private void showFieldsDialog(final String mapName) {
        fieldsDialog = new FieldsDialog(mContext, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_negative) {
                    fieldsDialog.dismiss();
                    Toast.makeText(mContext, getString(R.string.toast_must_select_field), Toast.LENGTH_LONG).show();
                    returnResultToCaller(Activity.RESULT_CANCELED, RESULT_EMPTY);
                } else if (v.getId() == R.id.btn_positive) {
                    fieldsDialog.dismiss();
                    String fields = v.getTag().toString();
                    AppPrefs.setFields(mContext, mapName, fields);
                    lookForLocationName(mLocation);
                }
            }
        });
        fieldsDialog.draw(ModelUtils.getFields(mContext, mapName),
                mapName,
                R.string.title_choose_fields,
                android.R.string.ok,
                android.R.string.cancel);
        fieldsDialog.show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void returnResultToCaller(int responseCode, String result) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_VALUE, result);
        intent.putExtra(EXTRA_RESULT, result);
        setResult(responseCode, intent);
        finish();
    }

    @Override
    protected void onStop() {
        disconnectGoogleApiClient();
        super.onStop();
    }

    /**
     * Stop getting location updates
     */
    private void disconnectGoogleApiClient() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
}