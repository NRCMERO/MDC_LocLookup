package com.openDC.loclookup.model.vo;

import com.openDC.loclookup.model.ListItemTypes;

import org.zakariya.stickyheaders.ListItem;

public class FieldItem extends ListItem {
    public String fieldName;
    public String fieldValue;
    public boolean isChecked;

    public FieldItem(String fieldName, String fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        listItemType = ListItemTypes.FIELD;
    }
}