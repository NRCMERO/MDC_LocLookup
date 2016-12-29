package com.openDC.loclookup.model;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ExtractionUtils {
    public static final int RESULT_FILE_NOT_EXIST = 0;
    public static final int RESULT_OK = 1;
    public static final int RESULT_MISSING_FILES = 2;
    public static final int RESULT_TOO_MANY_FILES = 3;

    public static boolean extract(String filePath, String mapsDir, String mapName) {
        String targetDirectory = mapsDir + File.separator + mapName;
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }
        File target = new File(targetDirectory);
        target.mkdir();
        try {
            List<String> requiredTypes = Arrays.asList(ModelUtils.EXT_SHP, ModelUtils.EXT_SHX, ModelUtils.EXT_DBF);
            FileInputStream fin = new FileInputStream(filePath);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze;
            while ((ze = zin.getNextEntry()) != null) {
                if (!ze.isDirectory()) {
                    try {
                        String zeName = ze.getName();
                        String ext = zeName.length() < 3
                                ? zeName
                                : zeName.substring(zeName.length() - 3);
                        if (!requiredTypes.contains(ext)) {
                            continue;
                        }
                        String fileName = mapName + "." + ext;
                        String targetFilePath = targetDirectory + File.separator + fileName;
                        FileOutputStream fOut = new FileOutputStream(targetFilePath);
                        int size;
                        byte[] buffer = new byte[2048];
                        BufferedOutputStream bufferOut = new BufferedOutputStream(fOut, buffer.length);
                        while ((size = zin.read(buffer, 0, buffer.length)) != -1) {
                            bufferOut.write(buffer, 0, size);
                        }
                        bufferOut.flush();
                        bufferOut.close();
                        zin.closeEntry();
                        fOut.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            zin.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Validates the selected map file if contains the requires types
     *
     * @param filePath the full map file path
     * @return the code of the result
     * 0 if file does not exists
     * 1 if contains all the required files
     * 2 if one or more required files are missing
     * 3 if there were two or more files of the same extension
     */
    public static int validate(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return RESULT_FILE_NOT_EXIST;
        }
        try {
            List<String> requiredTypes = Arrays.asList(ModelUtils.EXT_SHP, ModelUtils.EXT_SHX, ModelUtils.EXT_DBF);
            Set<String> types = new HashSet<>();
            FileInputStream fin = new FileInputStream(filePath);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze;
            int total = 0;
            while ((ze = zin.getNextEntry()) != null) {
                if (!ze.isDirectory()) {
                    try {
                        String zeName = ze.getName();
                        String ext = zeName.length() < 3
                                ? zeName
                                : zeName.substring(zeName.length() - 3);
                        if (requiredTypes.contains(ext)) {
                            types.add(ext);
                            total++;
                        }
                        zin.closeEntry();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            zin.close();
            if (types.size() < requiredTypes.size()) {
                return RESULT_MISSING_FILES;
            } else if (total > requiredTypes.size()) {
                return RESULT_TOO_MANY_FILES;
            }
            return RESULT_OK;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return RESULT_FILE_NOT_EXIST;
    }
}
