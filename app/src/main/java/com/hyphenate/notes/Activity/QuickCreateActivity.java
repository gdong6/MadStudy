package com.hyphenate.notes.Activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hyphenate.easeim.R;
import com.hyphenate.notes.Dialog.ProDialog;
import com.hyphenate.notes.Manager.NoteManager;
import com.hyphenate.notes.Manager.PersonalManager;
import com.hyphenate.notes.model.Date;
import com.hyphenate.notes.model.Note;

import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class QuickCreateActivity extends AppCompatActivity {


    private EditText content;
    private String location="";
    private String currentFolderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_create);

        currentFolderName = getIntent().getStringExtra("currentFolderName");
        init_Toolbar();
    }


    private  void init_Toolbar() {



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.pic_deleteall);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            }
        });


        toolbar.inflateMenu(R.menu.menu_quick);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                //done
                String title;
                Date date = new Date();
                if(content.getText().length()>=20)
                    title=content.getText().toString().substring(0,19);
                else {
                    title ="Untitled";
                }


                Note create_note = new Note(title, date,
                        location  , content.getText().toString(), currentFolderName);

                NoteManager noteManager = new NoteManager(QuickCreateActivity.this, currentFolderName);
                noteManager.add(create_note);

                finish();
                return false;
            }
        });

        //init head
        CircleImageView head = (CircleImageView)findViewById(R.id.head_quick);
        Drawable useHead = new PersonalManager(this).getHeadImg();
        if(useHead!=null){
            head.setImageDrawable(useHead);
        }




        final TextView word =(TextView)findViewById(R.id.words_bottom_quick);


        content = (EditText)findViewById(R.id.content_quick);


        content.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                word.setText(" " + s.length()+" ");
            }

            @Override
            public void afterTextChanged(Editable s) {
                for(int i = s.length(); i > 0; i--){
                }
            }
        });



    }





}
