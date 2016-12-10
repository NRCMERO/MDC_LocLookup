package com.openDC.loclookup.controller.utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.PolyUtil;
import com.openDC.loclookup.controller.activities.LocationActivity;

import java.util.ArrayList;
import java.util.List;

import diewald_shapeFile.files.shp.shapeTypes.ShpPolygon;
import diewald_shapeFile.shapeFile.ShapeFile;

public class ShapeFileUtils {
    /**
     * search the shape file for the location
     *
     * @param shapeFile the shape file to process
     * @param location  the location to look for
     * @return the area name that location found in, or empty string if not found
     */
    public static String getLocationName(ShapeFile shapeFile, Location location) {
        LatLng locationLatLon = new LatLng(location.getLatitude(), location.getLongitude());
        int index = 0;
        for (; index < shapeFile.getSHP_shapeCount(); index++) {
            Object obj = shapeFile.getSHP_shape(index);
            if (obj instanceof ShpPolygon) {
                ShpPolygon shpPolygon = (ShpPolygon) obj;
                double[][] boundary = shpPolygon.getBoundingBox();
                if (!isInsideBoundary(boundary, locationLatLon)) {
                    continue;
                }
                List<LatLng> boundaryPoints = new ArrayList<>();
                for (double[] xyz : shpPolygon.getPoints()) {
                    if (xyz.length < 2) {
                        continue;
                    }
                    boundaryPoints.add(new LatLng(xyz[1], xyz[0]));
                }
                boolean containsLocation = PolyUtil.containsLocation(locationLatLon, boundaryPoints, true);
                if (containsLocation) break;
            }
        }
        boolean isInsideShape = (index < shapeFile.getSHP_shapeCount());
        if (isInsideShape) {
            String[] data = shapeFile.getDBF_record(index);
            if (data.length >= 2) {
                return data[0].trim() + ";" + data[1].trim();
            }
        }
        return LocationActivity.RESULT_EMPTY;
    }

    /**
     * check the shape boundary of the if contains the location
     *
     * @param boundaryPoints contains minLat, maxLat, minLon, maxLon
     * @param location       the location to check if inside boundaries
     * @return whether the location is inside the shape boundaries
     */
    private static boolean isInsideBoundary(double[][] boundaryPoints, LatLng location) {
        if (boundaryPoints.length < 2 || boundaryPoints[0].length < 2 || boundaryPoints[1].length < 2) {
            return false;
        }
        LatLng southWest = new LatLng(boundaryPoints[1][0], boundaryPoints[0][0]);
        LatLng northEast = new LatLng(boundaryPoints[1][1], boundaryPoints[0][1]);
        LatLngBounds bounds = new LatLngBounds(southWest, northEast);
        return bounds.contains(location);
    }
}
