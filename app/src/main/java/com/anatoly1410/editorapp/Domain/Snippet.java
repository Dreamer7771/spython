package com.anatoly1410.editorapp.Domain;

/**
 * Created by 1 on 04.01.2017.
 */
/*Keeps info for insert operation*/
public class Snippet {
    /*Text that should be replaced by content*/
    public String tag;
    /*Text that should be inserted instead tag*/
    public String content;
    /*Cursor position offset after insertion; counted from the tag first character in text*/
    public int cursorOffset;
    /*type of snippet; -1 - no type*/
    public int type;
    public Snippet()
    {
        this.cursorOffset = - 1;
        this.type = -1;
    }
    public Snippet(String tag, String content)
    {
        this.tag = tag;
        this.content = content;
        this.cursorOffset = - 1;
        this.type = - 1;
    }
    public Snippet(String tag, String content,int cursorOffset)
    {
        this.tag = tag;
        this.content = content;
        this.cursorOffset = cursorOffset;
        this.type = - 1;
    }
    public String getTag() { return tag;}
    public int getCursorOffset()
    {
        if(cursorOffset >= 0)
            return cursorOffset;
        else
            return content.length();
    }

}