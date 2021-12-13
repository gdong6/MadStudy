package com.hyphenate.notes.View;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;


public class MainScrollview extends ScrollView {


   private ScrollViewListener scrollViewListener = null;

    public MainScrollview(Context context) {
        super(context);
    }

    public MainScrollview(Context context, AttributeSet attrs,
                          int defStyle) {
        super(context, attrs, defStyle);
    }

    public MainScrollview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnScrollListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }



    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {

        super.onScrollChanged(x, y, oldx, oldy);


        if (scrollViewListener != null) {

            if (oldy < y && ((y - oldy) > 15)) {
                scrollViewListener.onScroll(y - oldy);

            } else if (oldy > y && (oldy - y) > 15) {
                scrollViewListener.onScroll(y - oldy);
            }

        }
    }

    public  interface ScrollViewListener{
        void onScroll(int dy);
    }
}