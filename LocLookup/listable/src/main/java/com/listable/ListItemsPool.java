package com.listable;

import android.util.SparseArray;

public class ListItemsPool {
    private static final SparseArray<ListItemType> sTypeItemMap = new SparseArray<>();

    public static ListItemType getItemType(int type) {
        return sTypeItemMap.get(type);
    }

    public static void put(int itemViewType, ListItemType listItemType) {
        sTypeItemMap.put(itemViewType, listItemType);
    }
}