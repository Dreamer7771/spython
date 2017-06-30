package com.anatoly1410.editorapp.Domain;

import android.app.Activity;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.anatoly1410.editorapp.Data.FileManager;
import com.anatoly1410.editorapp.Presentation.AutocompletionMenu;
import com.anatoly1410.editorapp.Presentation.CEditText;
import com.anatoly1410.editorapp.Presentation.CScrollView;
import com.anatoly1410.editorapp.Presentation.Interfaces.IAutocompletionManager;
import com.anatoly1410.editorapp.Presentation.MainActivity;
import com.anatoly1410.editorapp.R;

import java.util.ArrayList;

/**
 * Created by 1 on 08.05.2017.
 */

public class AutocompletionManager implements IAutocompletionManager {

    private ArrayList<Snippet> mSnippets;

    public  ArrayList<Snippet> getSnippets(){
        return mSnippets;
    }

    AutocompletionListAdapter autocompletionListAdapter;

    public void setAutocompletionListAdapter(AutocompletionListAdapter adapter){
        autocompletionListAdapter = adapter;
    }
    private int mLastClickedItemIdx;

    private boolean isAutocompletionEnabled;

    public void setAutocompletionEnabled(boolean value){
        isAutocompletionEnabled = value;
    }
    public boolean getAutocompletionEnabled(){
        return isAutocompletionEnabled;
    }

    OnSnippetSelectionListener mSnippetSelectionListener;

    public void setSnippetSelectionListener(OnSnippetSelectionListener listener) {
        mSnippetSelectionListener = listener;
    }

    public void setLastClickedItem(int pos){
        mLastClickedItemIdx = pos;
    }

    public AutocompletionManager(){
        mSnippets = new ArrayList<Snippet>();
        isAutocompletionEnabled = true;
    }

    public void clickOnItem(int position){
        if(!isAutocompletionEnabled){
            return;
        }
        Snippet snippetElement = autocompletionListAdapter.getItem(position);
        mLastClickedItemIdx = position;
        pasteSnippet(snippetElement);
    }

    public void pasteSnippet(Snippet snippet){
        if(mSnippetSelectionListener != null){
            mSnippetSelectionListener.fireEvent(snippet.content, snippet.cursorOffset);
        }
    }

    public void setMenuContent(Snippet[] snippets){
        autocompletionListAdapter.clear();
        autocompletionListAdapter.addAll(snippets);
    }


    public void setMenuWidth(int width){
        if(autocompletionListAdapter == null)
            return;

        autocompletionListAdapter.setAutocompletionMenuWidth(width);
    }

    public String getSelectedItemAlias(){
        Snippet snippet = autocompletionListAdapter.getItem(mLastClickedItemIdx);
        return snippet.tag;
    }
}
