package com.anatoly1410.editorapp.Domain;

import com.anatoly1410.editorapp.Data.FileManager;
import com.anatoly1410.editorapp.Presentation.Tab;
import com.anatoly1410.editorapp.UtilityMethods;

import java.io.Serializable;

/**
 * Created by 1 on 07.01.2017.
 */
/*Class for keeping data of opened file*/
public class TabContent implements Serializable{
    /*Corresponding TabView*/
    private transient Tab mTabView;
    /*Flag for keeping unsaved state*/
    private boolean mNonSavedChanges;
    /*Path of the file*/
    private String mPath;
    /*Cursor position in file*/
    private int mCursorPos;
    /*Content of the file*/
    private String mContent;
    /*Command history keeper of tab*/
    private CommandHistory commandHistory;

    public String getPath() { return mPath;}
    public Tab getTab(){ return mTabView; }
    public String getContent(){ return mContent;}
    public int getCursorPos(){ return mCursorPos;}
    public void setTabView(Tab tabView)
    {
        mTabView = tabView;
    }
    public void setCursorPos(int cursorPos){
        mCursorPos = cursorPos;
    }
    public boolean isNonSaved() { return mNonSavedChanges;}

    public transient TabManager mTabManager;
    public CommandHistory getCommandHistory() {
        return commandHistory;
    }

    public TabContent(Tab tabView,String path,String content, TabManager tabManager)
    {
        mTabManager = tabManager;
        mTabView = tabView;
        mNonSavedChanges = false;
        mPath = path;
        mCursorPos = 0;
        mContent = content;
        setTabHeaderText();
        commandHistory = new CommandHistory();
    }
    /*Checks if path wasn't an empty string*/
    public boolean isPathNotEmpty()
    {
        return !mPath.equals("");
    }
    public String getHeader()
    {
        String prefix = (mNonSavedChanges)?"*":"";
        String header;
        if(isPathNotEmpty())
        {
            header = prefix+ UtilityMethods.getFileNameFromPath(mPath);
        }else{
            header = mTabManager.getDefaultFileName();
        }
        return header;
    }
    /*Sets header text for tab*/
    private void setTabHeaderText()
    {
        if(mTabView == null)
            return;
        String header = getHeader();
        mTabView.setText(header);
    }
    /*Sets content*/
    public void setContent(String txt)
    {
        mContent = txt;
    }
    /*Sets unsaved flag*/
    public void setUnsavedFlag(boolean isUnsaved)
    {
        mNonSavedChanges = isUnsaved;
        setTabHeaderText();
    }
    /*Sets path*/
    public void setPath(String path)
    {
        mPath = path;
        setTabHeaderText();
    }

    public void clearCommandHistory(){
        commandHistory = new CommandHistory();
    }
}
