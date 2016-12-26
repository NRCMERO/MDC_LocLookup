package com.openDC.loclookup.view;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class ViewUtils {
    public static void hideKeyboard(EditText... editTexts) {
        for (EditText editText : editTexts) {
            if (editText == null) {
                continue;
            }
            editText.clearFocus();
            InputMethodManager imm = (InputMethodManager) editText.getContext()
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    public static void hideKeyboard(Context activityContext) {
        hideKeyboard(activityContext, null);
    }

    public static void hideKeyboard(Context activityContext, View parent) {
        if (activityContext instanceof Activity) {
            View view = ((Activity) activityContext).getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) activityContext
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                if (parent != null) {
                    parent.requestFocus();
                } else {
                    view.clearFocus();
                }
            }
        }
    }

    public static void showKeyboard(EditText editText) {
        if (editText == null) {
            return;
        }
        editText.requestFocus();
        InputMethodManager keyboard = (InputMethodManager) editText.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.showSoftInput(editText, 0);
    }
}
