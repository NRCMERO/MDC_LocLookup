package com.openDC.loclookup.model.vo;

import org.zakariya.stickyheaders.ListItem;

public class MapPopupItem extends ListItem {
    public String title;
    public int iconResId;
    public int id;

    public MapPopupItem(String title, int iconResId, int id) {
        this.title = title;
        this.iconResId = iconResId;
        this.id = id;
    }
}