package com.lob.musicshare.util.recyclerview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

import com.lob.musicshare.R;

public class RecyclerViewItemDecorator extends RecyclerView.ItemDecoration {

    private Drawable mDivider;

    public RecyclerViewItemDecorator(Context context) {
        mDivider = context.getResources().getDrawable(R.drawable.line_divider);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = (int) (parent.getPaddingLeft() + convertDpToPixels(92));
        int right = (int) (parent.getWidth() - parent.getPaddingRight() - convertDpToPixels(16));

        int childCount = parent.getChildCount();
        for (int i = 1; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    private float convertDpToPixels(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return Math.round(dp * (metrics.densityDpi / 160f));
    }
}