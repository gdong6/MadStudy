package com.hyphenate.notes.Activity;


import android.content.Intent;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import androidx.appcompat.widget.Toolbar;

import com.hyphenate.easeim.R;
import com.hyphenate.notes.Dialog.ChooseDialog;
import com.hyphenate.notes.Dialog.MyOnClickListener;
import com.hyphenate.notes.Dialog.ProDialog;
import com.hyphenate.notes.Manager.NoteManager;
import com.hyphenate.notes.Util.ShareUtil;
import com.hyphenate.notes.Util.StringUtil;
import com.hyphenate.notes.View.MsgToast;
import com.hyphenate.notes.model.Note;

import java.util.Timer;
import java.util.TimerTask;

import jp.wasabeef.richeditor.RichEditor;


public class ContentActivity extends BaseActivity  {


    private Note note;

    private NoteManager mNoteManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);


        Intent intent = this.getIntent();
        note = (Note) intent.getSerializableExtra("note");
        init();
    }



    private void init() {
        init_toolbar();
        init_view();
        init_bottom();
    }



    private void init_toolbar() {

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.inflateMenu(R.menu.menu_content);

        TextView  mTitle = (TextView) findViewById(R.id.title_toolbar);
        mTitle.setText(note.getName());

    }


    private void init_view(){

        mNoteManager = new NoteManager(this, note.getFolderName());


        final TextView date = (TextView) findViewById(R.id.date_content);
        date.setText(note.getDate().getDetailDate());


        final TextView location = (TextView) findViewById(R.id.location_content);

        if (StringUtil.isEmpty(note.getLocation()))
            location.setVisibility(View.GONE);
        else {
            location.setText(note.getLocation());
        }

        RichEditor content = (RichEditor)findViewById(R.id.editor);
        content.setHtml(note.getText());
        content.setInputEnabled(false);

        TextView numberFollow = (TextView)findViewById(R.id.numberFollow_content);
         numberFollow.setText(" "+StringUtil.clearHtml(content.getHtml()).length()+" ");

        //Level
        switch (note.getLevel()){
            case Note.GRE_LEVEL:
                findViewById(R.id.level_content).setBackgroundResource(R.drawable.radius_green);
                break;
            case Note.ORA_LEVEL:
                findViewById(R.id.level_content).setBackgroundResource(R.drawable.radius_orange);
                break;
            case Note.RED_LEVEL:
                findViewById(R.id.level_content).setBackgroundResource(R.drawable.radius_red);
                break;
        }

    }

    private void init_bottom() {

        findViewById(R.id.edit_bottom_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit();
            }
        });

        findViewById(R.id.delete_bottom_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNoteManager.deleteNote(note);

                MsgToast.showToast(ContentActivity.this,
                        getResources().getString(R.string.move_recycle));
                finish();
            }
        });

        //findViewById(R.id.move_bottom_content).setOnClickListener(new View.OnClickListener() {
          //  @Override
            //public void onClick(View v) {

              //  Intent intent = new Intent(ContentActivity.this,FilesActivity.class);
                //intent.putExtra("move",true);
                //Bundle bundle = new Bundle();
                //bundle.putSerializable("note",note);
                //intent.putExtras(bundle);
                //startActivity(intent);
                //overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

            //}
        //});



    }


    private  void edit(){

        Intent intent = new Intent(this,CreateActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("note", note);
        intent.putExtras(bundle);

        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        finish();
    }


}
