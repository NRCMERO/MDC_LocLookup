package com.listable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.util.List;

public class EasyAdapter extends RecyclerArrayAdapter<ListItem, BasicViewHolder> {
    private LayoutInflater inflater;

    public EasyAdapter(Context context, List<? extends ListItem> data,
                       OnItemClickListener.OnItemClickCallback onItemClickCallback) {
        super(context, onItemClickCallback);
        inflater = LayoutInflater.from(context);
        addAll(data);
    }

    @Override
    public BasicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ListItemType listItemType = ItemsCollection.getItemType(viewType);
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

    @Override
    public void onBindViewHolder(BasicViewHolder viewHolder, int position) {
        viewHolder.draw(mItems.get(position));
    }
}