package com.anatoly1410.editorapp.Domain;

import java.util.ArrayList;

/**
 * Created by 1 on 01.05.2017.
 */

public abstract class SnippetTreeElement {
    public String Name;
    public SnippetTreeElement Parent;
    public long Id;
    public SnippetTreeElement(){

    }
    public SnippetTreeElement(String name, SnippetTreeElement parent, long id){
        this.Name = name;
        this.Parent = parent;
        this.Id = id;
    }

}
