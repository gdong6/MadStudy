package com.hyphenate.notes.Manager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {


    private static final String CREATE_NOTE =

            "create table Notes ("
                    + "item blob)";

    private static final String CREATE_RECYCLE =
            "create table recycle ("
                    + "item blob)";

    private Context mContext;


    private static DBHelper dbHelper = null;


    private DBHelper(Context context) {

        super(context, "Notes.db", null, 1);
        this.mContext = context;
    }



    public static DBHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new DBHelper(context);
        }
        return dbHelper;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NOTE);
        db.execSQL(CREATE_RECYCLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TO DO
    }


    public void add_table(String name) {

        String create =
                "create table " + name + "("
                        + "item blob)";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(create);
    }


    public void drop_table_deep(String name){
        String drop =
                "drop table " + name;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(drop);

    }


    public void drop_table(String name) {

        String drop =
                "drop table " + name;
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        switch (name) {

            case "Notes":
                String delete = "delete from " + name;
                String insert1 = " insert into recycle select  * from " + name;
                db.execSQL(insert1);
                db.execSQL(delete);
                break;
            case "recycle":
                db.execSQL(drop);
                db.execSQL(CREATE_RECYCLE);

            default:
                String insert = " insert into recycle select  * from " + name;

                db.execSQL(insert);
                db.execSQL(drop);
                break;
        }


    }


    public void update_table(String oldName, String newName) {

        String update = " alter table " + oldName + " rename to " + newName;


        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(update);
    }


    public boolean folderIsExist(String name) {

        boolean result = false;
        if (name != null) {

            try {
                SQLiteDatabase db = this.getReadableDatabase();
                String sql = "select count(*) as c from sqlite_master where type ='table' " +
                        "and name ='" +name+ "' ";
                Cursor cursor = db.rawQuery(sql, null);
                if (cursor.moveToNext()) {
                    int count = cursor.getInt(0);
                    if (count > 0) {
                        result = true;
                    }
                }
                cursor.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;

    }
}

