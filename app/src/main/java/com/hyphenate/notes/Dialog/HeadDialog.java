package com.hyphenate.notes.Dialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.hyphenate.easeim.R;

import de.hdodenhof.circleimageview.CircleImageView;



public class HeadDialog extends  android.app.Dialog {


    private CircleImageView img;
    private TextView title;
    private EditText info;

    private Button yes;
    private Button no;

    private Context mContext;

    private MyOnClickListener noListener;
    private MyOnClickListener yesListener;
    private MyOnClickListener imgListener;
    /**
     *
     * @param context
     */
    public HeadDialog(Context context) {
        super(context, R.style.MyDialog);
        mContext=context;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_head);
        setCanceledOnTouchOutside(false);

        initView();
        initEvent();
    }



    protected void initView(){

        yes = (Button) findViewById(R.id.yes_dialog);
        no = (Button) findViewById(R.id.no_dialog);
        img =(CircleImageView)findViewById(R.id.img_dialog);

        info = (EditText) findViewById(R.id.edit_dialog);
        title = (TextView) findViewById(R.id.title_dialog);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                info.setFocusableInTouchMode(true);
                info.setFocusable(true);
                info.requestFocus();
                hideOrOpenKeyBoard();
            }
        });
        title.setText("personal profile");

    }




    protected  void initEvent() {


        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgListener != null) {
                    imgListener.onClick();
                }
            }
        });


        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesListener != null) {
                    yesListener.onClick();
                }
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noListener != null) {
                    noListener.onClick();
                }else{
                    dismiss();
                }
            }
        });

    }

    public String getPersonalName(){
        return info.getText().toString();
    }

    public void setPersonalName(String name) {
        info.setText(name);
        info.setSelection(name.length());
        info.setFocusable(false);
        info.setFocusableInTouchMode(false);
    }


    public void setYesListener(MyOnClickListener yesListener) {
        this.yesListener = yesListener;
    }

    public void setNoListener(MyOnClickListener noListener) {
        this.noListener = noListener;
    }

    public void setImgListener(MyOnClickListener imgListener) {
        this.imgListener = imgListener;
    }

    public void setImg(Drawable img) {
        this.img.setImageDrawable(img);
    }

    public CircleImageView getImg() {
        return img;
    }

    private void hideOrOpenKeyBoard(){


        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
