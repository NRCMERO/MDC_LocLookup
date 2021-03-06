package com.openDC.loclookup.model;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import com.openDC.loclookup.model.vo.FieldItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import diewald_shapeFile.files.dbf.DBF_Field;
import diewald_shapeFile.shapeFile.ShapeFile;

public class ModelUtils {
    private static final String TAG = ModelUtils.class.getSimpleName();
    public static final String DIR_MAPS = "maps";
    public static final String EXT_SHX = "shx";
    public static final String EXT_DBF = "dbf";
    public static final String EXT_SHP = "shp";

    /**
     * Get the names of the existing map files
     *
     * @param context the context that will be used to access the storage
     * @param mapName the map which file names will be returned
     * @return map copied file names
     */
    private static List<String> getFileNames(Context context, String mapName) {
        List<String> fileNames = new ArrayList<>();
        File mapsDir = context.getExternalFilesDir(DIR_MAPS);
        if (mapsDir == null) {
            return fileNames;
        }
        String baseMapsDir = mapsDir.getPath();
        File mapDir = new File(baseMapsDir + File.separator + mapName);
        mapDir.mkdir();
        for (File file : mapDir.listFiles()) {
            fileNames.add(file.getName());
        }
        return fileNames;
    }

    /**
     * Fetches available map names
     *
     * @param context the context that will be used to access external storage
     * @return map names
     */
    public static List<String> getAvailableMaps(Context context) {
        List<String> fileNames = new ArrayList<>();
        File mapsDir = context.getExternalFilesDir(DIR_MAPS);
        if (mapsDir == null) {
            return fileNames;
        }
        for (File file : mapsDir.listFiles()) {
            if (isValidMapDirectory(file)) {
                fileNames.add(file.getName());
            }
        }
        return fileNames;
    }

    /**
     * Deletes map files
     *
     * @param context the context that will be used to access external storage
     * @param mapName the name of the map to be deleted
     */
    public static void deleteMap(Context context, String mapName) {
        File mapsDir = context.getExternalFilesDir(DIR_MAPS);
        if (mapsDir == null) {
            return;
        }
        File mapDir = new File(mapsDir.getPath() + File.separator + mapName);
        deleteDirectory(mapDir);
    }

    /**
     * Completely deleted the map directory with all sub-files
     *
     * @param file the directory to be deleted
     */
    private static void deleteDirectory(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File each : files) {
                    if (each.isDirectory()) {
                        deleteDirectory(each);
                    } else {
                        each.delete();
                    }
                }
            }
            file.delete();
        }
    }

    /**
     * Checks if the map is valid and all map files exist
     *
     * @param mapDirectory the map directory to check
     * @return true if the map is valid and map files exist
     */
    private static boolean isValidMapDirectory(File mapDirectory) {
        if (!mapDirectory.isDirectory()) {
            return false;
        }
        List<String> requiredTypes = Arrays.asList(ModelUtils.EXT_SHP, ModelUtils.EXT_SHX, ModelUtils.EXT_DBF);
        File[] files = mapDirectory.listFiles();
        Set<String> extensions = new HashSet<>();
        for (File file : files) {
            String filename = file.getName();
            String ext = filename.length() < 3
                    ? filename
                    : filename.substring(filename.length() - 3);
            if (requiredTypes.contains(ext)) {
                extensions.add(ext);
            }
        }
        return extensions.size() == requiredTypes.size();
    }

    /**
     * Fixes area records' encoding
     *
     * @param record area records
     */
    public static void fixRecords(String[] record) {
        for (int i = 0; i < record.length; i++) {
            try {
                record[i] = new String(record[i].getBytes("ISO-8859-1"), "UTF-8");
                record[i] = record[i].trim().replace("\n", " ");
            } catch (Exception e) {
                Log.e("Encoding", "Couldn't encode field: " + record[i]);
            }
        }
    }

    /**
     * Fetches the location address (fields values)
     *
     * @param selectedFieldKeys the fields selected by the user
     * @param fields            the data available for that area
     * @param record            the records of the area
     * @return
     */
    public static String getLocationName(List<String> selectedFieldKeys, DBF_Field[] fields, String[] record) {
        if (fields.length != record.length) {
            return null;
        }
        List<DBF_Field> fieldsList = Arrays.asList(fields);
        List<String> locationParts = new ArrayList<>();
        for (DBF_Field field : fields) {
            String fieldName = field.getName();
            if (selectedFieldKeys.contains(fieldName)) {
                int index = fieldsList.indexOf(field);
                locationParts.add(record[index]);
            }
        }
        return TextUtils.join(";", locationParts);
    }

    /**
     * Fetches field items for a random area
     *
     * @param context the context tht will be used to read map file
     * @param mapName the name of the map to fetch its fields
     * @return the field items of a random area in the given map
     */
    public static List<FieldItem> getFields(Context context, String mapName) {
        File mapsDir = context.getExternalFilesDir(ModelUtils.DIR_MAPS);
        String mapPath = mapsDir.getPath() + File.separator + mapName;
        ShapeFile shapeFile;
        try {
            shapeFile = new ShapeFile(mapPath, mapName);
            shapeFile.READ();
            int randomSample = new Random().nextInt(shapeFile.getDBF_record().length);
            String[] record = shapeFile.getDBF_record()[randomSample];
            if (shapeFile.getDBF_field().length == record.length) {
                ModelUtils.fixRecords(record);
                List<FieldItem> result = new ArrayList<>();
                for (int i = 0; i < shapeFile.getDBF_field().length; i++) {
                    DBF_Field field = shapeFile.getDBF_field()[i];
                    FieldItem fieldItem = new FieldItem(field.getName(), record[i]);
                    result.add(fieldItem);
                }
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return new ArrayList<>();
    }

    /**
     * Fetches field items for a random area having the shape file
     *
     * @param shapeFile the shape file to get fields from
     * @return the field items of a random area in the given map
     */
    public static List<FieldItem> getFields(ShapeFile shapeFile) {
        int randomSample = new Random().nextInt(shapeFile.getDBF_record().length);
        String[] record = shapeFile.getDBF_record()[randomSample];
        if (shapeFile.getDBF_field().length != record.length) {
            return new ArrayList<>();
        }
        ModelUtils.fixRecords(record);
        List<FieldItem> result = new ArrayList<>();
        for (int i = 0; i < shapeFile.getDBF_field().length; i++) {
            DBF_Field field = shapeFile.getDBF_field()[i];
            FieldItem fieldItem = new FieldItem(field.getName(), record[i]);
            result.add(fieldItem);
        }
        return result;
    }

    /**
     * Gets the shape file for a specified map
     *
     * @param context the context tht will be used to read map file
     * @param mapName the name of the map to fetch its shape file
     * @return the shape file for the specified map, or null if failed
     */
    public static ShapeFile readShapeFile(Context context, String mapName) {
        File mapsDir = context.getExternalFilesDir(ModelUtils.DIR_MAPS);
        String mapPath = mapsDir.getPath() + File.separator + mapName;
        ShapeFile shapeFile;
        try {
            shapeFile = new ShapeFile(mapPath, mapName);
            shapeFile.READ();
            return shapeFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Changes the name of the given map (oldName) to a new one
     * All sub-files will be renamed to the new name as well
     *
     * @param context the context that will be needed to access external storage
     * @param oldName the old map name
     * @param newName the new map name
     * @return true if renamed successfully
     */
    public static boolean renameMap(Context context, String oldName, String newName) {
        File mapsDir = context.getExternalFilesDir(ModelUtils.DIR_MAPS);
        if (mapsDir == null) {
            return false;
        }
        File mapFile = new File(mapsDir.getPath() + File.separator + oldName);
        File newFile = new File(mapsDir.getPath() + File.separator + newName);
        boolean result = mapFile.renameTo(newFile);
        for (File subFile : newFile.listFiles()) {
            String subFileName = subFile.getName();
            String extension = subFileName.substring(subFileName.lastIndexOf("."));
            String newSubFileName = newName + extension;
            File newSubFile = new File(newFile.getPath() + File.separator + newSubFileName);
            result &= subFile.renameTo(newSubFile);
        }
        return result;
    }

    /**
     * Copy map shape files from the assets to the internal storage
     *
     * @param context the context which will be needed to get the assets
     * @param mapName the name of the map which files will be copied
     */
    public static void copyMapFiles(Context context, String mapName) {
        AssetManager assetManager = context.getAssets();
        String assetsMapDirectory = DIR_MAPS + File.separator + mapName;
        File mapsDir = context.getExternalFilesDir(DIR_MAPS);
        if (mapsDir == null) {
            return;
        }
        String baseMapsDir = mapsDir.getPath();
        File mapDir = new File(baseMapsDir + File.separator + mapName);
        mapDir.mkdir();
        String[] files = null;
        try {
            files = assetManager.list(assetsMapDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (files == null) {
            return;
        }
        List<String> existingFileNames = ModelUtils.getFileNames(context, mapName);
        for (String filename : files) {
            if (existingFileNames.contains(filename)) {
                Log.i(TAG, "Already exists: " + filename);
                continue;
            }
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(assetsMapDirectory + File.separator + filename);
                File outFile = new File(mapDir, filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
                Log.i(TAG, "Copied: " + filename);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Copy a file from input to output stream
     *
     * @param in  the input stream to copy from
     * @param out the output stream to copy to
     */
    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}
