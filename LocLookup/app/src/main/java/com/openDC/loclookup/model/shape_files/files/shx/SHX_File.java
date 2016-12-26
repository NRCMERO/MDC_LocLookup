/*
 * Decompiled with CFR 0_118.
 */
package com.openDC.loclookup.model.shape_files.files.shx;

import com.openDC.loclookup.model.shape_files.files.ShapeFileReader;
import com.openDC.loclookup.model.shape_files.files.shp.SHP_Header;
import com.openDC.loclookup.model.shape_files.shapeFile.ShapeFile;

import java.io.File;
import java.nio.ByteOrder;
import java.util.Locale;

public class SHX_File
        extends ShapeFileReader {
    public static boolean LOG_INFO = true;
    public static boolean LOG_ONLOAD_HEADER = true;
    public static boolean LOG_ONLOAD_CONTENT = true;
    private SHP_Header header;
    private int[] SHX_shape_offsets;
    private int[] SHX_shape_content_lengths;

    public SHX_File(ShapeFile parent_shapefile, File file) throws Exception {
        super(parent_shapefile, file);
    }

    @Override
    public void read() throws Exception {
        this.header = new SHP_Header(this.parent_shapefile, this.file);
        this.header.read(this.bb);
        if (LOG_ONLOAD_HEADER) {
            this.printHeader();
        }
        this.bb.order(ByteOrder.BIG_ENDIAN);
        int number_of_bytes = this.bb.capacity() - this.bb.position();
        int number_of_ints = number_of_bytes / 4;
        int number_of_records = number_of_ints / 2;
        this.SHX_shape_offsets = new int[number_of_records];
        this.SHX_shape_content_lengths = new int[number_of_records];
        int i = 0;
        while (i < this.SHX_shape_offsets.length) {
            this.SHX_shape_offsets[i] = this.bb.getInt();
            this.SHX_shape_content_lengths[i] = this.bb.getInt();
            ++i;
        }
        if (LOG_ONLOAD_CONTENT) {
            this.printContent();
        }
        if (LOG_INFO) {
            System.out.printf("(ShapeFile) loaded File: \"%s\", records=%d\n", this.file.getName(), this.SHX_shape_offsets.length);
        }
    }

    public SHP_Header getHeader() {
        return this.header;
    }

    public int[] getRecordOffsets() {
        return this.SHX_shape_offsets;
    }

    public int[] getRecordLenghts() {
        return this.SHX_shape_content_lengths;
    }

    @Override
    public void printHeader() {
        this.header.print();
    }

    @Override
    public void printContent() {
        System.out.printf(Locale.ENGLISH, "\n________________________< CONTENT >________________________\n");
        System.out.printf(Locale.ENGLISH, "  FILE: \"%s\"\n", this.file.getName());
        System.out.printf(Locale.ENGLISH, "\n");
        int i = 0;
        while (i < this.SHX_shape_offsets.length) {
            System.out.printf("  [%4d] offset(bytes): %8d; record_length(bytes): %8d\n", i, this.SHX_shape_offsets[i], this.SHX_shape_content_lengths[i]);
            ++i;
        }
        System.out.printf(Locale.ENGLISH, "________________________< /CONTENT>________________________\n");
    }
}

