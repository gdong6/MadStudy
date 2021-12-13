package com.hyphenate.notes.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * string util
 */
public class StringUtil {

    /**
     * if empty string
     * @param str
     * @return
     */
    public static boolean isEmpty(String str){
        if(str==null || str.trim().equals("") )
            return true;
        else return false;
    }


    /**
     * clear html tag
     * @param html
     * @return
     */
    public static String clearHtml(String html) {

        String regEx_html = "<[^>]+>";
        String regEx_space = "\\s*|\t|\r|\n";

        if(isEmpty(html)) return html;
        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(html);
        html = m_html.replaceAll("");
        Pattern p_space = Pattern.compile(regEx_space, Pattern.CASE_INSENSITIVE);
        Matcher m_space = p_space.matcher(html);
        html = m_space.replaceAll("");
        html = html.replaceAll("&nbsp;","");
        return html.trim(); //
    }

    /**
     * replace return key
     * @param text
     * @return
     */
    public static String clearEnter(String text){

        if(isEmpty(text)) return text;
        return text.replaceAll("\\n"," ");

    }
}
