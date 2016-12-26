/*
 * Decompiled with CFR 0_118.
 */
package com.openDC.loclookup.model.shape_files.shapeFile;

import com.openDC.loclookup.model.shape_files.files.dbf.DBF_Field;
import com.openDC.loclookup.model.shape_files.files.dbf.DBF_File;
import com.openDC.loclookup.model.shape_files.files.shp.SHP_File;
import com.openDC.loclookup.model.shape_files.files.shp.shapeTypes.ShpShape;
import com.openDC.loclookup.model.shape_files.files.shx.SHX_File;

import java.io.File;
import java.util.ArrayList;

public class ShapeFile {
    public static final String _LIBRARY_NAME = "diewald_shapeFileReader";
    public static final String _LIBRARY_VERSION = "1.0";
    public static final String _LIBRARY_AUTHOR = "Thomas Diewald";
    public static final String _LIBRARY_LAST_EDIT = "2012.04.09";
    private SHX_File shx_file;
    private DBF_File dbf_file;
    private SHP_File shp_file;

    public ShapeFile(String path, String filename) throws Exception {
        File dir = new File(path);
        this.shx_file = new SHX_File(this, new File(dir, String.valueOf(filename) + ".shx"));
        this.dbf_file = new DBF_File(this, new File(dir, String.valueOf(filename) + ".dbf"));
        this.shp_file = new SHP_File(this, new File(dir, String.valueOf(filename) + ".shp"));
    }

    public ShapeFile READ() throws Exception {
        this.shx_file.read();
        this.dbf_file.read();
        this.shp_file.read();
        return this;
    }

    public SHX_File getFile_SHX() {
        return this.shx_file;
    }

    public DBF_File getFile_DBF() {
        return this.dbf_file;
    }

    public SHP_File getFile_SHP() {
        return this.shp_file;
    }

    public int getSHP_shapeCount() {
        return this.shp_file.getShpShapes().size();
    }

    public ArrayList<ShpShape> getSHP_shape() {
        return this.shp_file.getShpShapes();
    }

    public ShpShape getSHP_shape(int index) {
        return this.getSHP_shape().get(index);
    }

    public ShpShape.Type getSHP_shapeType() {
        return this.shp_file.getHeader().getShapeType();
    }

    public double[][] getSHP_boundingBox() {
        return this.shp_file.getHeader().getBoundingBox();
    }

    public int getDBF_fieldCount() {
        return this.dbf_file.getFields().length;
    }

    public DBF_Field[] getDBF_field() {
        return this.dbf_file.getFields();
    }

    public DBF_Field getDBF_field(int index) {
        return this.dbf_file.getFields()[index];
    }

    public int getDBF_recordCount() {
        return this.dbf_file.getContent().length;
    }

    public String[][] getDBF_record() {
        return this.dbf_file.getContent();
    }

    public String[] getDBF_record(int index) {
        return this.dbf_file.getContent()[index];
    }

    public String getDBF_record(int row, int col) {
        return this.dbf_file.getContent()[row][col];
    }
}

