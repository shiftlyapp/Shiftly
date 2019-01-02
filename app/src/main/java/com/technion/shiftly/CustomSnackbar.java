package com.technion.shiftly;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class CustomSnackbar {
    public static final int SNACKBAR_ERROR = 0;
    public static final int SNACKBAR_SUCCESS = 1;
    public static final int SNACKBAR_DEFAULT_TEXT_SIZE = 18;

    private int text_size;
    private int length;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getText_size() {
        return text_size;
    }

    public void setText_size(int text_size) {
        this.text_size = text_size;
    }

    CustomSnackbar(int text_size) {
        this.text_size = text_size;
    }
    public void show(Context context, View v, String msg, int type, int length) {
        Snackbar snackbar = Snackbar.make(v, msg, length);
        View snackbarView = snackbar.getView();
        TextView tv = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        snackbarView.setBackgroundColor(context.getResources().getColor(R.color.snackbar_bg));
        tv.setTextSize(text_size);
        tv.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        switch (type) {
            case 0: // Failure
                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.snackbar_error, 0, 0, 0);
                break;
            case 1: // Success
                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.v, 0, 0, 0);
                break;
        }
        snackbar.show();
    }
}