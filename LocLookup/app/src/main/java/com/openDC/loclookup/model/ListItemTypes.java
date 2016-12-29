package com.openDC.loclookup.model;

import com.listable.BasicViewHolder;
import com.listable.ListItemType;
import com.openDC.loclookup.view.viewholders.FieldViewHolder;
import com.openDC.loclookup.view.viewholders.MapViewHolder;

import loclookup.opendc.com.loclookup.R;

public class ListItemTypes {
    public static ListItemType NONE;
    public static ListItemType MAP_ITEM;
    public static ListItemType FIELD;

    static {
        NONE = new ListItemType(BasicViewHolder.class, 0);
        MAP_ITEM = new ListItemType(MapViewHolder.class, R.layout.item_choice);
        FIELD = new ListItemType(FieldViewHolder.class, R.layout.item_field);
    }
}