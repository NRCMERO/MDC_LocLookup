package com.openDC.loclookup.view;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openDC.loclookup.model.ListItemTypes;

import org.zakariya.stickyheaders.BasicViewHolder;
import org.zakariya.stickyheaders.ListItem;
import org.zakariya.stickyheaders.ListItemType;
import org.zakariya.stickyheaders.OnItemClickListener;
import org.zakariya.stickyheaders.SectioningAdapter;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class EasyAdapter extends SectioningAdapter {
    private static final String TAG = EasyAdapter.class.getSimpleName();

    private List<ListItem> mItems = new ArrayList<>();
    private OnItemClickListener.OnItemClickCallback mOnItemClickCallback;
    private boolean hasHeaders;

    public EasyAdapter(Context context, List<ListItem> items,
                       OnItemClickListener.OnItemClickCallback onItemClickCallback) {
        inflater = LayoutInflater.from(context);
        mItems = items;
        mOnItemClickCallback = onItemClickCallback;
        hasHeaders = checkHeaders();
        if (!hasHeaders) {
            wrapInSingleItem();
        }
    }

    public void toggleSectionCollapse(int sectionIndex) {
        Log.d(TAG, "toggleSectionCollapse() called with: " + "sectionIndex = [" + sectionIndex + "]");
        setSectionIsCollapsed(sectionIndex, !isSectionCollapsed(sectionIndex));
        mItems.get(sectionIndex).isCollapsed = isSectionCollapsed(sectionIndex);
    }

    /**
     * @return
     */
    @Override
    public int getNumberOfSections() {
        return mItems.size();
    }

    /**
     * @param sectionIndex index of the section in question
     * @return
     */
    @Override
    public int getNumberOfItemsInSection(int sectionIndex) {
        return mItems.get(sectionIndex).getSubItems().size();
    }

    /**
     * @param sectionIndex index of the section in question
     * @return
     */
    @Override
    public boolean doesSectionHaveHeader(int sectionIndex) {
        return hasHeaders && mItems.get(sectionIndex).isHeader;
    }

    /**
     * @param sectionIndex index of the section in question
     * @return
     */
    @Override
    public boolean doesSectionHaveFooter(int sectionIndex) {
        return mItems.get(sectionIndex).footer != null;
    }

    @Override
    public BasicViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        ListItemType listItemType = ListItemTypes.getItemType(viewType);
        View view = inflater.inflate(listItemType.layoutResId, parent, false);
        try {
            Class<?>[] types = new Class[]{View.class, OnItemClickListener.OnItemClickCallback.class};
            Constructor<?> cons = listItemType.viewHolderClass.getConstructor(types);
            return (BasicViewHolder) cons.newInstance(view, mOnItemClickCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param sectionIndex the header's section
     * @return
     */
    @Override
    public int getSectionHeaderUserType(int sectionIndex) {
        ListItemType type = mItems.get(sectionIndex).listItemType;
        return type == null ? 0 : type.itemViewType;
    }

    /**
     * @param sectionIndex the items's section
     * @param itemIndex    the position of the item in the section
     * @return
     */
    @Override
    public int getSectionItemUserType(int sectionIndex, int itemIndex) {
        return mItems.get(sectionIndex).get(itemIndex).listItemType.itemViewType;
    }

    /**
     * @param sectionIndex the footer's section
     * @return
     */
    @Override
    public int getSectionFooterUserType(int sectionIndex) {
        return mItems.get(sectionIndex).footer.listItemType.itemViewType;
    }

    /**
     * @param viewHolder   the view holder to update
     * @param sectionIndex the index of the section containing the header to update
     * @param headerType
     */
    @Override
    public void onBindHeaderViewHolder(BasicViewHolder viewHolder, int sectionIndex, int headerType) {
        viewHolder.draw(mItems.get(sectionIndex));
    }

    /**
     * @param viewHolder   the view holder to update
     * @param sectionIndex the index of the section containing the item
     * @param itemIndex    the index of the item in the section where 0 is the first item
     * @param itemType
     */
    @Override
    public void onBindItemViewHolder(BasicViewHolder viewHolder, int sectionIndex, int itemIndex, int itemType) {
        viewHolder.draw(mItems.get(sectionIndex).get(itemIndex));
    }

    /**
     * @param viewHolder   the view holder to update
     * @param sectionIndex the index of the section containing the footer to update
     * @param footerType
     */
    @Override
    public void onBindFooterViewHolder(BasicViewHolder viewHolder, int sectionIndex, int footerType) {
        viewHolder.draw(mItems.get(sectionIndex).footer);
    }

    private boolean checkHeaders() {
        for (ListItem item : mItems) {
            if (item.isHeader) {
                return true;
            }
        }
        return false;
    }

    private void wrapInSingleItem() {
        ListItem listItem = new ListItem();
        listItem.appendSubItems(mItems);
        mItems.clear();
        mItems.add(listItem);
    }

    /**
     * @return true if the recyclerview has no items
     */
    public boolean empty() {
        return hasHeaders ? mItems.isEmpty() : mItems.get(0).getSubItems().isEmpty();
    }

    public List<ListItem> getItems() {
        return hasHeaders ? mItems : mItems.get(0).getSubItems();
    }

    public void clear() {
        if (hasHeaders) {
            mItems.clear();
        } else {
            mItems.get(0).clear();
        }
    }

    public void addAll(List<ListItem> extraItems) {
        if (hasHeaders) {
            mItems.addAll(extraItems);
        } else {
            addAll(0, extraItems);
        }
    }

    public void addAll(int sectionIndex, List<ListItem> extraItems) {
        mItems.get(sectionIndex).appendSubItems(extraItems);
    }

    public void add(ListItem item) {
        if (hasHeaders) {
            mItems.add(item);
        } else {
            mItems.get(0).appendSubItem(item);
        }
    }

    public void add(int sectionIndex, ListItem item) {
        mItems.get(sectionIndex).appendSubItem(item);
    }

    public ListItem get(int sectionIndex, int itemIndex) {
        return mItems.get(sectionIndex).getSubItems().get(itemIndex);
    }

    public ListItem get(int index) {
        return hasHeaders ? mItems.get(index) : get(0, index);
    }

    public void remove(int sectionIndex, int itemIndex) {
        mItems.get(sectionIndex).getSubItems().remove(itemIndex);
    }

    public void remove(int index) {
        if (hasHeaders) {
            mItems.remove(index);
        } else {
            mItems.get(0).getSubItems().remove(index);
        }
    }

    public int indexOfHeader(ListItem item) {
        return mItems.indexOf(item);
    }

    public Point indexOfItem(ListItem item) {
        for (int index = 0; index < mItems.size(); index++) {
            int subIndex = mItems.get(index).getSubItems().indexOf(item);
            if (subIndex != -1) {
                return new Point(index, subIndex);
            }
        }
        return null;
    }

    public void collapseAll() {
        for (int sectionIndex = 0; sectionIndex < mItems.size(); sectionIndex++) {
            ListItem item = mItems.get(sectionIndex);
            item.isCollapsed = true;
            setSectionIsCollapsed(sectionIndex, true);
        }
    }
}
