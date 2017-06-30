package com.anatoly1410.editorapp.Presentation.Interfaces;

import android.view.View;

import com.anatoly1410.editorapp.Domain.AutocompletionListAdapter;
import com.anatoly1410.editorapp.Domain.Snippet;

import java.util.ArrayList;

/**
 * Created by 1 on 16.05.2017.
 */

public interface IAutocompletionManager {
    ArrayList<Snippet> getSnippets();
    void setAutocompletionListAdapter(AutocompletionListAdapter adapter);
    void setAutocompletionEnabled(boolean value);
    boolean getAutocompletionEnabled();
    interface OnSnippetSelectionListener {
        void fireEvent(String snippetContent, int cursorOffset);
    }
    void setSnippetSelectionListener(OnSnippetSelectionListener listener);
    void setLastClickedItem(int pos);
    void clickOnItem(int position);
    void pasteSnippet(Snippet snippet);
    void setMenuContent(Snippet[] snippets);
    void setMenuWidth(int width);
    String getSelectedItemAlias();
}
