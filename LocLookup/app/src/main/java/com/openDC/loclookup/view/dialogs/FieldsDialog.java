package com.openDC.loclookup.view.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.openDC.loclookup.model.AppPrefs;
import com.openDC.loclookup.model.vo.FieldItem;
import com.listable.EasyAdapter;
import com.openDC.loclookup.view.SimpleDividerItemDecoration;
import com.openDC.loclookup.view.custom.CustomTextView;

import com.listable.ListItem;
import com.listable.OnItemClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import loclookup.opendc.com.loclookup.R;

public class FieldsDialog extends android.support.v7.app.AlertDialog implements
        DialogInterface.OnDismissListener {
    private View dialogView;
    private RecyclerView recyclerView;
    private CustomTextView titleTextView;
    private CustomTextView positiveButton;
    private CustomTextView negativeButton;

    private Object mTag;
    private View.OnClickListener mOnClickListener;
    private EasyAdapter easyAdapter;

    public FieldsDialog(Context context, View.OnClickListener onClickListener) {
        super(context);
        mOnClickListener = onClickListener;
        setupView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setView(dialogView);
        setOnDismissListener(this);
        super.onCreate(savedInstanceState);
    }

    protected void setupView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        dialogView = inflater.inflate(R.layout.dialog_fields, null, false);
        titleTextView = (CustomTextView) dialogView.findViewById(R.id.txt_title);
        recyclerView = (RecyclerView) dialogView.findViewById(R.id.rv);
        preparePositiveButton();
        prepareNegativeButton();
    }

    private void preparePositiveButton() {
        positiveButton = (CustomTextView) dialogView.findViewById(R.id.btn_positive);
        positiveButton.setOnClickListener(onButtonClickListener);
        positiveButton.setTag(mTag);
    }

    private void prepareNegativeButton() {
        negativeButton = (CustomTextView) dialogView.findViewById(R.id.btn_negative);
        negativeButton.setOnClickListener(onButtonClickListener);
        negativeButton.setTag(mTag);
    }

    private View.OnClickListener onButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            v.setTag(getSelected());
            if (mOnClickListener != null) { // TODO: check if at least 2 fields selected
                mOnClickListener.onClick(v);
            }
        }
    };

    public void draw(List<FieldItem> fields,
                     String mapName,
                     int titleResId,
                     int positiveButtonCaptionResId,
                     int negativeButtonCaptionResId) {
        titleTextView.setText(titleResId);
        String selectedFields = AppPrefs.getFields(getContext(), mapName);
        drawList(fields, selectedFields);
        drawButtons(positiveButtonCaptionResId, negativeButtonCaptionResId);
    }

    private void drawList(List<FieldItem> fields, String selectedFields) {
        if (fields == null) {
            return;
        }
        if (selectedFields != null) {
            List<String> selectedFieldsList = Arrays.asList(selectedFields.split("[,]"));
            for (FieldItem fieldItem : fields) {
                fieldItem.isChecked = selectedFieldsList.contains(fieldItem.fieldName);
            }
        }
        easyAdapter = new EasyAdapter(getContext(), new ArrayList<ListItem>(fields),
                new OnItemClickListener.OnItemClickCallback() {
                    @Override
                    public void onItemClicked(View view, ListItem listableItem, int position) {
                        FieldItem field = (FieldItem) listableItem;
                        field.isChecked = !field.isChecked;
                        easyAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onItemLongClicked(View view, ListItem listableItem, int position) {
                    }
                });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(easyAdapter);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
    }

    private void drawButtons(int positiveButtonCaptionResId, int negativeButtonCaptionResId) {
        positiveButton.setText(positiveButtonCaptionResId);
        if (negativeButtonCaptionResId == 0) {
            negativeButton.setVisibility(View.GONE);
        } else {
            negativeButton.setVisibility(View.VISIBLE);
            negativeButton.setText(negativeButtonCaptionResId);
        }
    }

    public void setTag(Object tag) {
        this.mTag = tag;
        positiveButton.setTag(mTag);
        negativeButton.setTag(mTag);
    }

    public Object getTag() {
        return mTag;
    }

    private String getSelected() {
        if (easyAdapter == null) {
            return "";
        }
        List<String> selected = new ArrayList<>();
        for (ListItem listItem : easyAdapter.getItems()) {
            FieldItem field = (FieldItem) listItem;
            if (field.isChecked) {
                selected.add(field.fieldName);
            }
        }
        return TextUtils.join(",", selected);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        recyclerView.setVisibility(View.INVISIBLE);
    }
}