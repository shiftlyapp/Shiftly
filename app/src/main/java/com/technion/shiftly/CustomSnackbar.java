package com.technion.shiftly;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class CustomSnackbar {
    private Snackbar snackbar;
    private int text_size;

    public int getText_size() {
        return text_size;
    }

    public void setText_size(int text_size) {
        this.text_size = text_size;
    }

    public CustomSnackbar(int text_size) {
        this.text_size = text_size;
    }
    public void show(Context context, View v, String msg, int type) {
        this.snackbar = Snackbar.make(v, msg, Snackbar.LENGTH_SHORT);
        View snackbarView = snackbar.getView();
        TextView tv = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        snackbarView.setBackgroundColor(context.getResources().getColor(R.color.text_color_primary));
        tv.setTextSize(text_size);
        tv.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        switch (type) {
            case 0:
                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.snackbar_error, 0, 0, 0);
                break;
            case 1:
                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.snackbar_error, 0, 0, 0);
                break;
        }
        snackbar.show();
    }
}