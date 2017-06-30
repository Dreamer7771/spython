package com.anatoly1410.editorapp.IntegrationTests;

import android.content.Context;

import com.anatoly1410.editorapp.BuildConfig;
import com.anatoly1410.editorapp.Data.FileManager;
import com.anatoly1410.editorapp.Domain.TabManager;
import com.anatoly1410.editorapp.Presentation.Interfaces.ITabManager;
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
public class TabManager_FileManager_tests {
    @Test
    public void check(){
        ClassLoader classLoader = getClass().getClassLoader();
        Context mockContext = mock(Context.class);
        TabPanel mockTabPanel = mock(TabPanel.class);
        Tab mockTab = mock(Tab.class);
        when(mockTabPanel.addTab(anyString())).thenReturn(mockTab);
        ITabManager.OnTabOpeningListener mockListener = mock(ITabManager.OnTabOpeningListener.class);
        FileManager spyFileMgr = spy(new FileManager(mockContext));
        TabManager spyTabMgr = spy(new TabManager(mockContext,spyFileMgr));
        spyTabMgr.setTabOpeningListener(mockListener);
        spyTabMgr.setTabPanel(mockTabPanel);
        String filePath =  classLoader.getResource("raw/test_file.py").getPath();
        spyTabMgr.openDoc(filePath);
        verify(mockListener).fireEvent("def main():\n    pass\n",0);
    }
}
