package com.anatoly1410.editorapp.Data;

import java.util.ArrayList;

/**
 * Created by 1 on 03.05.2017.
 */

public class HelpIndexElement {
    public HelpIndexElement parent;
    public String name;
    public ArrayList<String> aliases;
    public ArrayList<HelpIndexElement> children;
    public String filePath;

    public HelpIndexElement(){
        this.children = new ArrayList<>();
        this.aliases = new ArrayList<>();
    }
    public HelpIndexElement(String name, ArrayList<String> aliases,
                            ArrayList<HelpIndexElement> children, String filePath,HelpIndexElement parent){
        this.name = name;
        this.aliases = aliases;
        this.filePath = filePath;
        this.children = children;
        this.parent = parent;
    }
}
