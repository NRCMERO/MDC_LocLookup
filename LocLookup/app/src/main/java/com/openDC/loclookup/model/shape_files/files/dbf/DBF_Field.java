/*
 * Decompiled with CFR 0_118.
 */
package com.openDC.loclookup.model.shape_files.files.dbf;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class DBF_Field {
    public static final int SIZE_BYTES = 32;
    private DBF_File parent_dbasefile;
    private int index = 0;
    private String DBF_field_name = "";
    private char DBF_field_type;
    private int DBF_field_displacement;
    private int DBF_field_length;
    private byte DBF_field_flag;
    private int DBF_autoincr_next;
    private byte DBF_autoincr_step;

    public DBF_Field(DBF_File parent_dbasefile, ByteBuffer bb, int index) {
        this.parent_dbasefile = parent_dbasefile;
        this.index = index;
        byte[] string_tmp = new byte[11];
        bb.get(string_tmp);
        try {
            this.DBF_field_name = new String(string_tmp, DBF_File.ENCODING);
            this.DBF_field_name = this.DBF_field_name.substring(0, this.DBF_field_name.indexOf(0));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        this.DBF_field_type = (char) bb.get();
        this.DBF_field_displacement = bb.getInt();
        this.DBF_field_length = bb.get() & 255;
        this.DBF_field_flag = bb.get();
        this.DBF_autoincr_next = bb.getInt();
        this.DBF_autoincr_step = bb.get();
    }

    public void print() {
        DBF_Field field = this;
        String name = field.getName();
        int length = field.getLength();
        char type = field.getType();
        String type_name = FieldType.byID(type).longName();
        System.out.printf("  DBF_Field[%d]: name: %-10s; length(chars): %3d; type: %1c(=%s)\n", this.index, name, length, Character.valueOf(type), type_name);
    }

    public String getName() {
        return this.DBF_field_name;
    }

    public char getType() {
        return this.DBF_field_type;
    }

    public int getLength() {
        return this.DBF_field_length;
    }

    public int getIndex() {
        return this.index;
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DBF_Field)) {
            return false;
        }
        DBF_Field rhs = ((DBF_Field) other);
        return getName().equals(rhs.getName());
    }

    public enum FieldType {
        C('C', "Character"),
        D('D', "Date"),
        N('N', "Numeric"),
        L('L', "Logical"),
        M('M', "Memo"),
        UNDEFINED('\u0000', "Undefined");

        private String name_long;
        private char ID;

        FieldType(char ID, String name_long) {
            this.ID = ID;
            this.name_long = name_long;
        }

        public static FieldType byID(char ID) {
            FieldType[] arrfieldType = FieldType.values();
            int n = arrfieldType.length;
            int n2 = 0;
            while (n2 < n) {
                FieldType type = arrfieldType[n2];
                if (type.ID == ID) {
                    return type;
                }
                ++n2;
            }
            return UNDEFINED;
        }

        public String longName() {
            return this.name_long;
        }

        public char ID() {
            return this.ID;
        }
    }
}

