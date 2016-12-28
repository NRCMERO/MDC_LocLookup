package com.listable;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

public abstract class RecyclerArrayAdapter<M extends ListItem, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {
    protected ArrayList<M> mItems;
    protected OnItemClickListener.OnItemClickCallback mOnItemClickCallback;
    protected LayoutInflater mInflater;

    public RecyclerArrayAdapter(Context context, OnItemClickListener.OnItemClickCallback onItemClickCallback) {
        this();
        mItems = new ArrayList<>();
        mOnItemClickCallback = onItemClickCallback;
        mInflater = LayoutInflater.from(context);
    }

    public RecyclerArrayAdapter() {
        setHasStableIds(true);
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).listItemType.itemViewType;
    }

    public void add(M object) {
        mItems.add(object);
    }

    public void add(int index, M object) {
        mItems.add(index, object);
    }

    public void addAll(Collection<? extends M> collection) {
        if (collection != null) {
            mItems.addAll(collection);
        }
    }

    public void addAll(int index, Collection<? extends M> collection) {
        if (collection != null && index >= 0) {
            mItems.addAll(index, collection);
        }
    }

    public void notifyNoDuplicates() {
        mItems = new ArrayList<>(new LinkedHashSet<>(mItems));
        notifyDataSetChanged();
    }

    public void addAll(List<M> items) {
        mItems.addAll(items);
    }

    public int indexOf(M object) {
        return mItems.indexOf(object);
    }

    public boolean contains(M object) {
        return mItems.contains(object);
    }

    public void clear() {
        mItems.clear();
    }

    public void remove(M object) {
        mItems.remove(object);
    }

    public void remove(int position) {
        mItems.remove(position);
    }

    public void removeAll(List<M> items) {
        mItems.removeAll(items);
    }

    public M getItem(int position) {
        return mItems.get(position);
    }

    public ArrayList<M> getItems() {
        return mItems;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public boolean empty() {
        return getItemCount() == 0;
    }
}
