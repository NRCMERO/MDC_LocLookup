package org.zakariya.stickyheaders;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class BasicViewHolder extends RecyclerView.ViewHolder {
    public OnItemClickListener onItemClickListener;
    public int section;
    public int numberOfItemsInSection;
    public int positionInSection;

    public BasicViewHolder(View itemView) {
        super(itemView);
    }

    public BasicViewHolder(View itemView, OnItemClickListener.OnItemClickCallback onItemClickCallback) {
        super(itemView);
        if (onItemClickCallback != null) {
            onItemClickListener = new OnItemClickListener(getAdapterPosition(), onItemClickCallback);
        }
    }

    public void draw(ListItem listable) {
        if (onItemClickListener != null) {
            onItemClickListener.setListableItem(listable);
            onItemClickListener.setPosition(getAdapterPosition());
        }
    }

    protected void attachClickListener(View... views) {
        for (View view : views) {
            view.setOnClickListener(onItemClickListener);
            view.setOnLongClickListener(onItemClickListener);
        }
    }

    protected View find(int id) {
        return itemView.findViewById(id);
    }

    protected Context getContext() {
        return itemView.getContext();
    }

    protected String getString(int resId) {
        return getContext().getString(resId);
    }

    protected int getColor(int resId) {
        return ContextCompat.getColor(getContext(), resId);
    }
}