package com.openDC.loclookup.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.openDC.loclookup.view.FontUtils;

import loclookup.opendc.com.loclookup.R;

public class CustomEditText extends EditText {
    public CustomEditText(Context context) {
        super(context);
        init(null);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    protected void init(AttributeSet attrs) {
        int ordinal = 0;
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CustomTextView);
            ordinal = typedArray.getInt(R.styleable.CustomTextView_font, 0);
            typedArray.recycle();
        }
        Typeface typeface = FontUtils.getFont(getContext(), FontUtils.CustomFont.values()[ordinal]);
        setTypeface(typeface);
        setPadding(getPaddingLeft(),
                getPaddingTop() - getTopMinusPadding(getContext()),
                getPaddingRight(),
                getPaddingBottom());
    }

    public void setFont(FontUtils.CustomFont font) {
        setTypeface(FontUtils.getFont(getContext(), font));
    }

    /**
     * Padding
     */

    private static int topMinusPadding = 0;

    private static int getTopMinusPadding(Context context) {
        if (topMinusPadding == 0) {
            topMinusPadding = (int) context.getResources().getDimension(R.dimen.margin6);
        }
        return topMinusPadding;
    }
}