package com.hyphenate.notes.View;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.hyphenate.easeim.R;



public class RecycleCreator implements com.baoyz.swipemenulistview.SwipeMenuCreator {


    private Context mContext;



    public RecycleCreator(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void create(SwipeMenu menu) {

        SwipeMenuItem openItem = new SwipeMenuItem(mContext.getApplicationContext());
        openItem.setBackground(R.color.light_blue);
        openItem.setWidth(dp2px(55));
        openItem.setIcon(R.drawable.pic_reback);
        openItem.setTitleColor(Color.WHITE);
        menu.addMenuItem(openItem);

        SwipeMenuItem deleteItem = new SwipeMenuItem(mContext.getApplicationContext());
        deleteItem.setBackground(R.color.red);
        deleteItem.setWidth(dp2px(55));
        deleteItem.setIcon(R.drawable.pic_delete);
        deleteItem.setTitleColor(Color.WHITE);
        menu.addMenuItem(deleteItem);

    }


    private int dp2px(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                mContext.getResources().getDisplayMetrics());
    }


}
