package com.hyphenate.notes.View;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.hyphenate.easeim.R;


public class MainCreator implements com.baoyz.swipemenulistview.SwipeMenuCreator {


    private Context mContext;



    public MainCreator(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void create(SwipeMenu menu) {

        SwipeMenuItem openItem = new SwipeMenuItem(mContext.getApplicationContext());

        openItem.setBackground(R.color.orange);
        openItem.setWidth(dp2px(55));
        openItem.setIcon(R.drawable.pic_edit);
        openItem.setTitleColor(Color.WHITE);
        menu.addMenuItem(openItem);


        SwipeMenuItem moveItem = new SwipeMenuItem(mContext.getApplicationContext());
        moveItem.setBackground(R.color.gray);
        moveItem.setWidth(dp2px(55));
       //moveItem.setIcon(R.drawable.pic_move);
        moveItem.setTitleColor(Color.WHITE);
        menu.addMenuItem(moveItem);


        SwipeMenuItem deleteItem = new SwipeMenuItem(mContext.getApplicationContext());
        deleteItem.setBackground(R.color.deep_red);
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
