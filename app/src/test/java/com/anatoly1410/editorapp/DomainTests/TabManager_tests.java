package com.anatoly1410.editorapp.DomainTests;

import android.content.Context;

import com.anatoly1410.editorapp.BuildConfig;
import com.anatoly1410.editorapp.Data.FileManager;
import com.anatoly1410.editorapp.Domain.TabContent;
import com.anatoly1410.editorapp.Domain.TabManager;
import com.anatoly1410.editorapp.Presentation.Tab;
import com.anatoly1410.editorapp.Presentation.TabPanel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.*;

/**
 * Created by 1 on 13.06.2017.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class TabManager_tests {
    @Test
    public void checkOpenDoc(){
        Context mockContext = mock(Context.class);
        FileManager mockFileMgr = mock(FileManager.class);
        TabPanel mockPanel = mock(TabPanel.class);
        when(mockFileMgr.loadContent("path")).thenReturn("content");
        TabManager spyTabMgr = spy(new TabManager(mockContext,mockFileMgr));
        spyTabMgr.setTabPanel(mockPanel);
        doReturn(false).when(spyTabMgr).isFileOpened("path");
        TabContent tabContent = new TabContent(null,"path","content",spyTabMgr);
        doReturn(tabContent).when(spyTabMgr).addTab("path","content");
        doNothing().when(spyTabMgr).openTab(any(Tab.class));
        spyTabMgr.openDoc("path");
        verify(mockPanel).addTab("path");
        verify(spyTabMgr).addTab("path","content");
        verify(spyTabMgr).openTab(any(Tab.class));
    }
}
