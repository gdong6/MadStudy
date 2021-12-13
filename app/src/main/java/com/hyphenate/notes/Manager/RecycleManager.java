package com.hyphenate.notes.Manager;


import android.content.Context;
import android.widget.BaseAdapter;

import com.hyphenate.notes.Activity.RecycleActivity;
import com.hyphenate.notes.Dialog.InfoDialog;
import com.hyphenate.notes.Dialog.MyOnClickListener;
import com.hyphenate.notes.View.MsgToast;
import com.hyphenate.notes.model.Note;

import java.util.List;

public class RecycleManager {

    private Context mContext;
    private List<Note> mData;
    private String currentFolderName;
    private BaseAdapter adapter;

    private DBManager dbManager;

    public RecycleManager(Context mContext, List<Note> list, BaseAdapter adapter) {
        this.mContext = mContext;
        this.dbManager = new DBManager(mContext);
        this.mData = list;
        this.currentFolderName = "recycle";
        this.adapter = adapter;
    }


    public void delete(int position){

        dbManager.delete(currentFolderName,mData.get(position));
        update_bottom(position);

    }



    public  void recovery(int position){


        Note note = mData.get(position);
        dbManager.recovery(note);
        update_bottom(position);
    }


    public  void recoveryAll(){

        if(mData.size() == 0 ){
            MsgToast.showToast(mContext,"Empty");
            return;
        }

       for(int i=0;i<mData.size();){
           recovery(i);
       }
    }


    public void clearAll(){


        int dataSize = mData.size();


        if(dataSize == 0 ){
            MsgToast.showToast(mContext,"Empty");
            return;
        }


        final InfoDialog warnDialog = new InfoDialog(mContext);
        warnDialog.show();
        warnDialog.setTitle("Warning");
        warnDialog.setEnableEdit(false);
        warnDialog.setInfo("Permanently delete");
        warnDialog.setYesListener(new MyOnClickListener() {
            @Override
            public void onClick() {
                int number  = dbManager.clearAllFolder(currentFolderName);
                update_bottom(-1);
                MsgToast.showToast(mContext,"delete "+number);
                ((RecycleActivity)mContext).finish();
            }
        });
    }

    private  void update_bottom(int position){

        if(position!=-1) {
            mData.remove(position);
        }
        adapter.notifyDataSetChanged();
        ((RecycleActivity)mContext).update_bottom();
        if(mData.size()==0){
            ((RecycleActivity)mContext).finish();
        }
    }


}
