package com.anatoly1410.editorapp.Domain;

import java.util.ArrayList;

/**
 * Created by 1 on 27.01.2017.
 */

public class LanguageDescription {
    public String[] keyWords;
    public Snippet[] embeddedSnippets;
    public ArrayList<Pair<String,String>> syntaxBlocks;

    public LanguageDescription(String[] keyWords, Snippet[] embeddedSnippets,
                               ArrayList<Pair<String,String>> syntaxBlocks){
        this.keyWords = keyWords;
        this.embeddedSnippets = embeddedSnippets;
        this.syntaxBlocks = syntaxBlocks;
    }
}
