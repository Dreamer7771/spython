package com.anatoly1410.editorapp.Domain.Interfaces;

import com.anatoly1410.editorapp.Domain.BufferFragment;
import com.anatoly1410.editorapp.Domain.SnippetTreeElement;

import java.util.ArrayList;

/**
 * Created by 1 on 16.05.2017.
 */

public interface IDBHelper {
    long addMultibufElement(int ordernum, String textvalue);
    long addSnippetElement(String name, String alias, SnippetTreeElement parentSnippet,
                                  String textvalue, boolean isfolder);
    void updateMultibufElement(int idx, BufferFragment fragment, String newContent);
    void updateSnippetElement(SnippetTreeElement snippet);
    void removeMultibufElement(BufferFragment bufferFragment);
    void removeSnippetElement(SnippetTreeElement snippet);
    void clearMultibuffer();
    ArrayList<BufferFragment> getBufferFragments();
    ArrayList<SnippetTreeElement> getSnippetElements();
}
