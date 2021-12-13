package com.hyphenate.notes.View;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.hyphenate.easeim.R;




public class FileCreator implements com.baoyz.swipemenulistview.SwipeMenuCreator {


    private Context mContext;

    public FileCreator(Context mContext) {
        this.mContext = mContext;
    }


    @Override
    public void create(SwipeMenu menu) {

        switch (menu.getViewType()) {

            case 0:
                create_0(menu);
                break;
            default:
                create_1(menu);
                break;
        }

    }


    private void create_0(SwipeMenu menu){
        SwipeMenuItem openItem = new SwipeMenuItem(mContext.getApplicationContext());
        openItem.setBackground(R.color.light_blue);
        openItem.setWidth(dp2px(60));
        // openItem.setTitle("清空");
        openItem.setTitleSize(18);
        openItem.setTitleColor(Color.WHITE);
        openItem.setIcon(R.drawable.pic_deleteall);
        menu.addMenuItem(openItem);

    }

    private void create_1(SwipeMenu menu){
        SwipeMenuItem openItem = new SwipeMenuItem(mContext.getApplicationContext());
        openItem.setBackground(R.color.light_blue);
        openItem.setWidth(dp2px(60));
        openItem.setTitleSize(18);
        openItem.setTitleColor(Color.WHITE);
        openItem.setIcon(R.drawable.pic_edit);
        menu.addMenuItem(openItem);

        SwipeMenuItem deleteItem = new SwipeMenuItem(mContext.getApplicationContext());
        deleteItem.setBackground(R.color.red);
        deleteItem.setWidth(dp2px(60));
        deleteItem.setTitleSize(18);
        deleteItem.setTitleColor(Color.WHITE);
        deleteItem.setIcon(R.drawable.pic_delete);
        menu.addMenuItem(deleteItem);
    }



    private int dp2px(int value) {

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value,
                mContext.getResources().getDisplayMetrics());
    }


}
