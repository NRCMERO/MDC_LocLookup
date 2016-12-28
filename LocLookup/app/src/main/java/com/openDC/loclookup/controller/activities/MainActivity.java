package com.openDC.loclookup.controller.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
import com.openDC.loclookup.view.EasyAdapter;
import com.openDC.loclookup.view.FontUtils;
import com.openDC.loclookup.view.custom.DroppyMapMenuItem;
import com.openDC.loclookup.view.dialogs.EditMapNameDialog;
import com.openDC.loclookup.view.dialogs.FieldsDialog;
import com.shehabic.droppy.DroppyClickCallbackInterface;
import com.shehabic.droppy.DroppyMenuPopup;
import com.shehabic.droppy.animations.DroppyScaleAnimation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import loclookup.opendc.com.loclookup.R;

public class MainActivity extends AppCompatActivity
        implements OnItemClickListener.OnItemClickCallback,
        DroppyClickCallbackInterface {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int STORAGE_PERMISSION_REQUEST_CODE = 0;
    public static final int FILE_BROWSER_REQUEST_CODE = 1;

    private Context mContext = this;
    private String selectedFilePath;
    private EasyAdapter mapsAdapter;
    private FieldsDialog fieldsDialog;
    private RecyclerView mapsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setActionbarTitle(R.string.app_name);
        prepareMapsRecyclerView();
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

    private void extract(String mapName) {
        if (selectedFilePath == null || selectedFilePath.isEmpty()) {
            return;
        }
        File mapsDir = getExternalFilesDir(ModelUtils.DIR_MAPS);
        if (mapsDir == null) {
            return;
        }
        int preCount = ModelUtils.getAvailableMaps(mContext).size();
        ExtractionUtils.extract(selectedFilePath, mapsDir.getPath(), mapName);
        int postCount = ModelUtils.getAvailableMaps(mContext).size();
        if (postCount > preCount) {
            AppPrefs.setMap(mContext, mapName);
            showFieldsDialog(mapName);
            Toast.makeText(mContext, R.string.toast_map_added, Toast.LENGTH_LONG).show();
            prepareMapsRecyclerView();
        }
    }

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
        if (requestCode == FILE_BROWSER_REQUEST_CODE && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            if (filePath.endsWith(".zip")) {
                selectedFilePath = filePath;
                int filesResultCode = ExtractionUtils.validate(selectedFilePath);
                switch (filesResultCode) {
                    case ExtractionUtils.RESULT_OK: {
                        showNameSettingDialog(StringUtils.getBaseName(filePath));
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
        }
    }

    private void showNameSettingDialog(String initialName) {
        editMapNameDialog = new EditMapNameDialog(mContext, new View.OnClickListener() {
            public void onClick(View v) {
                if (v.getId() == R.id.btn_negative) {
                    editMapNameDialog.dismiss();
                } else if (v.getId() == R.id.btn_positive) {
                    String newName = v.getTag().toString();
                    if (newName.trim().isEmpty()) {
                        Toast.makeText(mContext, R.string.toast_empty_map_name, Toast.LENGTH_LONG).show();
                    } else {
                        editMapNameDialog.dismiss();
                        extract(newName);
                    }
                }
            }
        });
        editMapNameDialog.draw(initialName,
                R.string.title_enter_map_name,
                android.R.string.ok,
                android.R.string.cancel);
        editMapNameDialog.show();
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
        initDroppyMenu(view, listableItem);
    }

    private DroppyMenuPopup droppyMenu;
    private MapItem popupMapItem;

    private void initDroppyMenu(View v, ListItem listableItem) {
        if (droppyMenu != null) {
            droppyMenu.dismiss(false);
        }
        this.popupMapItem = (MapItem) listableItem;
        Log.i(TAG, "Showing popup: " + popupMapItem.mapName);
        DroppyMenuPopup.Builder droppyBuilder = new DroppyMenuPopup.Builder(this, v);
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

    private EditMapNameDialog editMapNameDialog;

    @Override
    public void call(View v, int id) {
        switch (id) {
            case DroppyMapMenuItem.ID_EDIT_NAME: {
                onEditMapNameClicked();
                Log.i("Pop", "ID_EDIT_NAME " + popupMapItem.mapName);
                break;
            }
            case DroppyMapMenuItem.ID_EDIT_FIELDS: {
                showFieldsDialog(popupMapItem.mapName);
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

    private void onEditMapNameClicked() {
        editMapNameDialog = new EditMapNameDialog(mContext, new View.OnClickListener() {
            public void onClick(View v) {
                if (v.getId() == R.id.btn_negative) {
                    editMapNameDialog.dismiss();
                } else if (v.getId() == R.id.btn_positive) {
                    editMapNameDialog.dismiss();
                    String oldName = popupMapItem.mapName;
                    String newName = v.getTag().toString();
                    String currentFields = AppPrefs.getFields(mContext, oldName);
                    AppPrefs.setFields(mContext, newName, currentFields);
                    AppPrefs.setFields(mContext, oldName, null);
                    boolean isChecked = AppPrefs.getMap(mContext) != null &&
                            AppPrefs.getMap(mContext).equals(oldName);
                    ModelUtils.renameMap(mContext, oldName, newName);
                    if (isChecked) {
                        AppPrefs.setMap(mContext, newName);
                    }
                    popupMapItem.mapName = newName;
                    prepareMapsRecyclerView();
                }
            }
        });
        editMapNameDialog.draw(popupMapItem.mapName,
                R.string.title_edit_map_name,
                android.R.string.ok,
                android.R.string.cancel);
        editMapNameDialog.show();
    }

    private void showFieldsDialog(final String mapName) {
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
        fieldsDialog.draw(ModelUtils.getFields(mContext, mapName),
                mapName,
                R.string.title_choose_fields,
                android.R.string.ok,
                android.R.string.cancel);
        fieldsDialog.show();
    }

    public void setActionbarTitle(int titleResId) {
        setActionbarTitle(getString(titleResId));
    }

    public void setActionbarTitle(String title) {
        if (getSupportActionBar() != null) {
            SpannableString spannableString = new SpannableString(title);
            spannableString.setSpan(new com.openDC.loclookup.view.TypefaceSpan(this,
                            FontUtils.CustomFont.ARABIA_REGULAR.getFontName()),
                    0, spannableString.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            getSupportActionBar().setTitle(spannableString);
        }
    }

    private void refreshWelcomeMessage() {
        findViewById(R.id.txt_welcome).setVisibility(mapsAdapter == null || mapsAdapter.empty()
                ? View.VISIBLE
                : View.GONE);
    }
}
