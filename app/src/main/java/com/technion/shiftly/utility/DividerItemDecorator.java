package com.technion.shiftly.utility;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class DividerItemDecorator extends RecyclerView.ItemDecoration {
    private Drawable mDivider;

    public DividerItemDecorator(Drawable divider) {
        mDivider = divider;
    }

    @Override
    public void onDraw(@Nullable Canvas c, @Nullable RecyclerView parent, @Nullable RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getAdapter().getItemCount();
        for (int i = 0; i < childCount - 1; i++) {

            View child = parent.getChildAt(i);
            if (child != null) {

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}