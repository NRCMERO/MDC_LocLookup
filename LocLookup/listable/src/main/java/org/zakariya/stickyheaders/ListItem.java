package org.zakariya.stickyheaders;

import java.util.ArrayList;
import java.util.List;

public class ListItem {
    private List<ListItem> subItems = new ArrayList<>();

    public ListItemType listItemType;
    public ListItem footer;
    public boolean isHeader;
    public boolean isCollapsed;

    public List<ListItem> getSubItems() {
        return subItems;
    }

    public void appendSubItems(List<ListItem> extraItems) {
        subItems.addAll(extraItems);
        isHeader = true;
    }

    public void appendSubItem(ListItem extraItem) {
        subItems.add(extraItem);
        isHeader = true;
    }

    public void clear() {
        subItems.clear();
    }

    public ListItem get(int index) {
        return subItems.get(index);
    }

    public long getStableId() {
        return 0;
    }
}