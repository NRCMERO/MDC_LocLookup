package com.openDC.loclookup.controller.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.listable.EasyAdapter;
import com.listable.ListItem;
import com.listable.OnItemClickListener;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.openDC.loclookup.model.AppPrefs;
import com.openDC.loclookup.model.ExtractionUtils;
import com.openDC.loclookup.model.ModelUtils;
import com.openDC.loclookup.model.StringUtils;
import com.openDC.loclookup.model.vo.MapItem;
import com.openDC.loclookup.model.vo.MapPopupItem;
import com.openDC.loclookup.view.custom.DroppyMapMenuItem;
import com.openDC.loclookup.view.dialogs.Dialogs;
import com.shehabic.droppy.DroppyClickCallbackInterface;
import com.shehabic.droppy.DroppyMenuPopup;
import com.shehabic.droppy.animations.DroppyScaleAnimation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import loclookup.opendc.com.loclookup.R;

public class MainActivity extends BaseActivity implements
        OnItemClickListener.OnItemClickCallback,
        DroppyClickCallbackInterface {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int STORAGE_PERMISSION_REQUEST_CODE = 0;
    public static final int FILE_BROWSER_REQUEST_CODE = 1;

    private Context mContext = this;
    private String selectedFilePath;
    private EasyAdapter mapsAdapter;
    private RecyclerView mapsRecyclerView;
    private Dialogs dialogs;
    private DroppyMenuPopup droppyMenu;
    private MapItem popupMapItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setActionbarTitle(R.string.app_name);
        prepareMapsRecyclerView();
        dialogs = new Dialogs(mContext);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {
            if (checkStoragePermission()) {
                openFileBrowser();
            }
        }
        return true;
    }

    /**
     * Prepares the list of maps to include all available and valid map folders
     */
    private void prepareMapsRecyclerView() {
        if (mapsRecyclerView == null) {
            mapsRecyclerView = (RecyclerView) findViewById(R.id.rv);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mContext,
                    DividerItemDecoration.VERTICAL);
            mapsRecyclerView.addItemDecoration(dividerItemDecoration);
        }

        List<ListItem> maps = new ArrayList<>();
        List<String> availableMaps = ModelUtils.getAvailableMaps(mContext);
        for (String map : availableMaps) {
            maps.add(new MapItem(map));
        }
        String selectedMap = AppPrefs.getMap(this);
        int selectedIndex = availableMaps.indexOf(selectedMap);
        if (selectedIndex == -1) {
            if (availableMaps.isEmpty()) {
                mapsAdapter = new EasyAdapter(mContext, new ArrayList<ListItem>(), this);
                mapsRecyclerView.setAdapter(mapsAdapter);
                mapsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            } else {
                selectedIndex = 0;
            }
        }
        if (selectedIndex != -1) {
            ((MapItem) maps.get(selectedIndex)).isChecked = true;
            mapsAdapter = new EasyAdapter(mContext, maps, this);
            mapsRecyclerView.setAdapter(mapsAdapter);
            mapsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            AppPrefs.setMap(mContext, availableMaps.get(selectedIndex));
        }
        refreshWelcomeMessage();
    }

    /**
     * Adds a new map to the list
     *
     * @param mapName the name of the map to be added
     */
    private void add(String mapName) {
        if (selectedFilePath == null || selectedFilePath.isEmpty()) {
            return;
        }
        File mapsDir = getExternalFilesDir(ModelUtils.DIR_MAPS);
        if (mapsDir == null) {
            return;
        }
        int preCount = ModelUtils.getAvailableMaps(mContext).size();
        ExtractionUtils.extract(selectedFilePath, mapsDir.getPath(), mapName);
        boolean isValid = ModelUtils.validate(mContext, mapName);
        if (!isValid) {
            ModelUtils.deleteMap(mContext, mapName);
            Toast.makeText(mContext, R.string.toast_zip_file_invalid_shape_files, Toast.LENGTH_LONG).show();
            return;
        }
        int postCount = ModelUtils.getAvailableMaps(mContext).size();
        if (postCount > preCount) {
            AppPrefs.setMap(mContext, mapName);
            dialogs.showFieldsDialog(mapName);
            Toast.makeText(mContext, R.string.toast_map_added, Toast.LENGTH_LONG).show();
            prepareMapsRecyclerView();
        }
    }

    /**
     * Opens file browser to pick a map file with .zip extension
     */
    private void openFileBrowser() {
        new MaterialFilePicker()
                .withActivity(this)
                .withRequestCode(FILE_BROWSER_REQUEST_CODE)
                .withFilter(Pattern.compile(".*\\.zip$"))
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean picked = requestCode == FILE_BROWSER_REQUEST_CODE && resultCode == RESULT_OK;
        if (!picked) {
            return;
        }
        String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
        if (!filePath.endsWith(".zip")) {
            Toast.makeText(mContext, R.string.toast_zip_file_not_found, Toast.LENGTH_LONG).show();
            return;
        }
        selectedFilePath = filePath;
        int filesResultCode = ExtractionUtils.validate(selectedFilePath);
        switch (filesResultCode) {
            case ExtractionUtils.RESULT_OK: {
                dialogs.showNameSettingDialog(StringUtils.getBaseName(filePath), new Dialogs.OnNameSetCallback() {
                    @Override
                    public void onNameSet(String newName) {
                        add(newName);
                    }
                });
                break;
            }
            case ExtractionUtils.RESULT_FILE_NOT_EXIST: {
                Toast.makeText(mContext, R.string.toast_zip_file_not_found, Toast.LENGTH_LONG).show();
                break;
            }
            case ExtractionUtils.RESULT_MISSING_FILES: {
                Toast.makeText(mContext, R.string.toast_zip_file_missing_files, Toast.LENGTH_LONG).show();
                break;
            }
            case ExtractionUtils.RESULT_TOO_MANY_FILES: {
                Toast.makeText(mContext, R.string.toast_zip_file_duplicate_files, Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (isStoragePermissionGranted()) {
            openFileBrowser();
        }
    }

    /**
     * Check and show permission request dialog if storage permission is still needed
     *
     * @return whether storage permission is granted
     */
    private boolean checkStoragePermission() {
        if (isStoragePermissionGranted()) {
            return true;
        }
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_REQUEST_CODE);
        return false;
    }

    /**
     * Check if the storage permission has been granted by the user
     *
     * @return whether storage permission is granted
     */
    private boolean isStoragePermissionGranted() {
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onItemClicked(View view, ListItem listableItem, int position) {
        final MapItem clickedMapItem = (MapItem) listableItem;
        for (ListItem listItem : mapsAdapter.getItems()) {
            if (listItem instanceof MapItem) {
                MapItem mapItem = (MapItem) listItem;
                mapItem.isChecked = mapItem.equals(clickedMapItem);
                if (mapItem.isChecked) {
                    AppPrefs.setMap(mContext, mapItem.mapName);
                }
            }
        }
        mapsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemLongClicked(View view, ListItem listableItem, int position) {
        showPopup(view, listableItem);
    }

    /**
     * Shows a pop-up menu under the clicked view
     *
     * @param view the view under which the menu will appear
     * @param item the model of the clicked view
     */
    private void showPopup(View view, ListItem item) {
        if (droppyMenu != null) {
            droppyMenu.dismiss(false);
        }
        this.popupMapItem = (MapItem) item;
        Log.i(TAG, "Showing popup: " + popupMapItem.mapName);
        DroppyMenuPopup.Builder droppyBuilder = new DroppyMenuPopup.Builder(this, view);
        MapPopupItem item1 = new MapPopupItem(getString(R.string.popup_edit_name),
                R.drawable.ic_edit_name,
                DroppyMapMenuItem.ID_EDIT_NAME);
        droppyBuilder.addMenuItem(new DroppyMapMenuItem(R.layout.popup_map, item1))
                .addSeparator();
        MapPopupItem item2 = new MapPopupItem(getString(R.string.popup_edit_fields),
                R.drawable.ic_edit_fields,
                DroppyMapMenuItem.ID_EDIT_FIELDS);
        droppyBuilder.addMenuItem(new DroppyMapMenuItem(R.layout.popup_map, item2))
                .addSeparator();
        MapPopupItem item3 = new MapPopupItem(getString(R.string.popup_delete_map),
                R.drawable.ic_delete,
                DroppyMapMenuItem.ID_DELETE_MAP);
        droppyBuilder.addMenuItem(new DroppyMapMenuItem(R.layout.popup_map, item3));
        droppyBuilder.setPopupAnimation(new DroppyScaleAnimation());
        droppyBuilder.setOnClick(this);
        droppyBuilder.setOnDismissCallback(new DroppyMenuPopup.OnDismissCallback() {
            @Override
            public void call() {
                mapsRecyclerView.setAdapter(mapsAdapter);
            }
        });
        droppyMenu = droppyBuilder.build();
        droppyMenu.show();
    }

    @Override
    public void call(View v, int id) {
        switch (id) {
            case DroppyMapMenuItem.ID_EDIT_NAME: {
                dialogs.showEditMapNameDialog(popupMapItem.mapName, new Dialogs.OnNameSetCallback() {
                    @Override
                    public void onNameSet(String newName) {
                        popupMapItem.mapName = newName;
                        prepareMapsRecyclerView();
                    }
                });
                Log.i("Pop", "ID_EDIT_NAME " + popupMapItem.mapName);
                break;
            }
            case DroppyMapMenuItem.ID_EDIT_FIELDS: {
                dialogs.showFieldsDialog(popupMapItem.mapName);
                Log.i("Pop", "ID_EDIT_FIELDS " + popupMapItem.mapName);
                break;
            }
            case DroppyMapMenuItem.ID_DELETE_MAP: {
                AppPrefs.setFields(mContext, popupMapItem.mapName, null);
                ModelUtils.deleteMap(mContext, popupMapItem.mapName);
                prepareMapsRecyclerView();
                Log.i("Pop", "ID_DELETE_MAP " + popupMapItem.mapName);
                break;
            }
        }
    }

    /**
     * Updates welcome message visibility, show message is maps list is empty, hide otherwise
     */
    private void refreshWelcomeMessage() {
        findViewById(R.id.txt_welcome).setVisibility(mapsAdapter == null || mapsAdapter.empty()
                ? View.VISIBLE
                : View.GONE);
    }
}
