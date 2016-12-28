package com.openDC.loclookup.model;

import android.util.SparseArray;

import com.openDC.loclookup.view.viewholders.FieldViewHolder;
import com.openDC.loclookup.view.viewholders.MapViewHolder;

import com.listable.BasicViewHolder;
import com.listable.ListItemType;

import loclookup.opendc.com.loclookup.R;

public class ListItemTypes {
    public static ListItemType NONE;
    public static ListItemType MAP_ITEM;
    public static ListItemType FIELD;

    private static SparseArray<ListItemType> sTypeItemMap = new SparseArray<>();

    public static ListItemType getItemType(int type) {
        return sTypeItemMap.get(type);
    }

    // @formatter:off
    static {
        sTypeItemMap    = new SparseArray<>();
        NONE            = new ListItemType(  BasicViewHolder.class,    0,                             sTypeItemMap);
        MAP_ITEM        = new ListItemType(  MapViewHolder.class,      R.layout.item_choice,   sTypeItemMap);
        FIELD           = new ListItemType(  FieldViewHolder.class,    R.layout.item_field,           sTypeItemMap);
    }
    // @formatter:on
}