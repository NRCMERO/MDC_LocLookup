package com.openDC.loclookup.view.viewholders;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.openDC.loclookup.model.vo.MapItem;

import com.listable.BasicViewHolder;
import com.listable.ListItem;
import com.listable.OnItemClickListener;

import loclookup.opendc.com.loclookup.R;

public class MapViewHolder extends BasicViewHolder {
    public TextView choiceTextView;
    public ImageView tickImageView;

    public MapViewHolder(View itemView, OnItemClickListener.OnItemClickCallback onItemClickCallback) {
        super(itemView, onItemClickCallback);
        choiceTextView = (TextView) find(R.id.txt_choice);
        tickImageView = (ImageView) find(R.id.img_tick);
        attachClickListener(find(R.id.container));
    }

    @Override
    public void draw(ListItem listable) {
        super.draw(listable);
        MapItem map = (MapItem) listable;
        choiceTextView.setText(map.mapName);
        int color = map.isChecked
                ? ContextCompat.getColor(choiceTextView.getContext(), R.color.colorPrimary)
                : Color.BLACK;
        choiceTextView.setTextColor(color);
        tickImageView.setVisibility(map.isChecked ? View.VISIBLE : View.GONE);
    }
}