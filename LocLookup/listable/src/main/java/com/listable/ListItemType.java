package com.listable;

public class ListItemType {
    public static int type;

    public Class<?> viewHolderClass;
    public int layoutResId;
    public int itemViewType;

    public ListItemType(Class<?> viewHolderClass, int layoutResId) {
        this.viewHolderClass = viewHolderClass;
        this.layoutResId = layoutResId;
        this.itemViewType = type++;
        ItemsCollection.put(itemViewType, this);
    }

    @Override
    public int hashCode() {
        return itemViewType;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ListItemType)) {
            return false;
        }
        ListItemType rhs = ((ListItemType) other);
        return itemViewType == rhs.itemViewType;
    }
}