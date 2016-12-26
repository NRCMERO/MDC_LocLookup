/*
 * Decompiled with CFR 0_118.
 */
package com.openDC.loclookup.model.shape_files.files.shp;

import com.openDC.loclookup.model.shape_files.files.ShapeFileReader;
import com.openDC.loclookup.model.shape_files.files.shp.shapeTypes.ShpMultiPoint;
import com.openDC.loclookup.model.shape_files.files.shp.shapeTypes.ShpPoint;
import com.openDC.loclookup.model.shape_files.files.shp.shapeTypes.ShpPolyLine;
import com.openDC.loclookup.model.shape_files.files.shp.shapeTypes.ShpPolygon;
import com.openDC.loclookup.model.shape_files.files.shp.shapeTypes.ShpShape;
import com.openDC.loclookup.model.shape_files.shapeFile.ShapeFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class SHP_File
extends ShapeFileReader {
    public static boolean LOG_INFO = true;
    public static boolean LOG_ONLOAD_HEADER = true;
    public static boolean LOG_ONLOAD_CONTENT = true;
    private SHP_Header header;
    private ArrayList<ShpShape> shapes = new ArrayList();

    public SHP_File(ShapeFile parent_shapefile, File file) throws Exception {
        super(parent_shapefile, file);
    }

    @Override
    public void read() throws Exception {
        ShpShape.Type shape_type;
        this.header = new SHP_Header(this.parent_shapefile, this.file);
        this.header.read(this.bb);
        if (LOG_ONLOAD_HEADER) {
            this.printHeader();
        }
        if ((shape_type = this.header.getShapeType()) != ShpShape.Type.NullShape) {
            if (shape_type.isTypeOfPolygon()) {
                while (this.bb.position() != this.bb.capacity()) {
                    this.shapes.add(new ShpPolygon(shape_type).read(this.bb));
                }
            } else if (shape_type.isTypeOfPolyLine()) {
                while (this.bb.position() != this.bb.capacity()) {
                    this.shapes.add(new ShpPolyLine(shape_type).read(this.bb));
                }
            } else if (shape_type.isTypeOfPoint()) {
                while (this.bb.position() != this.bb.capacity()) {
                    this.shapes.add(new ShpPoint(shape_type).read(this.bb));
                }
            } else if (shape_type.isTypeOfMultiPoint()) {
                while (this.bb.position() != this.bb.capacity()) {
                    this.shapes.add(new ShpMultiPoint(shape_type).read(this.bb));
                }
            } else if (shape_type == ShpShape.Type.MultiPatch) {
                System.err.println("(ShapeFile) Shape.Type.MultiPatch not supported at the moment.");
            }
        }
        if (LOG_ONLOAD_CONTENT) {
            this.printContent();
        }
        if (LOG_INFO) {
            System.out.printf("(ShapeFile) loaded File: \"%s\", records=%d (%s-Shapes)\n", this.file.getName(), this.shapes.size(), shape_type);
        }
    }

    public SHP_Header getHeader() {
        return this.header;
    }

    public ArrayList<ShpShape> getShpShapes() {
        return this.shapes;
    }

    @Override
    public void printHeader() {
        this.header.print();
    }

    @Override
    public void printContent() {
        System.out.printf(Locale.ENGLISH, "\n");
        System.out.printf(Locale.ENGLISH, "________________________< CONTENT >________________________\n");
        System.out.printf(Locale.ENGLISH, "  FILE: \"%s\"\n", this.file.getName());
        System.out.printf(Locale.ENGLISH, "\n");
        for (ShpShape shape : this.shapes) {
            shape.print();
        }
        System.out.printf(Locale.ENGLISH, "\n");
        System.out.printf(Locale.ENGLISH, "________________________< /CONTENT >________________________\n");
    }
}

