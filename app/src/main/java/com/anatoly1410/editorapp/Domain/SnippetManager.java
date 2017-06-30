package com.anatoly1410.editorapp.Domain;


import com.anatoly1410.editorapp.Domain.Interfaces.IDBHelper;
import com.anatoly1410.editorapp.Presentation.Interfaces.ISnippetManager;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by 1 on 01.05.2017.
 */

public class SnippetManager implements ISnippetManager {

    @Inject
    protected IDBHelper mDBHelper;

    SnippetTreeListAdapter mSnippetTreeListAdapter;
    public void setmSnippetTreeListAdapter(SnippetTreeListAdapter adapter){
        mSnippetTreeListAdapter = adapter;
    }

    ArrayList<SnippetTreeElement> mSnippets;
    public ArrayList<SnippetTreeElement> getSnippets(){
        return mSnippets;
    }
    public ArrayList<SnippetTreeElement> getViewedSnippets(){
        return viewedSnippets;
    }
    ArrayList<SnippetTreeElement> viewedSnippets;
    SnippetTreeElement currentFolder = null;
    private int mLastClickedItemIdx = -1;

    public int getLastClickedItem(){
        return mLastClickedItemIdx;
    }
    public void setLastClickedItem(int idx){
        mLastClickedItemIdx = idx;
    }

    private final int UNDEF_SNIPPET_TYPE = -1;
    private final int FUNC_SNIPPET_TYPE = 0;
    private final int CLASS_SNIPPET_TYPE = 1;
    private final int CONSTANT_SNIPPET_TYPE = 2;

    public int get_UNDEF_SNIPPET_TYPE(){
        return UNDEF_SNIPPET_TYPE;
    }
    public int get_FUNC_SNIPPET_TYPE(){
        return FUNC_SNIPPET_TYPE;
    }
    public int get_CLASS_SNIPPET_TYPE(){
        return CLASS_SNIPPET_TYPE;
    }
    public int get_CONSTANT_SNIPPET_TYPE(){
        return CONSTANT_SNIPPET_TYPE;
    }

    OnViewedSnippetListUpdatingListener mViewedSnippetListUpdatingListener;
    OnLoadSnippetsListener mLoadSnippetsListener;
    OnSnippetInsertionListener mSnippetInsertionListener;

    public void setViewedSnippetListUpdatingListener (OnViewedSnippetListUpdatingListener listener) {
        mViewedSnippetListUpdatingListener = listener;
    }

    public void setLoadSnippetsListener(OnLoadSnippetsListener listener) {
        mLoadSnippetsListener = listener;
    }

    public void setSnippetInsertionListener(OnSnippetInsertionListener listener) {
        mSnippetInsertionListener = listener;
    }

    public void setItemViewCreationListener(SnippetTreeListAdapter.ItemViewCreationListener listener){
        if(mSnippetTreeListAdapter != null){
            mSnippetTreeListAdapter.setItemViewCreationListener(listener);
        }
    }

    public SnippetManager(IDBHelper dbHelper){
        mDBHelper = dbHelper;
        mSnippets = new ArrayList<>();
        viewedSnippets = new ArrayList<>();
        loadSnippets();
    }
    public SnippetManager(IDBHelper dbHelper,boolean loadDuringInit){
        mDBHelper = dbHelper;
        mSnippets = new ArrayList<>();
        viewedSnippets = new ArrayList<>();
        if(loadDuringInit){
            loadSnippets();
        }
    }


    public void clickOnItem(int position){
        SnippetTreeElement snippetElement = mSnippetTreeListAdapter.getItem(position);
        if(snippetElement.getClass() == SnippetFolder.class){
            currentFolder = snippetElement;
            updateViewedSnippetsList();

        }else if(snippetElement.getClass() == SnippetInsertedElement.class){
            pasteSnippet((SnippetInsertedElement) snippetElement);
        }
    }

    public void pasteSnippet(SnippetInsertedElement snippet)
    {
        String content = snippet.getSnippet().content;
        if(mSnippetInsertionListener != null){
            mSnippetInsertionListener.fireEvent(content);
        }
    }

    public void loadSnippets(){
        mSnippets = mDBHelper.getSnippetElements();
        updateViewedSnippetsList();
    }

    public boolean updateSnippetInsertedElement(SnippetInsertedElement element){

        boolean alias_is_unique = true;
        for(SnippetTreeElement curElement: mSnippets){
            if(curElement.Id == element.Id){
                continue;
            }
            if(curElement.getClass() == SnippetInsertedElement.class){
                SnippetInsertedElement snippet = (SnippetInsertedElement)curElement;
                if(snippet.getSnippet().tag.equals((element.getSnippet().tag))){
                    alias_is_unique = false;
                    break;
                }
            }
        }
        if(alias_is_unique){
            mDBHelper.updateSnippetElement(element);
            loadSnippets();
            return true;
        }else{
            return false;
        }
    }

    private void updateViewedSnippetsList(){
        viewedSnippets.clear();
        for(SnippetTreeElement snippetElement: mSnippets){
            if(currentFolder == null){
                if(snippetElement.Parent == null){
                    viewedSnippets.add(snippetElement);
                }
            }else{
                if(snippetElement.Parent != null
                        && snippetElement.Parent.Id == currentFolder.Id){
                    viewedSnippets.add(snippetElement);
                }
            }
        }

        if(mSnippetTreeListAdapter != null){
            mSnippetTreeListAdapter.notifyDataSetChanged();
        }

        if(mViewedSnippetListUpdatingListener != null){
            mViewedSnippetListUpdatingListener.fireEvent();
        }
    }

    public void renameLastSelectedSnippetFolder(String newName){
        int idx = getLastClickedItem();
        if(idx < 0 || idx >= viewedSnippets.size()){
            return;
        }

        SnippetTreeElement snippetFolder = viewedSnippets.get(idx);
        snippetFolder.Name = newName;
        mDBHelper.updateSnippetElement(snippetFolder);
        loadSnippets();
    }

    public void goBackFromCurrentFolder(){
        if(currentFolder == null)
            return;
        currentFolder = currentFolder.Parent;
        updateViewedSnippetsList();
    }

    public boolean addSnippetInsertedElement(String name,String alias, String textvalue){
        boolean alias_is_unique = true;
        ArrayList<SnippetTreeElement> snippets = getSnippets();
        for(SnippetTreeElement element: snippets){
            if(element.getClass() == SnippetInsertedElement.class){
                SnippetInsertedElement snippet = (SnippetInsertedElement)element;
                if(snippet.mSnippet.tag.equals(alias)){
                    alias_is_unique = false;
                    break;
                }
            }
        }
        if(alias_is_unique){
            mDBHelper.addSnippetElement(name,alias,currentFolder,textvalue,false);
            loadSnippets();
            return true;
        }else{
            return false;
        }

    }
    public void addSnippetFolder(String name){
        mDBHelper.addSnippetElement(name,"",currentFolder,"",true);

        loadSnippets();
    }

    public String getCurrentSnippetFolderName(){
        if(currentFolder == null){
            return null;
        }else{
            return currentFolder.Name;
        }
    }

    public SnippetTreeElement getViewedSnippetByIdx(int idx){
        if(idx < 0 || idx >= viewedSnippets.size()){
            return null;
        }
        return viewedSnippets.get(idx);
    }

    public void clearViewedSnippets(){
        for(SnippetTreeElement element:viewedSnippets){
            removeSnippet(element);
        }
        loadSnippets();
    }

    public void removeViewedSnippetByIdx(int idx){
        if(idx < 0 || idx >= viewedSnippets.size()){
            return;
        }

        SnippetTreeElement snippetForRemove = viewedSnippets.get(idx);
        removeSnippet(snippetForRemove);
        for(SnippetTreeElement element: mSnippets){
            if(element.Parent != null){
                if(element.Parent.Id == snippetForRemove.Id){
                    removeSnippet(snippetForRemove);
                }
            }
        }
        loadSnippets();
    }

    private void removeSnippet(SnippetTreeElement snippetElement){
        for(SnippetTreeElement element: mSnippets){
            if(element.Parent != null){
                if(element.Parent.Id == snippetElement.Id){
                    removeSnippet(element);
                }
            }
        }
        mDBHelper.removeSnippetElement(snippetElement);
    }

    public boolean isLastSelectedItemIsFolder(){
        int idx = getLastClickedItem();

        SnippetTreeElement snippetElement = viewedSnippets.get(idx);
        if(snippetElement.getClass() == SnippetFolder.class){
            return true;
        }else{
            return false;
        }
    }

    public String getLastSelectedSnippetName(){
        int idx = getLastClickedItem();
        if(idx < 0 || idx >= viewedSnippets.size()){
            return null;
        }
        return viewedSnippets.get(idx).Name;
    }

}
