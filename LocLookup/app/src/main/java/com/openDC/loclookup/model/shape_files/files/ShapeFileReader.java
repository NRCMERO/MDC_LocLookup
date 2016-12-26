/*
 * Decompiled with CFR 0_118.
 */
package com.openDC.loclookup.model.shape_files.files;

import com.openDC.loclookup.model.shape_files.shapeFile.ShapeFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class ShapeFileReader {
    protected ShapeFile parent_shapefile;
    protected File file;
    protected ByteBuffer bb;

    public ShapeFileReader(ShapeFile parent_shapefile, File file) throws IOException {
        this.parent_shapefile = parent_shapefile;
        this.file = file;
        this.bb = ShapeFileReader.loadFile(file);
    }

    public abstract void read() throws Exception;

    public abstract void printHeader();

    public abstract void printContent();

    public static ByteBuffer loadFile(File file) throws IOException {
        FileInputStream is = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(is);
        byte[] data = new byte[bis.available()];
        bis.read(data);
        bis.close();
        is.close();
        return ByteBuffer.wrap(data);
    }

    public ShapeFile getShapeFile() {
        return this.parent_shapefile;
    }

    public File getFile() {
        return this.file;
    }
}

