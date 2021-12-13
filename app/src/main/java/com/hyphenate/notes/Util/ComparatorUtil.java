package com.hyphenate.notes.Util;


import com.hyphenate.notes.model.Note;

import java.util.Comparator;


public class ComparatorUtil implements Comparator<Note> {

    @Override
    public int compare(Note o1, Note o2) {
        return  o1.compareTo(o2);
    }

}
