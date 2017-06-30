package com.anatoly1410.editorapp.Domain;

import java.util.ArrayList;

/**
 * Created by 1 on 01.05.2017.
 */

public class SnippetFolder extends SnippetTreeElement{
    ArrayList<SnippetTreeElement> mChildren;
    public SnippetFolder(){
        mChildren = new ArrayList<SnippetTreeElement>();
    }

    public SnippetFolder(String name, SnippetTreeElement parent, long id,
                         ArrayList<SnippetTreeElement> children){
        super(name, parent, id);
        mChildren = children;
    }

    public void addChildren(SnippetTreeElement element){
        if(!mChildren.contains(element)){
            mChildren.add(element);
        }
    }

    public ArrayList<SnippetTreeElement> getChilden(){
        return mChildren;
    }
    public void removeChildren(SnippetTreeElement element){
        mChildren.remove(element);
    }
}
