package com.anatoly1410.editorapp.Domain.Interfaces;

import com.anatoly1410.editorapp.Domain.LanguageDescription;
import com.anatoly1410.editorapp.Domain.Pair;
import com.anatoly1410.editorapp.Domain.Snippet;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

/**
 * Created by 1 on 16.05.2017.
 */

public interface IXmlLangSyntaxParser {
    LanguageDescription parseXml();
    String[] getKeyWords();
    Snippet[] getEmbeddedSnippets();
    ArrayList<Pair<String, String>> getSyntaxBlocks();
}
