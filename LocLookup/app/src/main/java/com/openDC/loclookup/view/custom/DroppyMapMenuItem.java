package com.openDC.loclookup.view.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.openDC.loclookup.model.vo.MapPopupItem;
import com.shehabic.droppy.DroppyMenuItemAbstract;

import loclookup.opendc.com.loclookup.R;

public class DroppyMapMenuItem extends DroppyMenuItemAbstract {
    public static final int ID_EDIT_NAME = 0;
    public static final int ID_EDIT_FIELDS = 1;
    public static final int ID_DELETE_MAP = 2;

    private MapPopupItem mapPopupItem;

    public DroppyMapMenuItem(int customResourceId, MapPopupItem mapPopupItem) {
        type = TYPE_CUSTOM;
        customViewResourceId = customResourceId;
        this.mapPopupItem = mapPopupItem;
    }

    @Override
    public View render(Context context) {
        if (renderedView == null) {
            renderedView = LayoutInflater.from(context).inflate(customViewResourceId, null);
        }
        TextView titleTextView = (TextView) renderedView.findViewById(R.id.txt_title);
        titleTextView.setText(mapPopupItem.title);
        ImageView iconImageView = (ImageView) renderedView.findViewById(R.id.img_icon);
        iconImageView.setImageResource(mapPopupItem.iconResId);
        return renderedView;
    }

    @Override
    public int getId() {
        return mapPopupItem.id;
    }
}
