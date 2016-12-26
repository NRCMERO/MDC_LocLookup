package org.zakariya.stickyheaders;

import android.view.View;

public class OnItemClickListener implements View.OnClickListener,
        View.OnLongClickListener {
    private OnItemClickCallback onItemClickCallback;
    private ListItem listableItem;
    private int position;

    public OnItemClickListener(int position, OnItemClickCallback onItemClickCallback) {
        this.position = position;
        this.onItemClickCallback = onItemClickCallback;
    }

    @Override
    public void onClick(View view) {
        onItemClickCallback.onItemClicked(view, listableItem, position);
    }

    @Override
    public boolean onLongClick(View v) {
        onItemClickCallback.onItemLongClicked(v, listableItem, position);
        return true;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setListableItem(ListItem listableItem) {
        this.listableItem = listableItem;
    }

    public OnItemClickCallback getOnItemClickCallback() {
        return onItemClickCallback;
    }

    public interface OnItemClickCallback {
        void onItemClicked(View view, ListItem listableItem, int position);

        void onItemLongClicked(View view, ListItem listableItem, int position);
    }
}