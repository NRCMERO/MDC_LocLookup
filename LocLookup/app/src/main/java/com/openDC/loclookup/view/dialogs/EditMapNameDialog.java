package com.openDC.loclookup.view.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.openDC.loclookup.view.custom.CustomTextView;

import loclookup.opendc.com.loclookup.R;

public class EditMapNameDialog extends AlertDialog {
    private View dialogView;
    private TextView titleTextView;
    private TextView positiveButton;
    private TextView negativeButton;
    private EditText mapNameEditText;

    private Object mTag;
    private View.OnClickListener mOnClickListener;

    public EditMapNameDialog(Context context, View.OnClickListener onClickListener) {
        super(context);
        mOnClickListener = onClickListener;
        setupView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setView(dialogView);
        super.onCreate(savedInstanceState);
    }

    protected void setupView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        dialogView = inflater.inflate(R.layout.dialog_edit_map_name, null, false);
        titleTextView = (CustomTextView) dialogView.findViewById(R.id.txt_title);
        mapNameEditText = (EditText) dialogView.findViewById(R.id.etxt_map_name);
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
            String newMapName = mapNameEditText.getText().toString();
            if (newMapName.isEmpty()) {
                Toast.makeText(getContext(), R.string.toast_empty_map_name, Toast.LENGTH_LONG).show();
                return;
            }
            setTag(newMapName);
            if (mOnClickListener != null) {
                mOnClickListener.onClick(v);
            }
        }
    };

    public void draw(String mapName,
                     int titleResId,
                     int positiveButtonCaptionResId,
                     int negativeButtonCaptionResId) {
        titleTextView.setText(titleResId);
        mapNameEditText.setText(mapName);
        drawButtons(positiveButtonCaptionResId, negativeButtonCaptionResId);
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
}