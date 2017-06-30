package com.anatoly1410.editorapp.Presentation.Interfaces;

import android.app.Activity;

import com.anatoly1410.editorapp.Domain.SnippetInsertedElement;
import com.anatoly1410.editorapp.Domain.SnippetTreeElement;
import com.anatoly1410.editorapp.Domain.SnippetTreeListAdapter;

import java.util.ArrayList;

/**
 * Created by 1 on 16.05.2017.
 */

public interface ISnippetManager {
    void setmSnippetTreeListAdapter(SnippetTreeListAdapter adapter);
    ArrayList<SnippetTreeElement> getSnippets();
    ArrayList<SnippetTreeElement> getViewedSnippets();
    int getLastClickedItem();
    void setLastClickedItem(int idx);
    interface OnViewedSnippetListUpdatingListener {
        void fireEvent();
    }
    interface OnLoadSnippetsListener {
        void fireEvent();
    }
    interface OnSnippetInsertionListener {
        void fireEvent(String snippetContent);
    }
    void setViewedSnippetListUpdatingListener (OnViewedSnippetListUpdatingListener listener);
    void setLoadSnippetsListener(OnLoadSnippetsListener listener);
    void setSnippetInsertionListener(OnSnippetInsertionListener listener);
    void setItemViewCreationListener(SnippetTreeListAdapter.ItemViewCreationListener listener);
    void clickOnItem(int position);
    boolean updateSnippetInsertedElement(SnippetInsertedElement element);
    void renameLastSelectedSnippetFolder(String newName);
    void goBackFromCurrentFolder();
    boolean addSnippetInsertedElement(String name,String alias, String textvalue);
    void addSnippetFolder(String name);
    String getCurrentSnippetFolderName();
    SnippetTreeElement getViewedSnippetByIdx(int idx);
    void clearViewedSnippets();
    void removeViewedSnippetByIdx(int idx);
    boolean isLastSelectedItemIsFolder();
    String getLastSelectedSnippetName();
    int get_UNDEF_SNIPPET_TYPE();
    int get_FUNC_SNIPPET_TYPE();
    int get_CLASS_SNIPPET_TYPE();
    int get_CONSTANT_SNIPPET_TYPE();
}
