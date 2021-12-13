package com.hyphenate.notes.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.baoyz.swipemenulistview.*;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.navigation.NavigationView;
import com.hyphenate.notes.Adapter.MainSwipeAdapter;
import com.hyphenate.notes.Dialog.ChooseDialog;
import com.hyphenate.notes.Dialog.HeadDialog;
import com.hyphenate.notes.Dialog.MyOnClickListener;
import com.hyphenate.notes.Manager.DBManager;
import com.hyphenate.notes.Manager.NoteManager;
import com.hyphenate.notes.Manager.PersonalManager;
import com.hyphenate.notes.Util.AppUtil;
import com.hyphenate.notes.Util.ComparatorUtil;
import com.hyphenate.notes.Util.StringUtil;
import com.hyphenate.notes.View.MainCreator;
import com.hyphenate.notes.View.MainScrollview;
import com.hyphenate.notes.View.MsgToast;
import com.hyphenate.notes.View.SwipeListView;
import com.hyphenate.notes.model.Note;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import com.hyphenate.easeim.R;


public class NoteMainActivity extends AppCompatActivity {


    private NoteManager noteManager;


    private DrawerLayout mDrawer;
    private SwipeListView mListView;
    private List<Note> mData;


    private String currentFolderName ="Notes";


    private long backPressFirst = 0;

    private CircleImageView navHeadImg;
    private CircleImageView dialogIcon;
    private Uri pic_uri;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main_note);
        actionbarReset();
        init();
    }


    @Override
    protected void onResume() {
        super.onResume();
        listView_setting();
        findViewById(R.id.action_menu).bringToFront();

    }

    protected void onStart(){
        super.onStart();
        listView_setting();
        findViewById(R.id.action_menu).bringToFront();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }




    public void init (){

        listView_setting();

        fab_setting();
    }






    public void actionbarReset(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        resetHeadIcon();


        TextView toolbar_title = (TextView) findViewById(R.id.title_toolbar) ;
        toolbar_title.setText(currentFolderName);

        toolbar.inflateMenu(R.menu.menu_main);


    }


    private void personalSet(final View view){

        final PersonalManager personal = new PersonalManager( this);
        final Drawable headImg = personal.getHeadImg();
        final String name = personal.getPersonName();


        final TextView userName = (TextView) view.findViewById(R.id.useName_nav);
        final CircleImageView userImg = (CircleImageView) view.findViewById(R.id.useImg_nav);


        if(headImg!=null) userImg.setImageDrawable(headImg);
        userName.setText(name);


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final HeadDialog dialog = new HeadDialog(NoteMainActivity.this);
                dialog.show();


                dialog.setPersonalName(name);
                if(headImg!=null) dialog.setImg(headImg);


                dialog.setImgListener(new MyOnClickListener() {
                   @Override
                   public void onClick() {
                       picOrPhoto();
                       navHeadImg = userImg;
                       dialogIcon = dialog.getImg();

                   }
                });

                dialog.setYesListener(new MyOnClickListener(){
                    @Override
                    public void onClick() {
                        String newName = dialog.getPersonalName();
                        if(! newName.equals( name)){
                            personal.savePersonName(newName);
                            userName.setText(newName);
                        }
                        saveImg();
                        dialog.setImg(headImg);


                        resetHeadIcon();
                      dialog.dismiss();
                    }
                });
                mDrawer.closeDrawers();
            }
        };



       userImg.setOnClickListener(listener);
        (view.findViewById(R.id.useEdit_nav)).setOnClickListener(listener);
 }


    private void resetHeadIcon(){


        CircleImageView main_head = (CircleImageView) findViewById(R.id.headicon_main);

        Drawable db = new PersonalManager(this).getHeadImg();

        if(db!=null) {
            main_head.setImageDrawable(db);
        }

    }



    private  void picOrPhoto(){


        final ChooseDialog dialog = new ChooseDialog(this);

        dialog.show();
        dialog.setTitle("please choose");
        dialog.setInfo("choose your own profile photo");


        dialog.setChoose1("take a photo");
        dialog.setListener_1(new MyOnClickListener() {
            @Override
            public void onClick() {
                MsgToast.showToast(NoteMainActivity.this,"we don't support this function right now");
            }
        });

        dialog.setChoose2("choose from album");
        dialog.setListener_2(new MyOnClickListener() {
            @Override
            public void onClick() {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");

                startActivityForResult(intent, 2);
            }
        });
        dialog.setChoose3("do nothing");
    }


    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String returnData = data.getStringExtra("currentFolderName");
                    if (!StringUtil.isEmpty(returnData)) {
                        currentFolderName = returnData;
                        TextView toolbar_title = (TextView) findViewById(R.id.title_toolbar);
                        toolbar_title.setText(currentFolderName);
                    }
                }
                listView_setting();
                break;
            case 2:

                if (data != null) {

                    pic_uri = data.getData();

                    try {
                        Bitmap pic = MediaStore.Images.Media.getBitmap
                                (getContentResolver(), pic_uri);
                        dialogIcon.setImageBitmap(pic);
                        navHeadImg.setImageBitmap(pic);
                    } catch (IOException e) {
                        MsgToast.showToast(NoteMainActivity.this, "failed to search");
                    }
                }
                break;
            default:
                break;
        }

    }



    private void saveImg(){
        if(pic_uri!=null) {

            PersonalManager personal = new PersonalManager(this);
            personal.setHeadImg(pic_uri);
        }
    }



    private void hide_fabMenu(){

        FloatingActionsMenu menu = (FloatingActionsMenu)findViewById(R.id.action_menu);
        if(menu!=null) menu.collapse();
    }


    public void listView_setting(){

        hide_fabMenu();

        mData = new DBManager(this).search(currentFolderName);

        Collections.sort(mData, new ComparatorUtil());

        MainSwipeAdapter adapter = new MainSwipeAdapter(this,mData);

        noteManager = new NoteManager(this, currentFolderName,mData,adapter);

        MainCreator mainCreator = new MainCreator(this);

        mListView =(SwipeListView) findViewById(R.id.list_view);
        mListView.setMenuCreator(mainCreator);
        mListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        mListView.setAdapter(adapter);

        MainScrollview scrollview = (MainScrollview) findViewById(R.id.main_scrollView);
        scrollview.setOnScrollListener(new MainScrollview.ScrollViewListener() {
            @Override
            public void onScroll(int dy) {
                if (dy > 0) {
                    showOrHideFab(false);
                } else if (dy <= 0 ) {
                    showOrHideFab(true);
                }
            }
        });

        setDescription();
        view_Listener();
        emptyListCheck();
    }

    private void setDescription(){
        if(AppUtil.haveDescription(this)){
            noteManager.addDescription();
        }
    }

    private void fab_setting(){


        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.action_note);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FloatingActionsMenu menu = (FloatingActionsMenu)findViewById(R.id.action_menu);
                menu.collapse();

                Intent intent = new Intent(NoteMainActivity.this,CreateActivity.class);
                intent.putExtra("currentFolderName",currentFolderName);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

                findViewById(R.id.action_menu).bringToFront();
            }
        });

        FloatingActionButton fab_quick = (FloatingActionButton)findViewById(R.id.action_quick);

        fab_quick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FloatingActionsMenu menu = (FloatingActionsMenu)findViewById(R.id.action_menu);
                menu.collapse();

                Intent intent = new Intent(NoteMainActivity.this,QuickCreateActivity.class);
                intent.putExtra("currentFolderName",currentFolderName);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

                findViewById(R.id.action_menu).bringToFront();
            }
        });
    }


    private void showOrHideFab(boolean show){

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.action_menu);

        if(show){
            fab.setVisibility(View.VISIBLE);
        }else{
            fab.setVisibility(View.GONE);
        }

    }

    public void view_Listener() {

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent,View view,int position,long id){
                noteManager.ItemClick(position);


            }
        });


        mListView.setOnMenuItemClickListener( new SwipeMenuListView.OnMenuItemClickListener(){

            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                switch (index){
                    //edit
                    case 0:
                        noteManager.editClick(position);
                        break;
                    case 1:


                        Intent intent = new Intent(NoteMainActivity.this,FilesActivity.class);
                        intent.putExtra("move",true);

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("note",mData.get(position));
                        intent.putExtras(bundle);
                        startActivity(intent);

                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                        //move
                        break;
                    case 2:
                        noteManager.deleteClick(position);
                        emptyListCheck();
                    default:
                        break;
                }
                return true;
            }
        });

    }


    public void emptyListCheck(){



        int number = 0;
        if(mData!=null){
            number=mData.size();
        }

        if(number == 0) {
            //hide and show
            mListView.setVisibility(View.GONE);
            RelativeLayout empty = (RelativeLayout) findViewById(R.id.empty);
            empty.setVisibility(View.VISIBLE);

            TextView info = (TextView) findViewById(R.id.text_empty);
            info.setText(R.string.main_empty_info);
        }else{
            mListView.setVisibility(View.VISIBLE);
            RelativeLayout empty = (RelativeLayout) findViewById(R.id.empty);
            empty.setVisibility(View.GONE);
        }
    }



}


