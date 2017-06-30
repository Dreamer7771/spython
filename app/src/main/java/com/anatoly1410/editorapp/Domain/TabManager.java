package com.anatoly1410.editorapp.Domain;

import android.content.Context;

import com.anatoly1410.editorapp.Domain.Interfaces.IFileManager;
import com.anatoly1410.editorapp.Presentation.Interfaces.ITabManager;
import com.anatoly1410.editorapp.Presentation.Tab;
import com.anatoly1410.editorapp.Presentation.TabPanel;
import com.anatoly1410.editorapp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by 1 on 17.05.2017.
 */

public class TabManager implements ITabManager {
    private transient Context mContext;
    /*TabContent collection of opened tabs*/
    private ArrayList<TabContent> mTabs;
    /*Opened tab*/
    private TabContent mOpenedTab;
    /*Default file name
    * Uses for creating new file; is taken from resources*/
    private String  mDefaultFileName;
    public String getDefaultFileName(){ return mDefaultFileName;}

    /*TabPanel view*/
    private transient TabPanel mTabPanel;
    public transient IFileManager mFileManager;
    public ArrayList<TabContent> getTabs() { return mTabs; }
    public TabContent getOpenedTab(){ return mOpenedTab; }

    private final String tabsDataFileName = "file_data.dat";

    public TabManager(Context context,
                      IFileManager fileManager){
        mContext = context;
        mTabs = new ArrayList<TabContent>();
        mFileManager = fileManager;
        mDefaultFileName = "New file";
    }
    public void setTabPanel(TabPanel tabPanel)
    {
        mTabPanel = tabPanel;
    }

    OnTabOpeningListener mTabOpeningListener;
    OnWarningMessageEventListener mWarningMessageEventListener;

    public void setTabOpeningListener(OnTabOpeningListener listener) {
        mTabOpeningListener = listener;
    }
    public void setWarningMessageEventListener(OnWarningMessageEventListener listener) {
        mWarningMessageEventListener = listener;
    }
    /*Loads serialized tabs data*/
    public void loadTabsContentFromFiles()
    {
        if(mContext == null)
        {
            return;
        }
        String fileName = mContext.getResources().getString(R.string.tabsFileName);
        ArrayList<TabContent> forDelete = new ArrayList<>();
        for(TabContent tabContent: mTabs)
        {
            File file = new File(tabContent.getPath());
            if(file.exists())
            {
                String content = mFileManager.loadContent(tabContent.getPath());
                tabContent.setContent(content);
                tabContent.setUnsavedFlag(false);
            }else if(tabContent.getPath().equals("")){
                tabContent.setContent("");
            }else{
                forDelete.add(tabContent);
            }
        }
        for(TabContent tabContent:forDelete)
        {
            mTabs.remove(tabContent);
        }
        if(forDelete.contains(mOpenedTab)) {
            mOpenedTab = null;
        }
    }
    /*Opens text file from path*/
    public void openDoc(String path)
    {
        if(isFileOpened(path))
        {
            if(mWarningMessageEventListener != null){
                mWarningMessageEventListener.fireEvent("File was opened");
            }
            return;
        }
        String content = mFileManager.loadContent(path);
        TabContent tabContent = addTab(path, content);
        Tab tabView = mTabPanel.addTab(tabContent.getHeader());
        tabContent.setTabView(tabView);
        openTab(tabView);
    }

    public void saveState(){
        Object state = getStateForSave();
        String fileName = tabsDataFileName;
        mFileManager.saveToFile(fileName,state);
    }
    public boolean resumeTabsState(String filesDir){
        String tabsDataFilePath = filesDir+"/"+tabsDataFileName;
        File dataStateFile = new File(filesDir+"/"+tabsDataFileName);
        if(dataStateFile.exists())
        {
            Object aa =  mFileManager.loadFromFile(tabsDataFileName);
            TabManagerState tabState = (TabManagerState)mFileManager.loadFromFile(tabsDataFileName);
            resumeState(tabState);
            if(MyApplication.firstMainActivityInit)
            {
                loadTabsContentFromFiles();
                setTabHistoriesToEmpty();
            }
            if(getTabs().size() == 0)
            {
                TabContent tabContent = addTab("","");
                openTab(getOpenedTab());
            }else if(getOpenedTab() == null){
                openTab(getTabs().get(0));
            }else{
                openTab(getOpenedTab());
            }
            return true;
        }else{
            return false;
        }
    }
    /*Returns FileManager state for save then activity is paused*/
    private Object getStateForSave()
    {
        TabManagerState state = new TabManagerState();
        state.mTabs = mTabs;
        state.mOpenedTab = mOpenedTab;
        return state;
    } /*Resumes state*/
    public void resumeState(Object stateObj) {
        TabManagerState state = (TabManagerState)stateObj;
        mTabs = state.mTabs;
        if(mTabs != null){
            for(TabContent tabContent : mTabs){
                tabContent.mTabManager = this;
            }
        }

        mOpenedTab = state.mOpenedTab;
    }
    /*Checks if file with path 'path' was opened*/
    public boolean isFileOpened(String path)
    {
        for(TabContent tab:mTabs)
        {
            if(tab.getPath().equals(path))
            {
                return true;
            }
        }
        return false;
    }
    /*Adds tab to collection of TabContent*/
    public TabContent addTab(String path,String content){
        mTabs.add(new TabContent(null,path,content, this));
        return mTabs.get(mTabs.size() - 1);
    }
    /*Opens tab: sets text for CEditText, sets new value for mOpenedTab
    * tab - tab view of tab that should be opened*/
    public void openTab(Tab tab)
    {
        for(TabContent t : mTabs)
        {
            if(t.getTab().equals(tab))
            {
                mOpenedTab = t;
                fireTapOpeningEvent(t.getContent(),t.getCursorPos());
            }else{

            }
        }
    }
    /*Opens tab: sets text for CEditText, sets new value for mOpenedTab
    * tabContent - TabContent object of tab that should be opened*/
    public void openTab(TabContent tabContent)
    {
        for(TabContent t : mTabs)
        {
            if(t.equals(tabContent))
            {
                mOpenedTab = t;
                fireTapOpeningEvent(t.getContent(),t.getCursorPos());
                break;
            }
        }
    }
    /*Sets text and cursor position for CEditText*/
    private void fireTapOpeningEvent(String text,int cursorPosition)
    {
        if(mTabOpeningListener != null){
            mTabOpeningListener.fireEvent(text, cursorPosition);
        }
    }
    /*Sets new content for TabContent object of opened tab*/
    public void setContentForOpenedTab(String content,boolean setAsNonSaved)
    {
        if(mOpenedTab == null)
            return;
        mOpenedTab.setContent(content);
        if(setAsNonSaved){
            mOpenedTab.setUnsavedFlag(true);
        }
    }
    /*Saves opened tab as existing file*/
    public void saveOpenedTabAsExistingFile()
    {
        mFileManager.saveTabAsExistingFile(mOpenedTab.getPath(), mOpenedTab.getContent());
        mOpenedTab.setUnsavedFlag(false);
    }
    /*Sets cursor pos for opened tab*/
    public void setCursorPosForOpenedTab(int pos)
    {
        if(mOpenedTab == null)
            return;
        mOpenedTab.setCursorPos(pos);
    }
    /*Clear TabContent collection*/
    public void clearTabs()
    {
        mTabs.clear();
    }
    /*Remove tab object from TabContent collection*/
    public void removeTab(TabContent tab)
    {
        mTabs.remove(tab);
    }
    public void setTabHistoriesToEmpty(){
        if(mTabs == null){
            return;
        }

        for(TabContent tabContent:mTabs){
            tabContent.clearCommandHistory();
        }
    }
}
