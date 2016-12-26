package com.openDC.loclookup.model;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ExtractionUtils {
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
}
