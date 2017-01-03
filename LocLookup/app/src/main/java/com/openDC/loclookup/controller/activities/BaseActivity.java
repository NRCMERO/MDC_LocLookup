package com.openDC.loclookup.controller.activities;

import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;

import com.openDC.loclookup.view.FontUtils;

public class BaseActivity extends AppCompatActivity {
    /**
     * Changes actionbar title
     *
     * @param titleResId the string resource id of the new title
     */
    public void setActionbarTitle(int titleResId) {
        setActionbarTitle(getString(titleResId));
    }

    /**
     * Changes actionbar title
     *
     * @param title the new actionbar title
     */
    public void setActionbarTitle(String title) {
        if (getSupportActionBar() != null) {
            SpannableString spannableString = new SpannableString(title);
            Object obj = new com.openDC.loclookup.view.TypefaceSpan(this, FontUtils.CustomFont.CAIRO.getFontName());
            spannableString.setSpan(obj, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            getSupportActionBar().setTitle(spannableString);
        }
    }
}
