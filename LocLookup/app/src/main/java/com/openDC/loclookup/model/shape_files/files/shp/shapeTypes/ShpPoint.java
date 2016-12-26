/*
 * Decompiled with CFR 0_118.
 */
package com.openDC.loclookup.model.shape_files.files.shp.shapeTypes;

import java.nio.ByteBuffer;
import java.util.Locale;

public class ShpPoint extends ShpShape {
    private double[] SHP_xyz = new double[3];
    private double SHP_m_value;

    public ShpPoint(ShpShape.Type shape_type) {
        super(shape_type);
    }

    @Override
    protected void readRecordContent(ByteBuffer bb) {
        this.SHP_xyz[0] = bb.getDouble();
        this.SHP_xyz[1] = bb.getDouble();
        if (this.shape_type.hasZvalues()) {
            this.SHP_xyz[2] = bb.getDouble();
        }
        if (this.shape_type.hasMvalues()) {
            this.SHP_m_value = bb.getDouble();
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
        System.out.printf(Locale.ENGLISH, "  |    x,y,z,m                 = %5.2f, %5.2f, %5.2f, %5.2f\n", this.SHP_xyz[0], this.SHP_xyz[1], this.SHP_xyz[2], this.SHP_m_value);
        System.out.printf(Locale.ENGLISH, "  \\_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ /\n");
    }

    public double[] getPoint() {
        return this.SHP_xyz;
    }

    public double getMeasure() {
        return this.SHP_m_value;
    }
}

