package com.hyphenate.notes.Activity;

import android.content.Intent;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.hyphenate.easeim.R;
import com.hyphenate.notes.Adapter.FileSwipeAdapter;
import com.hyphenate.notes.Dialog.ChooseDialog;
import com.hyphenate.notes.Dialog.InfoDialog;
import com.hyphenate.notes.Dialog.MyOnClickListener;
import com.hyphenate.notes.Manager.DBHelper;
import com.hyphenate.notes.Manager.DBManager;
import com.hyphenate.notes.Util.StringUtil;
import com.hyphenate.notes.View.FileCreator;
import com.hyphenate.notes.View.MsgToast;
import com.hyphenate.notes.model.Note;

import java.util.List;



public class FilesActivity extends BaseActivity implements View.OnClickListener {

    private List<String> folderName;
    private SwipeMenuListView mListView;

    private boolean addFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);
        init();
    }

    @Override
    protected void onStart(){
        super.onStart();
        viewUpdate();
    }

    private void init() {


        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.pic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });


        findViewById(R.id.add_file).setOnClickListener(this);
        viewUpdate();


        boolean isMove = getIntent().getBooleanExtra("move", false);

        if (isMove) {
            final Note moveNote = (Note) getIntent().getSerializableExtra("note");

            TextView title = (TextView) findViewById(R.id.title_toolbar);
            title.setText("choose notes type");
            TextView text = (TextView) findViewById(R.id.text_files);
            text.setVisibility(View.VISIBLE);
            text.setText("move your notes to " + moveNote.getName() + " ...");



            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String chooseFolder = folderName.get(position);

                    DBManager dbManager = new DBManager(FilesActivity.this);
                    dbManager.moveToFolder(chooseFolder,moveNote);
                    finish();
                }
            });


        } else {


            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if(addFolder)return;

                    Intent intent = new Intent();
                    String returnData = folderName.get(position);
                    intent.putExtra("currentFolderName", returnData);
                    setResult(RESULT_OK, intent);
                    finish();
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }
            });
        }



        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {


                final int postin = position;

                if (postin == 0) {
                    final InfoDialog info = new InfoDialog(FilesActivity.this);
                    info.show();
                    info.setTitle("notification");
                    info.setInfo("confirm to delete all the Notes ?");
                    info.setEnableEdit(false);
                    info.setYesListener(new MyOnClickListener() {
                        @Override
                        public void onClick() {
                            dropFolder(postin);
                            info.dismiss();
                        }
                    });
                    return true;
                }

                switch (index) {
                    //edit
                    case 0:
                        final InfoDialog dialog = new InfoDialog(FilesActivity.this);
                        dialog.show();

                        final String select_item = folderName.get(position);

                        dialog.setEnableEdit(true);
                        dialog.setTitle("rename this type");
                        dialog.setInfo(select_item);
                        dialog.setYesListener(new MyOnClickListener() {
                            @Override
                            public void onClick() {
                                String newName = dialog.getInfo();
                                if (!newName.isEmpty()) {
                                    updateFolder(folderName.get(postin), newName);
                                }
                                dialog.dismiss();

                            }
                        });
                        break;
                    case 1:

                        final ChooseDialog deleteDialog = new ChooseDialog(FilesActivity.this);

                        deleteDialog.show();
                        deleteDialog.setTitle("delete the classification?");
                        deleteDialog.setInfo("If only deleting the folder, all notes in it will be moved to trash station");
                        deleteDialog.setChoose1("delete the folder and the notes");
                        deleteDialog.setListener_1(new MyOnClickListener() {
                            @Override
                            public void onClick() {
                                dropFolderAndNote(postin);
                            }
                        });
                        deleteDialog.setChoose2("only delete the notes");
                        deleteDialog.setListener_2(new MyOnClickListener() {
                            @Override
                            public void onClick() {
                                dropFolder(postin);
                            }
                        });
                        deleteDialog.setChoose3("cancel");

                        break;

                    default:
                        break;
                }
                return true;
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_file:
                add();
                break;
            default:
                break;

        }
    }


    private void updateFolder(String oldName,String newName){
        DBHelper.getInstance(this).update_table(oldName,newName);
        MsgToast.showToast(this,"successfully changed");
        viewUpdate();

    }

    private void dropFolderAndNote(int position){

        String folder= folderName.get(position);
        DBHelper dbHelper = DBHelper.getInstance(this);
        dbHelper.drop_table_deep(folder);
        MsgToast.showToast(this,"successfully deleted");
        viewUpdate();

    }
    private void dropFolder(int position){

        String folder= folderName.get(position);
        DBHelper dbHelper = DBHelper.getInstance(this);
        dbHelper.drop_table(folder);
        MsgToast.showToast(this,"successfully deleted");
        viewUpdate();
    }

    private void add(){

        final DBHelper dbHelper = DBHelper.getInstance(this);


        addFolder=true;
        final InfoDialog info = new InfoDialog(this);
        info.show();
        info.setTitle("new class");
        info.setInfo("enter your preferred name");
        info.setEnableEdit(true);
        info.setYesListener(new MyOnClickListener() {
            @Override
            public void onClick() {
                String newFolder = info.getInfo().trim();

                if(!StringUtil.isEmpty(newFolder)){
                    if(folderName.contains(newFolder)){
                        MsgToast.showToast(FilesActivity.this,"the type already existed");
                        return ;
                    }
                    dbHelper.add_table(newFolder);
                    viewUpdate();
                    MsgToast.showToast(FilesActivity.this,"successfully built");
                }
                info.dismiss();
            }
        });
        addFolder=false;
    }


    private void viewUpdate(){
        //getListViewName
        DBManager dbManager = new DBManager(this);

        folderName = dbManager.getTableName();

        listSort();
        //listView
        mListView = (SwipeMenuListView)findViewById(R.id.list_view);

        //menu
        FileCreator creator = new FileCreator(this);
        mListView.setMenuCreator(creator);

        //adapter
        FileSwipeAdapter adapter = new FileSwipeAdapter(FilesActivity.this,folderName);
        adapter.notifyDataSetChanged();
        //setAdapter
        mListView.setAdapter(adapter);
        //botton
        TextView bottom= (TextView)findViewById(R.id.text_bottom);
        bottom.setText(" "+folderName.size()+" ");

    }

    private void listSort(){

        folderName.remove("Notes");
        folderName.add(0,"Notes");
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        switch(keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
               finish();
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                break;
        }
        return super.onKeyUp(keyCode, event);
    }
}
