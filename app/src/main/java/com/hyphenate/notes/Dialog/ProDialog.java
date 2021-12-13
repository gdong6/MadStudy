package com.hyphenate.notes.Dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.hyphenate.easeim.R;



public class ProDialog extends ProgressDialog{

    private String text;

    public ProDialog(Context context,String text) {
        super(context, R.style.ProgressDialog);
        this.text=text;
    }



    @Override
    protected void onCreate(Bundle  savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        init(getContext(),text);
    }

    private void init(Context context,String text){


        setCancelable(false);
        setCanceledOnTouchOutside(false);

        setContentView(R.layout.dialog_progress);

        TextView info = (TextView) findViewById(R.id.tv_load_dialog);
        info.setText(text);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
    }

    @Override
    public void show()
    {
        super.show();
    }
}
