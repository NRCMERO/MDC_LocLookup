/*
 * Decompiled with CFR 0_118.
 */
package com.openDC.loclookup.model.shape_files.files.shp.shapeTypes;

import java.nio.ByteBuffer;
import java.util.Locale;

public class ShpPolygon extends ShpShape {
    private double[][] SHP_bbox = new double[3][2];
    private double[] SHP_range_m = new double[2];
    private int SHP_num_parts;
    private int SHP_num_points;
    private int[] SHP_parts;
    private double[][] SHP_xyz_points;
    private double[] SHP_m_values;
    private double[][][] parts = null;

    public ShpPolygon(ShpShape.Type shape_type) {
        super(shape_type);
    }

    @Override
    protected void readRecordContent(ByteBuffer bb) {
        this.SHP_bbox[0][0] = bb.getDouble();
        this.SHP_bbox[1][0] = bb.getDouble();
        this.SHP_bbox[0][1] = bb.getDouble();
        this.SHP_bbox[1][1] = bb.getDouble();
        this.SHP_num_parts = bb.getInt();
        this.SHP_num_points = bb.getInt();
        this.SHP_parts = new int[this.SHP_num_parts];
        int i = 0;
        while (i < this.SHP_num_parts) {
            this.SHP_parts[i] = bb.getInt();
            ++i;
        }
        this.SHP_xyz_points = new double[this.SHP_num_points][3];
        i = 0;
        while (i < this.SHP_num_points) {
            this.SHP_xyz_points[i][0] = bb.getDouble();
            this.SHP_xyz_points[i][1] = bb.getDouble();
            ++i;
        }
        if (this.shape_type.hasZvalues()) {
            this.SHP_bbox[2][0] = bb.getDouble();
            this.SHP_bbox[2][1] = bb.getDouble();
            i = 0;
            while (i < this.SHP_num_points) {
                this.SHP_xyz_points[i][2] = bb.getDouble();
                ++i;
            }
        }
        if (this.shape_type.hasMvalues()) {
            this.SHP_range_m[0] = bb.getDouble();
            this.SHP_range_m[1] = bb.getDouble();
            this.SHP_m_values = new double[this.SHP_num_points];
            i = 0;
            while (i < this.SHP_num_points) {
                this.SHP_m_values[i] = bb.getDouble();
                ++i;
            }
        }
    }

    @Override
    public void print() {
        System.out.printf(Locale.ENGLISH, "   _ _ _ _ _ \n");
        System.out.printf(Locale.ENGLISH, "  / SHAPE   \\_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _\n");
        System.out.printf(Locale.ENGLISH, "  |                                                    \\\n");
        System.out.printf(Locale.ENGLISH, "  |  <RECORD HEADER>\n");
        System.out.printf(Locale.ENGLISH, "  |    SHP_record_number       = %d\n", this.SHP_record_number);
        System.out.printf(Locale.ENGLISH, "  |    SHP_content_length      = %d bytes  (check: start/end/size = %d/%d/%d)\n", this.SHP_content_length * 2, this.position_start, this.position_end, this.content_length);
        System.out.printf(Locale.ENGLISH, "  |\n");
        System.out.printf(Locale.ENGLISH, "  |  <RECORD CONTENT>\n");
        System.out.printf(Locale.ENGLISH, "  |    shape_type              = %s (%d)\n", this.shape_type, this.shape_type.ID());
        System.out.printf(Locale.ENGLISH, "  |    SHP_bbox: xmin, xmax    = %+7.3f, %+7.3f\n", this.SHP_bbox[0][0], this.SHP_bbox[0][1]);
        System.out.printf(Locale.ENGLISH, "  |    SHP_bbox: ymin, ymax    = %+7.3f, %+7.3f\n", this.SHP_bbox[1][0], this.SHP_bbox[1][1]);
        System.out.printf(Locale.ENGLISH, "  |    SHP_bbox: zmin, zmax    = %+7.3f, %+7.3f\n", this.SHP_bbox[2][0], this.SHP_bbox[2][1]);
        System.out.printf(Locale.ENGLISH, "  |    SHP_measure: mmin, mmax = %+7.3f, %+7.3f\n", this.SHP_range_m[0], this.SHP_range_m[1]);
        System.out.printf(Locale.ENGLISH, "  |    SHP_num_parts           = %d\n", this.SHP_num_parts);
        System.out.printf(Locale.ENGLISH, "  |    SHP_num_points          = %d\n", this.SHP_num_points);
        System.out.printf(Locale.ENGLISH, "  \\_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ /\n");
    }

    public double[][] getBoundingBox() {
        return this.SHP_bbox;
    }

    public double[] getMeasureRange() {
        return this.SHP_range_m;
    }

    public int getNumberOfPoints() {
        return this.SHP_num_points;
    }

    public int getNumberOfParts() {
        return this.SHP_num_parts;
    }

    public double[][] getPoints() {
        return this.SHP_xyz_points;
    }

    public double[][][] getPointsAs3DArray() {
        if (this.parts != null) {
            return this.parts;
        }
        int[] indices = new int[this.SHP_num_parts + 1];
        System.arraycopy(this.SHP_parts, 0, indices, 0, this.SHP_num_parts);
        indices[indices.length - 1] = this.SHP_num_points;
        this.parts = new double[this.SHP_num_parts][][];
        int i = 0;
        while (i < indices.length - 1) {
            int from = indices[i];
            int to = indices[i + 1];
            int size = to - from;
            this.parts[i] = new double[size][4];
            int j = from;
            int idx = 0;
            while (j < to) {
                this.parts[i][idx][0] = this.SHP_xyz_points[j][0];
                this.parts[i][idx][1] = this.SHP_xyz_points[j][1];
                this.parts[i][idx][2] = this.SHP_xyz_points[j][2];
                if (this.shape_type.hasMvalues()) {
                    this.parts[i][idx][3] = this.SHP_m_values[j];
                }
                ++j;
                ++idx;
            }
            ++i;
        }
        return this.parts;
    }

    public double[] getMeasureValues() {
        return this.SHP_m_values;
    }
}

