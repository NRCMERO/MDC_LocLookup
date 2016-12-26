package com.openDC.loclookup.view.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.openDC.loclookup.model.vo.FieldItem;

import org.zakariya.stickyheaders.BasicViewHolder;
import org.zakariya.stickyheaders.ListItem;
import org.zakariya.stickyheaders.OnItemClickListener;

import loclookup.opendc.com.loclookup.R;

public class FieldViewHolder extends BasicViewHolder {
    public TextView fieldNameTextView;
    public TextView fieldValueTextView;
    public ImageView tickImageView;

    public FieldViewHolder(View itemView, OnItemClickListener.OnItemClickCallback onItemClickCallback) {
        super(itemView, onItemClickCallback);
        fieldNameTextView = (TextView) find(R.id.txt_field_name);
        fieldValueTextView = (TextView) find(R.id.txt_field_value);
        tickImageView = (ImageView) find(R.id.img_tick);
        attachClickListener(itemView);
    }

    @Override
    public void draw(ListItem listable) {
        super.draw(listable);
        FieldItem map = (FieldItem) listable;
        fieldNameTextView.setText(map.fieldName);
        fieldValueTextView.setText(map.fieldValue);
        tickImageView.setAlpha(map.isChecked ? 1.0f : 0.2f);
    }
}