package com.openDC.loclookup.model;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ModelUtils {
    private static final String TAG = ModelUtils.class.getSimpleName();
    public static final String DIR_MAPS = "maps";
    public static final String MAP_SOMALIA = "somalia";
    public static final String FILE_NAME = "district-boundaries_polygon";

    /**
     * copy map shape files from the assets to the internal storage
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
     * copy the file from input to output stream
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

    /**
     * get the names of already copied map files
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
}
