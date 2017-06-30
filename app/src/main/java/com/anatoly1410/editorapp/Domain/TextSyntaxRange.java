package com.anatoly1410.editorapp.Domain;

/**
 * Created by 1 on 16.03.2017.
 */

public class TextSyntaxRange {
    public int start;
    public int end;
    public int type;

    public static final int COMMENTS_TYPE = 0;
    public static final int STRING_TYPE = 1;
    public static final int CONSTANT_TYPE = 2;
    public static final int DECORATOR_TYPE = 3;
    public static final int KEY_WORD_TYPE = 4;

    public TextSyntaxRange(int start, int end, int type){
        this.start = start;
        this.end = end;
        this.type = type;
    }
}
