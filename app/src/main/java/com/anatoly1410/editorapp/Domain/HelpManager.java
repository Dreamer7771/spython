package com.anatoly1410.editorapp.Domain;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.anatoly1410.editorapp.Data.FileManager;
import com.anatoly1410.editorapp.Data.HelpIndexElement;
import com.anatoly1410.editorapp.Data.HelpLoadManager;
import com.anatoly1410.editorapp.Domain.Interfaces.IHelpLoadManager;
import com.anatoly1410.editorapp.Presentation.Interfaces.IHelpManager;
import com.anatoly1410.editorapp.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by 1 on 02.05.2017.
 */

public class HelpManager implements IHelpManager {

    IHelpLoadManager mHelpLoadManager;

    private ArrayList<HelpIndexElement> mHelpIndex;
    private ArrayList<HelpIndexElement> viewedHelpIndex;
      public ArrayList<HelpIndexElement> getViewedHelpIndex(){
        return viewedHelpIndex;
    }

    private HashMap<String, HelpIndexElement> helpElementsHash;

    private HelpIndexElement currentHelpIndexElement;
    private HelpIndexElement currentDisplayedHelpElement;

    private final String rootHelpDirectoryName = "Help content";

    public String getRootHelpDirectoryName() {
        return rootHelpDirectoryName;
    }

    HelpTreeListAdapter mHelpTreeListAdapter;

    private int mLastClickedItemIdx = -1;
    public int getLastClickedItemIdx(){
        return mLastClickedItemIdx;
    }
    public void setLastClickedItemIdx(int idx){
        mLastClickedItemIdx = idx;
    }

    public void setItemViewCreationListener(HelpTreeListAdapter.ItemViewCreationListener listener) {
        if (mHelpTreeListAdapter == null)
            throw new NullPointerException("helpTreeListAdapter is null - ItemViewCreationListener registration impossible");

        mHelpTreeListAdapter.setItemViewCreationListener(listener);
    }

    public void setHelpTreeListAdapter(HelpTreeListAdapter adapter){
        mHelpTreeListAdapter = adapter;
    }

    public boolean HelpIsLoaded(){
        return mHelpLoadManager.HelpIsLoaded();
    }

    OnViewedHelpListUpdatingListener mViewedHelpListUpdatingListener;


    OnHelpItemSelectedListener mHelpItemSelectedListener;

    public void setHelpItemSelectedListener(OnHelpItemSelectedListener listener) {
        mHelpItemSelectedListener = listener;
    }

    public HelpManager(IHelpLoadManager helpLoadManager){
        mHelpLoadManager = helpLoadManager;
    }

    public void putInHelpElementHash(String alias, HelpIndexElement element){
        helpElementsHash.put(alias, element);
    }

    public void LoadHelp() {
        mHelpIndex = mHelpLoadManager.LoadHelpIndex();
        if(mHelpIndex == null){
            return;
        }
        viewedHelpIndex = new ArrayList<>();
        helpElementsHash = new HashMap<>();
        for (HelpIndexElement element : mHelpIndex) {
            for (String alias : element.aliases) {
                putInHelpElementHash(alias, element);
            }
        }
    }

    public String openHelpFile(String alias) {

        HelpIndexElement helpElement = getHelpFileIndexElement(alias);

        if (helpElement != null) {
            if (helpElement.children.size() > 0) {
                currentHelpIndexElement = helpElement;
            } else {
                currentHelpIndexElement = helpElement.parent;
            }
        }

        currentDisplayedHelpElement = helpElement;

        if (currentDisplayedHelpElement != null) {
            return mHelpLoadManager.LoadHelpFileContent(currentDisplayedHelpElement.filePath);
        } else {
            return "";
        }
    }

    public String getHelpFileContentByPath(String path) {
        return mHelpLoadManager.LoadHelpFileContent(path);
    }

    public HelpIndexElement getHelpFileIndexElement(String alias) {
        HelpIndexElement element = helpElementsHash.get(alias);
        return element;
    }

    public boolean aliasExists(String alias) {
        return helpElementsHash.containsKey(alias);
    }

    public void setHelpActivity(Activity helpActivity) {
        updateViewedSnippetsList();
    }

    public void clickOnItem(int idx) {
        HelpIndexElement clickedElement = viewedHelpIndex.get(idx);

        if (clickedElement.children.size() > 0) {
            currentHelpIndexElement = clickedElement;
        } else {
            currentHelpIndexElement = clickedElement.parent;
        }

        currentDisplayedHelpElement = clickedElement;

        if (mHelpItemSelectedListener != null) {
            mHelpItemSelectedListener.fireEvent(currentHelpIndexElement, currentDisplayedHelpElement);
        }
        updateViewedSnippetsList();
    }

    public void gotoBack() {
        if (currentHelpIndexElement != null) {
            currentHelpIndexElement = currentHelpIndexElement.parent;
            currentDisplayedHelpElement = currentHelpIndexElement;
        }
        if (mHelpItemSelectedListener != null) {
            mHelpItemSelectedListener.fireEvent(currentHelpIndexElement, currentDisplayedHelpElement);
        }
        updateViewedSnippetsList();
    }

    public void updateViewedSnippetsList() {
        if(mHelpTreeListAdapter == null){
            return;
        }
        mHelpTreeListAdapter.clear();

        ArrayList<HelpIndexElement> viewedElements;
        if (getCurrentHelpIndexElement() != null) {
            viewedElements = getCurrentHelpIndexElement().children;
        } else {
            viewedElements = new ArrayList<>();
            for (HelpIndexElement element : mHelpIndex) {
                if (element.parent == null) {
                    viewedElements.add(element);
                }
            }
        }

        mHelpTreeListAdapter.addAll(viewedElements);
        if (mViewedHelpListUpdatingListener != null) {
            mViewedHelpListUpdatingListener.fireEvent();
        }
        mHelpTreeListAdapter.notifyDataSetChanged();
    }

    public HelpIndexElement getCurrentHelpIndexElement() {
        return currentHelpIndexElement;
    }

    public HelpIndexElement getCurrentDisplayedHelpElement() {
        return currentDisplayedHelpElement;
    }

}
