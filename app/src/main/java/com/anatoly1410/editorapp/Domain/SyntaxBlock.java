package com.anatoly1410.editorapp.Domain;

/**
 * Created by 1 on 28.01.2017.
 */

public class SyntaxBlock implements Comparable<SyntaxBlock> {
    public String header;
    public int startPos;
    public int type;
    public int getStartPos(){return startPos;}
    public SyntaxBlock(String header, int startPos,int type)
    {
        this.header = header;
        this.startPos = startPos;
        this.type = type;
    }

    public int compareTo(SyntaxBlock obj) {
        return this.startPos - obj.startPos;
    }
}
