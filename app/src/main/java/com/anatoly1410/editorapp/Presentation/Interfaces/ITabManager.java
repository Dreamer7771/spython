package com.anatoly1410.editorapp.Presentation.Interfaces;

import com.anatoly1410.editorapp.Domain.TabContent;
import com.anatoly1410.editorapp.Domain.TabManager;
import com.anatoly1410.editorapp.Domain.TabManagerState;
import com.anatoly1410.editorapp.Presentation.Tab;
import com.anatoly1410.editorapp.Presentation.TabPanel;

import java.util.ArrayList;

/**
 * Created by 1 on 17.05.2017.
 */

public interface ITabManager {
    ArrayList<TabContent> getTabs();
    TabContent getOpenedTab();
    void setTabPanel(TabPanel tabPanel);
    interface OnTabOpeningListener {
        void fireEvent(String content, int position);
    }
    interface OnWarningMessageEventListener {
        void fireEvent(String messageContent);
    }

    void setTabOpeningListener(OnTabOpeningListener listener);
    void setWarningMessageEventListener(OnWarningMessageEventListener listener);
    void loadTabsContentFromFiles();
    void openDoc(String path);
    void resumeState(Object state);
    TabContent addTab(String path,String content);
    void openTab(Tab tab);
    void openTab(TabContent tabContent);
    void setContentForOpenedTab(String content,boolean setAsNonSaved);
    void saveOpenedTabAsExistingFile();
    void setCursorPosForOpenedTab(int pos);
    void clearTabs();
    void removeTab(TabContent tab);
    void setTabHistoriesToEmpty();
    void saveState();
    boolean resumeTabsState(String filesDir);
}
