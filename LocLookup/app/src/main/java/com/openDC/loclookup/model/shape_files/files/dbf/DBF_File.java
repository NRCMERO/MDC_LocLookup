/*
 * Decompiled with CFR 0_118.
 */
package com.openDC.loclookup.model.shape_files.files.dbf;

import com.openDC.loclookup.model.shape_files.files.ShapeFileReader;
import com.openDC.loclookup.model.shape_files.shapeFile.ShapeFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;
import java.util.Locale;

public class DBF_File extends ShapeFileReader {
    public static final String ENCODING = "ISO-8859-1";
    public static boolean LOG_INFO = true;
    public static boolean LOG_ONLOAD_HEADER = true;
    public static boolean LOG_ONLOAD_CONTENT = true;
    private byte DBF_file_type;
    private int DBF_date_yy;
    private int DBF_date_mm;
    private int DBF_date_dd;
    private int DBF_number_of_records;
    private int DBF_size_header_bytes;
    private int DBF_size_record_bytes;
    private DBF_Field[] DBF_fields;
    private String[][] DBF_records;

    public DBF_File(ShapeFile parent_shapefile, File file) throws IOException {
        super(parent_shapefile, file);
    }

    @Override
    public void read() throws Exception {
        this.bb.order(ByteOrder.LITTLE_ENDIAN);
        this.DBF_file_type = this.bb.get(0);
        this.DBF_date_yy = this.bb.get(1) + 1900;
        this.DBF_date_mm = this.bb.get(2);
        this.DBF_date_dd = this.bb.get(3);
        this.DBF_number_of_records = this.bb.getInt(4);
        this.DBF_size_header_bytes = this.bb.getShort(8);
        this.DBF_size_record_bytes = this.bb.getShort(10);
        if (LOG_ONLOAD_HEADER) {
            this.printHeader();
        }
        int POS = 32;
        this.bb.position(POS);
        int num_fields = (this.DBF_size_header_bytes - POS - 1) / 32;
        this.DBF_fields = new DBF_Field[num_fields];
        int i = 0;
        while (i < this.DBF_fields.length) {
            this.DBF_fields[i] = new DBF_Field(this, this.bb, i);
            this.bb.position(POS += 32);
            ++i;
        }
        byte DBF_header_terminator = this.bb.get();
        POS = this.DBF_size_header_bytes;
        this.bb.position(POS);
        this.DBF_records = new String[this.DBF_number_of_records][num_fields];
        int i2 = 0;
        while (i2 < this.DBF_number_of_records) {
            byte[] string_tmp = new byte[this.DBF_size_record_bytes];
            this.bb.get(string_tmp);
            try {
                String DBF_record = new String(string_tmp, ENCODING);
                int from = 1;
                int to = 1;
                int j = 0;
                while (j < this.DBF_fields.length) {
                    this.DBF_records[i2][j] = DBF_record.substring(from, to += this.DBF_fields[j].getLength());
                    from = to;
                    ++j;
                }
            } catch (UnsupportedEncodingException | StringIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            this.bb.position(POS += this.DBF_size_record_bytes);
            ++i2;
        }
        if (LOG_ONLOAD_CONTENT) {
            this.printContent();
        }
        if (LOG_INFO) {
            System.out.printf("(ShapeFile) loaded File: \"%s\", records=%d\n", this.file.getName(), this.DBF_number_of_records);
        }
    }

    public String[][] getContent() {
        return this.DBF_records;
    }

    public DBF_Field[] getFields() {
        return this.DBF_fields;
    }

    public String getDate() {
        return String.format(Locale.ENGLISH, "%d.%d.%d", this.DBF_date_yy, this.DBF_date_mm, this.DBF_date_dd);
    }

    @Override
    public void printHeader() {
        System.out.printf(Locale.ENGLISH, "\n");
        System.out.printf(Locale.ENGLISH, "________________________< HEADER >________________________\n");
        System.out.printf(Locale.ENGLISH, "  FILE: \"%s\"\n", this.file.getName());
        System.out.printf("  DBF_file_type         = %d\n", this.DBF_file_type);
        System.out.printf("  YYYY.MM.DD            = %d.%d.%d\n", this.DBF_date_yy, this.DBF_date_mm, this.DBF_date_dd);
        System.out.printf("  DBF_number_of_records = %d\n", this.DBF_number_of_records);
        System.out.printf("  DBF_size_header_bytes = %d\n", this.DBF_size_header_bytes);
        System.out.printf("  DBF_size_record_bytes = %d\n", this.DBF_size_record_bytes);
        System.out.printf(Locale.ENGLISH, "________________________< /HEADER >________________________\n");
    }

    @Override
    public void printContent() {
        System.out.printf(Locale.ENGLISH, "\n");
        System.out.printf(Locale.ENGLISH, "________________________< CONTENT >________________________\n");
        System.out.printf(Locale.ENGLISH, "  FILE: \"%s\"\n", this.file.getName());
        System.out.printf(Locale.ENGLISH, "\n");
        System.out.printf(Locale.ENGLISH, "  FIELDS:\n");
        int i = 0;
        while (i < this.DBF_fields.length) {
            DBF_Field field = this.DBF_fields[i];
            field.print();
            ++i;
        }
        System.out.printf(Locale.ENGLISH, "\n");
        System.out.printf(Locale.ENGLISH, "  RECORDS:\n");
        i = 0;
        while (i < this.DBF_number_of_records) {
            System.out.printf("  [%4d]", i);
            int j = 0;
            while (j < this.DBF_fields.length) {
                System.out.printf("\t[%1d]%s", j, this.DBF_records[i][j]);
                ++j;
            }
            System.out.printf("\n");
            ++i;
        }
        System.out.printf(Locale.ENGLISH, "________________________< /CONTENT >________________________\n");
    }
}

