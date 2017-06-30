package com.anatoly1410.editorapp.Presentation.Interfaces;

import com.anatoly1410.editorapp.Domain.Snippet;

import java.util.ArrayList;

/**
 * Created by 1 on 16.05.2017.
 */

public interface IAutocompletionItemsKeeper {
    void init();
    void loadFromSnippetManager();
    Snippet[] getWordsForPrefix(String prefix);
}
