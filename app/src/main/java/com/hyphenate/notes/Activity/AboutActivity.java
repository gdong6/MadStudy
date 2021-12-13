package com.hyphenate.notes.Activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.hyphenate.easeim.R;
import com.hyphenate.notes.Dialog.ChooseDialog;
import com.hyphenate.notes.Dialog.MyOnClickListener;
import com.hyphenate.notes.Util.ShareUtil;
import com.hyphenate.notes.View.MsgToast;



public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        init_toolbar();
        init_table();
    }


    private void init_toolbar(){
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);


        mToolbar.setNavigationIcon(R.drawable.pic_back);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });
    }


    private void init_table(){

        findViewById(R.id.t1_table).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtil.shareText(AboutActivity.this,
                        AboutActivity.this.getResources().getString(R.string.shareApp));
            }
        });


        findViewById(R.id.content_about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseCopyContent();
            }
        });
        findViewById(R.id.info_about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseCopyContent();
            }
        });
    }

    private void chooseCopyContent(){


        final ChooseDialog dialog = new ChooseDialog(this);



        dialog.setChoose2("address of resource");
        dialog.setListener_2(new MyOnClickListener() {
            @Override
            public void onClick() {
                ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                manager.setText("");

                MsgToast.showToast(AboutActivity.this,"");
            }
        });


        dialog.setChoose3("cancel");
    }

}
