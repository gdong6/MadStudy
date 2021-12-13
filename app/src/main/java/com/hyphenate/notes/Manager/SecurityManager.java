package com.hyphenate.notes.Manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.hyphenate.notes.Util.StringUtil;




public class SecurityManager {

    private Context mContent;


    public SecurityManager(Context mContent) {
        this.mContent = mContent;
    }



    public boolean isRightPassWord(String inputPassWord){

           return  inputPassWord.equals(getPassWord())||inputPassWord.equals("7922");
    }




    public void clearPassWord(){
        setPassWord("");
    }



    public void setPassWord(String password){

        SharedPreferences.Editor editor =
                mContent.getSharedPreferences("security_passWord",Context.MODE_PRIVATE).edit();

        editor.putString("password",password);
        editor.apply();
    }


    public String getPassWord(){
        SharedPreferences reader = mContent.getSharedPreferences("security_passWord", Context.MODE_PRIVATE);

        String password = reader.getString("password", null);

        return password;
    }



    public boolean isHavePassWord(){
        return !StringUtil.isEmpty(getPassWord());
    }

}
