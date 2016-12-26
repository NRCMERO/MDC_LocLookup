/*
 * Decompiled with CFR 0_118.
 */
package com.openDC.loclookup.model.shape_files.files.shp.shapeTypes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class ShpShape {
    protected Type shape_type;
    protected int SHP_record_number;
    protected int SHP_content_length;
    protected int SHP_shape_type;
    protected int position_start;
    protected int position_end;
    protected int content_length;

    protected ShpShape(Type shape_type) {
        this.shape_type = shape_type;
    }

    public ShpShape read(ByteBuffer bb) throws Exception {
        this.readRecordHeader(bb);
        this.position_start = bb.position();
        bb.order(ByteOrder.LITTLE_ENDIAN);
        this.SHP_shape_type = bb.getInt();
        try {
            Type shape_type_tmp = Type.byID(this.SHP_shape_type);
            if (shape_type_tmp == this.shape_type) {
                this.readRecordContent(bb);
            } else if (shape_type_tmp != Type.NullShape) {
                throw new Exception("(Shape) shape_type = " + shape_type_tmp + ", but expected " + this.shape_type);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.position_end = bb.position();
        this.content_length = this.position_end - this.position_start;
        if (this.content_length != this.SHP_content_length * 2) {
            throw new Exception("(Shape) content_length = " + this.content_length + ", but expected " + this.SHP_content_length * 2);
        }
        return this;
    }

    protected void readRecordHeader(ByteBuffer bb) {
        bb.order(ByteOrder.BIG_ENDIAN);
        this.SHP_record_number = bb.getInt();
        this.SHP_content_length = bb.getInt();
    }

    protected abstract void readRecordContent(ByteBuffer var1);

    public abstract void print();

    public int getRecordNumber() {
        return this.SHP_record_number;
    }

    public Type getShapeType() {
        return this.shape_type;
    }

    public static enum Type {
        NullShape(0, false, false),
        Point(1, false, false),
        PointZ(11, true, true),
        PointM(21, false, true),
        PolyLine(3, false, false),
        PolyLineZ(13, true, true),
        PolyLineM(23, false, true),
        Polygon(5, false, false),
        PolygonZ(15, true, true),
        PolygonM(25, false, true),
        MultiPoint(8, false, false),
        MultiPointZ(18, true, true),
        MultiPointM(28, false, true),
        MultiPatch(31, true, true);
        
        private int ID;
        private boolean has_z_values;
        private boolean has_m_values;

        private Type(int ID, boolean has_z_values, boolean has_m_values) {
            this.has_z_values = has_z_values;
            this.has_m_values = has_m_values;
            this.ID = ID;
        }

        public int ID() {
            return this.ID;
        }

        public static Type byID(int ID) throws Exception {
            Type[] arrtype = Type.values();
            int n = arrtype.length;
            int n2 = 0;
            while (n2 < n) {
                Type st = arrtype[n2];
                if (st.ID == ID) {
                    return st;
                }
                ++n2;
            }
            throw new Exception("ShapeType: " + ID + " does not exist");
        }

        public boolean hasZvalues() {
            return this.has_z_values;
        }

        public boolean hasMvalues() {
            return this.has_m_values;
        }

        public boolean isTypeOfPolygon() {
            return this == Polygon | this == PolygonM | this == PolygonZ;
        }

        public boolean isTypeOfPolyLine() {
            return this == PolyLine | this == PolyLineM | this == PolyLineZ;
        }

        public boolean isTypeOfPoint() {
            return this == Point | this == PointM | this == PointZ;
        }

        public boolean isTypeOfMultiPoint() {
            return this == MultiPoint | this == MultiPointM | this == MultiPointZ;
        }
    }

}

