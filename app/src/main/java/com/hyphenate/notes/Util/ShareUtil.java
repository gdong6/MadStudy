package com.hyphenate.notes.Util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.view.View;


import com.hyphenate.easeim.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class ShareUtil {


    public  static  void shareImg(Activity activity){


        String image = shoot(activity);
        Intent intent  = new Intent(Intent.ACTION_SEND);
        File file = new File(image);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        intent.setType("image/jpeg");
        Intent chooser = Intent.createChooser(intent,
                activity.getResources().getString(R.string.shareImg));
        if(intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(chooser);
        }
    }


    public static  void shareText(Activity activity,String text){

        Intent intent=new Intent(Intent.ACTION_SEND);

        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, activity.getResources().getString(R.string.share));
        intent.putExtra(Intent.EXTRA_TEXT,text+"\n"+activity.getResources().getString(R.string.shareFrom));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(Intent.createChooser(intent,activity.getResources().getString(R.string.shareTo)));

    }


    public static String shoot(Activity a) {
        String strFileName = "sdcard/" + String.valueOf(System.currentTimeMillis()) + ".png";
        ShareUtil.savePic(ShareUtil.takeScreenShot(a), strFileName);
        return strFileName;
    }


    private static Bitmap takeScreenShot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay()
                .getHeight();
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }


    private static void savePic(Bitmap b, String strFileName) {

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(strFileName);
            b.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
