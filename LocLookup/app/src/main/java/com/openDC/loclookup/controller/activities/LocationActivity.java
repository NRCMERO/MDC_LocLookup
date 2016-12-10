package com.openDC.loclookup.controller.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.openDC.loclookup.controller.interfaces.ResultCallback;
import com.openDC.loclookup.controller.utils.ShapeFileUtils;
import com.openDC.loclookup.model.ModelUtils;
import com.openDC.loclookup.view.Dialogs;

import java.io.File;

import diewald_shapeFile.shapeFile.ShapeFile;
import loclookup.opendc.com.loclookup.R;

public class LocationActivity extends Activity implements
        com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback {

    public static final long UPDATE_INTERVAL = 3000;
    public static final long FASTEST_INTERVAL = 3000;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 0;
    public static final String EXTRA_VALUE = "value";
    public static final String EXTRA_RESULT = "result";
    public static final String RESULT_EMPTY = "";
    public static final String PACKAGE_ODK = "org.koboc.collect.android";
    public static final String PACKAGE_MOBENZI = "mq.root";
    public static boolean FILTER_CALLING_PACKAGES = false;

    private Context mContext = this;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        initGoogleApiClient();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAndStart();
    }

    /**
     * check the calling package and the gps availability before requesting gps location updates
     */
    private void checkAndStart() {
        if (!checkCallingPackage()) {
            return;
        }
        checkAndConnect();
    }

    /**
     * check whether the gps is enabled
     * show a dialog telling the user to turn the gps on if it was turned off
     */
    private void checkAndConnect() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mGoogleApiClient.connect();
        } else {
            Dialogs.showGpsNeededDialog(mContext, this);
        }
    }

    /**
     * check whether the calling package is white-listed
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
     * initialize Google Api Client
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
     * check and show permission request dialog if gps permission is still needed
     *
     * @return whether gps permission is granted
     */
    private boolean checkGpsPermission() {
        if (!isGpsPermissionGranted()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    /**
     * check if the gps permission has been granted by the user
     *
     * @return whether gps permission is granted
     */
    private boolean isGpsPermissionGranted() {
        return (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * start fetching user's location
     */
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationRequest mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_LOW_POWER)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
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
     * looks for the location in the map areas
     * returns the result to the caller application
     *
     * @param location the location to look for
     */
    private void lookForLocationName(Location location) {
        ModelUtils.copyMapFiles(mContext, ModelUtils.MAP_SOMALIA);
        File mapsDir = getExternalFilesDir(ModelUtils.DIR_MAPS);
        if (mapsDir == null) {
            returnResultToCaller(RESULT_CANCELED, RESULT_EMPTY);
            return;
        }
        String mapPath = mapsDir.getPath() + File.separator + ModelUtils.MAP_SOMALIA;
        ShapeFile shapeFile = null;
        try {
            shapeFile = new ShapeFile(mapPath, ModelUtils.FILE_NAME);
            shapeFile.READ();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (shapeFile == null) {
            Toast.makeText(mContext, getString(R.string.toast_unrecognized_share_file), Toast.LENGTH_LONG).show();
            returnResultToCaller(RESULT_CANCELED, RESULT_EMPTY);
            return;
        }
        String locationName = ShapeFileUtils.getLocationName(shapeFile, location);
        if (locationName.trim().isEmpty()) {
            Toast.makeText(mContext, getString(R.string.toast_location_outside), Toast.LENGTH_LONG).show();
            returnResultToCaller(Activity.RESULT_CANCELED, RESULT_EMPTY);
        } else {
            returnResultToCaller(Activity.RESULT_OK, locationName);
        }
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
     * stops getting location updates
     */
    private void disconnectGoogleApiClient() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
}
