/*
 * Decompiled with CFR 0_118.
 */
package com.openDC.loclookup.model.shape_files.files.shp;

import com.openDC.loclookup.model.shape_files.files.shp.shapeTypes.ShpShape;
import com.openDC.loclookup.model.shape_files.shapeFile.ShapeFile;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;

public class SHP_Header {
    private ShapeFile parent_shapefile;
    private File file;
    private static final int SHP_MAGIC = 9994;
    private static final int SHP_VERSION = 1000;
    private int SHP_file_length;
    private int SHP_shape_type;
    private double[][] SHP_bbox = new double[3][2];
    private double[] SHP_range_m = new double[2];
    private ShpShape.Type shape_type = null;

    public SHP_Header(ShapeFile parent_shapefile, File file) {
        this.parent_shapefile = parent_shapefile;
        this.file = file;
    }

    public void read(ByteBuffer bb) throws Exception {
        bb.order(ByteOrder.BIG_ENDIAN);
        int SHP_MAGIC_read = bb.getInt(0);
        if (SHP_MAGIC_read != 9994) {
            throw new Exception("(ShapeFile) error: SHP_MAGIC = 9994, File: " + this.file);
        }
        this.SHP_file_length = bb.getInt(24);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        int SHP_version_read = bb.getInt(28);
        if (SHP_version_read != 1000) {
            throw new Exception("(ShapeFile) error: SHP_VERSION = 1000, File: " + this.file);
        }
        this.SHP_shape_type = bb.getInt(32);
        try {
            this.shape_type = ShpShape.Type.byID(this.SHP_shape_type);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.SHP_bbox[0][0] = bb.getDouble(36);
        this.SHP_bbox[1][0] = bb.getDouble(44);
        this.SHP_bbox[0][1] = bb.getDouble(52);
        this.SHP_bbox[1][1] = bb.getDouble(60);
        this.SHP_bbox[2][1] = bb.getDouble(68);
        this.SHP_bbox[2][1] = bb.getDouble(76);
        this.SHP_range_m[0] = bb.getDouble(84);
        this.SHP_range_m[1] = bb.getDouble(92);
        bb.position(100);
    }

    public ShpShape.Type getShapeType() {
        return this.shape_type;
    }

    public double[][] getBoundingBox() {
        return this.SHP_bbox;
    }

    public double[] getMeasureRange() {
        return this.SHP_range_m;
    }

    public int getFileLengthBytes() {
        return this.SHP_file_length;
    }

    public int getVersion() {
        return 1000;
    }

    public int getMagicNumber() {
        return 9994;
    }

    public void print() {
        System.out.printf(Locale.ENGLISH, "\n");
        System.out.printf(Locale.ENGLISH, "________________________< HEADER >________________________\n");
        System.out.printf(Locale.ENGLISH, "  FILE: \"%s\"\n", this.file.getName());
        System.out.printf(Locale.ENGLISH, "\n");
        System.out.printf(Locale.ENGLISH, "  SHP_MAGIC               = %d\n", 9994);
        System.out.printf(Locale.ENGLISH, "  SHP_file_length         = %d bytes\n", this.SHP_file_length * 2);
        System.out.printf(Locale.ENGLISH, "  SHP_VERSION             = %d\n", 1000);
        System.out.printf(Locale.ENGLISH, "  shape_type              = %s (%d)\n", this.shape_type, this.shape_type.ID());
        System.out.printf(Locale.ENGLISH, "  SHP_bbox: xmin, xmax    = %+7.3f, %+7.3f\n", this.SHP_bbox[0][0], this.SHP_bbox[0][1]);
        System.out.printf(Locale.ENGLISH, "  SHP_bbox: ymin, ymax    = %+7.3f, %+7.3f\n", this.SHP_bbox[1][0], this.SHP_bbox[1][1]);
        System.out.printf(Locale.ENGLISH, "  SHP_bbox: zmin, zmax    = %+7.3f, %+7.3f\n", this.SHP_bbox[2][0], this.SHP_bbox[2][1]);
        System.out.printf(Locale.ENGLISH, "  SHP_measure: mmin, mmax = %+7.3f, %+7.3f\n", this.SHP_range_m[0], this.SHP_range_m[1]);
        System.out.printf(Locale.ENGLISH, "________________________</HEADER >________________________\n");
        System.out.printf(Locale.ENGLISH, "\n");
    }
}

