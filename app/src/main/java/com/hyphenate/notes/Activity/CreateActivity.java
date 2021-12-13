package com.hyphenate.notes.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.appcompat.widget.Toolbar;

import com.hyphenate.easeim.R;
import com.hyphenate.notes.Dialog.ProDialog;
import com.hyphenate.notes.Manager.NoteManager;
import com.hyphenate.notes.Util.StringUtil;
import com.hyphenate.notes.View.MsgToast;
import com.hyphenate.notes.model.Date;
import com.hyphenate.notes.model.Note;

import java.util.Timer;
import java.util.TimerTask;

import jp.wasabeef.richeditor.RichEditor;


public class CreateActivity extends BaseActivity implements View.OnClickListener {


    private EditText title;

    private RichEditor mEditor;

    private Date date;

    private TextView date_view;

    private TextView location;
    //level
    private int level;

    private String currentFolderName;


    private boolean model;


    private Note edit_Note;


    private Boolean hide = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        init();

    }


    private void init(){
        TextView  model_title = (TextView) findViewById(R.id.title_toolbar);
        Intent intent = this.getIntent();


        currentFolderName = intent.getStringExtra("currentFolderName");

        if(currentFolderName == null){
            model_title.setText("edit your note");
            model = true;
            edit_Note = (Note) intent.getSerializableExtra("note");
            currentFolderName = edit_Note.getFolderName();
        }else{

            model_title.setText("new note");

        }

        init_NoteEditor();
        init_view();
        init_Toolbar();

        if(model){
            init_edit();
        }

    }


    private void init_edit(){

        title.setText( edit_Note.getName() );
        mEditor.setHtml( edit_Note.getText() );
        date_view.setText( edit_Note.getDate().getDetailDate() );
        location.setText( edit_Note.getLocation());

    }


    private  void init_view(){

        title = (EditText) findViewById(R.id.title_create);
        location  = (TextView) findViewById(R.id.location_create);

        date_view = (TextView) findViewById(R.id.date_create);
        date = new Date( );
        date_view.setText(date.getDetailDate());



        Button btn_red = (Button) findViewById(R.id.btn_red);
        btn_red.setOnClickListener(this);
        Button btn_orange = (Button) findViewById(R.id.btn_orange);
        btn_orange.setOnClickListener(this);
        Button btn_green = (Button) findViewById(R.id.btn_green);
        btn_green.setOnClickListener(this);

        init_bottom();
    }

    private void init_bottom(){

       findViewById(R.id.open_bottom_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.bottom_create).setVisibility(View.GONE);
                findViewById(R.id.editor_bottom).setVisibility(View.VISIBLE);
            }
        });



        findViewById(R.id.reBack_bottom_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(hide){

                    findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
                    findViewById(R.id.second).setVisibility(View.VISIBLE);
                    findViewById(R.id.third).setVisibility(View.VISIBLE);
                }else{
                    findViewById(R.id.toolbar).setVisibility(View.GONE);
                    findViewById(R.id.second).setVisibility(View.GONE);
                    findViewById(R.id.third).setVisibility(View.GONE);
                }
                hide = !hide;


            }
        });

    }






    private  void init_NoteEditor() {



        mEditor = (RichEditor) findViewById(R.id.editor);

        mEditor.setFontSize(14);
        mEditor.setPlaceholder("Write your content here");

                findViewById(R.id.action_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.bottom_create).setVisibility(View.VISIBLE);
                findViewById(R.id.editor_bottom).setVisibility(View.GONE);
            }
        });

        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBold();
                MsgToast.showToast(CreateActivity.this,"Bold");
            }
        });


        findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setItalic();
                MsgToast.showToast(CreateActivity.this,"Italic");
            }
        });
        findViewById(R.id.action_underline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setUnderline();
                MsgToast.showToast(CreateActivity.this,"underline");
            }
        });

        findViewById(R.id.action_deleteline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setStrikeThrough();
                MsgToast.showToast(CreateActivity.this,"delete");
            }
        });


        findViewById(R.id.action_checkbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.insertTodo();
                MsgToast.showToast(CreateActivity.this,"checkbox");
            }
        });

        findViewById(R.id.action_menulist).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mEditor.setNumbers();
                MsgToast.showToast(CreateActivity.this,"ordered list");
            }
        });


        findViewById(R.id.action_menubullte).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setBullets();
                MsgToast.showToast(CreateActivity.this,"unordered list");
            }
        });

        findViewById(R.id.action_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignLeft();
                MsgToast.showToast(CreateActivity.this,"align to left");
            }
        });


       findViewById(R.id.action_center).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               mEditor.setAlignCenter();
               MsgToast.showToast(CreateActivity.this,"middle");
           }
       });
        findViewById(R.id.action_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignRight();
                MsgToast.showToast(CreateActivity.this,"align to right");
            }
        });
    }



    private  void init_Toolbar(){

        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.pic_deleteall);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });


        toolbar.inflateMenu(R.menu.menu_create);


        if(model) {
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                        NoteManager noteManager = new NoteManager(CreateActivity.this, currentFolderName);

                        Note newNote = new Note(title.getText().toString(), edit_Note.getDate(),
                                location.getText().toString(), mEditor.getHtml(), currentFolderName, level);

                        noteManager.update(edit_Note, newNote);
                        MsgToast.showToast(CreateActivity.this, "Save");
                        finish();

                    return false;
                }
            });


        }else {
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {


                        String titleName = title.getText().toString();
                        if(StringUtil.isEmpty(titleName)){
                            titleName="Untitled";
                        }
                        Note create_note = new Note("Untitled", date,
                                location.getText().toString(), mEditor.getHtml(),
                                currentFolderName, level);

                        NoteManager noteManager = new NoteManager(CreateActivity.this, currentFolderName);
                        noteManager.add(create_note);
                        hideOrOpenKeyBoard();
                        finish();

                    return false;
                }
            });

        }


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


        StringBuilder sb = new StringBuilder(4);
        switch (v.getId()) {
            case R.id.btn_red:
                level = Note.RED_LEVEL;
                sb.append("Red");
                break;
            case R.id.btn_orange:
                level = Note.ORA_LEVEL;
                sb.append("Orange");
                break;
            case R.id.btn_green:
                level = Note.GRE_LEVEL;
                sb.append("Green");
                break;
        }
        MsgToast.showToast(this, sb.toString());
    }


    private void hideOrOpenKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
