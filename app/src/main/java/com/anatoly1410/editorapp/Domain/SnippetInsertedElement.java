package com.anatoly1410.editorapp.Domain;

/**
 * Created by 1 on 01.05.2017.
 */

public class SnippetInsertedElement extends SnippetTreeElement{
    Snippet mSnippet;
    public SnippetInsertedElement(){
        mSnippet = new Snippet();
    }
    public SnippetInsertedElement(String name, SnippetTreeElement parent, long id,
                                  Snippet snippet){
        super(name, parent, id);
        mSnippet = snippet;
    }

    public Snippet getSnippet(){
        return mSnippet;
    }
}
