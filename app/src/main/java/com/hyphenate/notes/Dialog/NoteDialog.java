package com.hyphenate.notes.Dialog;


import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hyphenate.easeim.R;
import com.hyphenate.notes.model.Note;



public class NoteDialog  extends  android.app.Dialog implements View.OnClickListener{

    private TextView title;
    private EditText info;
    private Button yes;
    private Button no;
    private Context mContext;


    private int level = Note.GRE_LEVEL;

    private MyOnClickListener noListener;
    private MyOnClickListener yesListener;


    public NoteDialog(Context context){
        super(context, R.style.MyDialog);
        mContext = context;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_note);
        setCanceledOnTouchOutside(false);
        initView();
        initEvent();
    }


    private void initView() {

        yes = (Button) findViewById(R.id.yes_dialog);
        no = (Button) findViewById(R.id.no_dialog);


        info = (EditText) findViewById(R.id.edit_dialog);
        setEnableEdit(false);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEnableEdit(true);
            }
        });
        title = (TextView) findViewById(R.id.title_dialog);



    Button btn_red = (Button) findViewById(R.id.btn_red);
    btn_red.setOnClickListener(this);
    Button btn_orange = (Button) findViewById(R.id.btn_orange);
    btn_orange.setOnClickListener(this);
    Button btn_green = (Button) findViewById(R.id.btn_green);
    btn_green.setOnClickListener(this);
}

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_red:
            case R.id.btn_green:
            case R.id.btn_orange:
                change_level(v);
                break;
        }

    }




    private void change_level(View v) {

        switch (v.getId()) {
            case R.id.btn_red:
                level = Note.RED_LEVEL;
                break;
            case R.id.btn_orange:
                level = Note.ORA_LEVEL;
                break;
            case R.id.btn_green:
                level = Note.GRE_LEVEL;
                break;
        }
    }







    protected  void initEvent() {

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














        private void hideOrOpenKeyBoard(){
        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }


        public void setYesListener (MyOnClickListener listener){

        this.yesListener = listener;
        }

        public void setNoListener (MyOnClickListener listener){
        this.noListener = listener;
        }





    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getInfo() {
        String rInfo="";
        if (info!=null){
        rInfo=info.getText().toString();
        }
        return rInfo;
        }



    public void setTitle(String title) {
        this.title.setText(title);
        }



    public void setInfo(String msg){
        if(info!=null){
        info.setHint(msg);
        }
    }


    public void setEnableEdit (boolean b){

        if(b) {
        info.setFocusableInTouchMode(true);
        info.setFocusable(true);
        info.requestFocus();

        }
        else {
        info.setFocusable(false);
        info.setFocusableInTouchMode(false);
        hideOrOpenKeyBoard();
        }
    }



}

