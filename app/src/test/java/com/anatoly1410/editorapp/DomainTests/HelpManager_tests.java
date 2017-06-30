package com.anatoly1410.editorapp.DomainTests;

import com.anatoly1410.editorapp.BuildConfig;
import com.anatoly1410.editorapp.Data.HelpIndexElement;
import com.anatoly1410.editorapp.Data.HelpLoadManager;
import com.anatoly1410.editorapp.Domain.HelpManager;
import com.anatoly1410.editorapp.Domain.HelpTreeListAdapter;
import com.anatoly1410.editorapp.Domain.Pair;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

/**
 * Created by 1 on 09.06.2017.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class HelpManager_tests {
    @Test
    public void checkLoadHelp(){
        HelpLoadManager mockHelpLoadManager = mock(HelpLoadManager.class);
        ArrayList<HelpIndexElement> helpIndex = new ArrayList<>();
        HelpIndexElement hElement = new HelpIndexElement();
        hElement.name = "help 1";
        hElement.aliases.add("alias1_1");
        hElement.aliases.add("alias1_2");
        helpIndex.add(hElement);
        HelpIndexElement hElement2 = new HelpIndexElement();
        hElement2.name = "help 2";
        hElement2.aliases.add("alias2_1");
        hElement2.aliases.add("alias2_2");
        helpIndex.add(hElement2);
        when(mockHelpLoadManager.LoadHelpIndex()).thenReturn(helpIndex);
        HelpManager spyHelpMgr = spy(new HelpManager(mockHelpLoadManager));

        spyHelpMgr.LoadHelp();
        verify(spyHelpMgr).putInHelpElementHash(eq("alias1_1"),any(HelpIndexElement.class));
        verify(spyHelpMgr).putInHelpElementHash(eq("alias1_2"),any(HelpIndexElement.class));
        verify(spyHelpMgr).putInHelpElementHash(eq("alias2_1"),any(HelpIndexElement.class));
        verify(spyHelpMgr).putInHelpElementHash(eq("alias2_2"),any(HelpIndexElement.class));
    }
    @Test
    public void checkOpenHelpFile(){
        HelpLoadManager mockHelpLoadManager = mock(HelpLoadManager.class);
        HelpManager spyHelpManager = spy(new HelpManager(mockHelpLoadManager));
        HelpIndexElement indexElement
                = new HelpIndexElement("name",null,new ArrayList<HelpIndexElement>(),"path",null);
        doReturn(indexElement).when(spyHelpManager).getHelpFileIndexElement("alias");
        spyHelpManager.openHelpFile("alias");
        verify(mockHelpLoadManager).LoadHelpFileContent("path");
    }
    @Test
    public void checkUpdateViewedSnippetsList(){
        HelpTreeListAdapter mockAdapter = mock(HelpTreeListAdapter.class);
        HelpLoadManager mockHelpLoadManager = mock(HelpLoadManager.class);
        ArrayList<HelpIndexElement> children = new ArrayList<>();
        HelpIndexElement indexElement = new HelpIndexElement("",null,children,"",null);
        HelpManager spyHelpManager = spy(new HelpManager(mockHelpLoadManager));
        spyHelpManager.setHelpTreeListAdapter(mockAdapter);
        doReturn(indexElement).when(spyHelpManager).getCurrentHelpIndexElement();
        spyHelpManager.updateViewedSnippetsList();
        verify(mockAdapter).clear();
        verify(mockAdapter).addAll(eq(children));
        verify(mockAdapter).notifyDataSetChanged();
    }
}
