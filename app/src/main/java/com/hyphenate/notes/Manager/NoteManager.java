package com.hyphenate.notes.Manager;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.BaseAdapter;

import com.hyphenate.easeim.R;
import com.hyphenate.notes.Activity.ContentActivity;
import com.hyphenate.notes.Dialog.EditDialog;
import com.hyphenate.notes.Dialog.MyOnClickListener;
import com.hyphenate.notes.Util.StringUtil;
import com.hyphenate.notes.View.MsgToast;
import com.hyphenate.notes.model.Date;
import com.hyphenate.notes.model.Note;

import java.util.List;



public class NoteManager{

    private Context mContext;
    private List<Note> list;

    private String currentFolderName;
    private BaseAdapter adapter;
    private DBManager dbManager;



    public NoteManager(Context context,String currentFolderName){
        this.mContext=context;
        this.currentFolderName=currentFolderName;
        dbManager=new DBManager(mContext);
    }

    public NoteManager(Context context,String currentFolderName,
                           List<Note> list,BaseAdapter adapter){
        this(context,currentFolderName);
        this.list=list;
        this.adapter=adapter;
    }


    public void addDescription(){

        Note description = new Note(mContext.getResources().getString(R.string.title_des),new Date(),
                mContext.getResources().getString(R.string.app_name),
                mContext.getResources().getString(R.string.content_des),
                currentFolderName);
        add(description);
    }

    public void ItemClick(int position){
        final Note select_item = list.get(position);
        ItemClick(select_item);
    }


    private void ItemClick(Note select_item){

        Intent intent = new Intent(mContext, ContentActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("note", select_item);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
        ((Activity) mContext).overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }


    public void editClick(int position){


        final EditDialog dialog = new EditDialog(mContext);
        dialog.show();

        final Note select_item = list.get(position);

        dialog.setTitle("Edit");
        dialog.setInfo(select_item.getName());

        dialog.setYesListener(new MyOnClickListener() {
            @Override
            public void onClick() {
                String newName = dialog.getInfo();

                if(StringUtil.isEmpty(newName.trim())){
                    MsgToast.showToast(mContext,"Name cannot be empty");
                }else{
                    update(select_item,newName,dialog.getLevel());
                }
                dialog.dismiss();
            }
        });
    }


    public void deleteClick(int position){


        Note select_item = list.get(position);
        delete(select_item);
        MsgToast.showToast(mContext,"Move to trash");

    }


    public void add(Note note){
        dbManager.insert(currentFolderName,note);
    }

    private void delete(Note note) {
        list.remove(note);

        adapter.notifyDataSetChanged();

        dbManager.delete(currentFolderName,note);
    }

    public void deleteNote(Note note) {

        final Note note1 = note;
        dbManager.delete(currentFolderName,note1);

    }


    public void update(Note preNote,Note newNote){
        dbManager.upDate(currentFolderName,preNote,newNote);
    }


    private void update(Note note,String newName,int newLevel){

        Note newNote = note.getClone();
        newNote.setName(newName);
        newNote.setLevel(newLevel);


        dbManager.upDate(currentFolderName,note,newNote);

        if(list!=null) {
            int index = list.indexOf(note);
            list.set(index, newNote);
            adapter.notifyDataSetChanged();
        }
    }


}
