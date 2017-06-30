package com.anatoly1410.editorapp.DomainTests;

import com.anatoly1410.editorapp.BuildConfig;
import com.anatoly1410.editorapp.Data.DBHelper;
import com.anatoly1410.editorapp.Domain.Snippet;
import com.anatoly1410.editorapp.Domain.SnippetInsertedElement;
import com.anatoly1410.editorapp.Domain.SnippetManager;
import com.anatoly1410.editorapp.Domain.SnippetTreeListAdapter;
import com.anatoly1410.editorapp.Presentation.Interfaces.ISnippetManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by 1 on 13.06.2017.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SnippetManager_test {
    @Test
    public void checkClickOnItem(){
        DBHelper dbHepler = mock(DBHelper.class);
        SnippetTreeListAdapter mockAdapter = mock(SnippetTreeListAdapter.class);
        SnippetInsertedElement snipInsElement
                = new SnippetInsertedElement("snippet",null,3,new Snippet("tag","",0));
        when(mockAdapter.getItem(3)).thenReturn(snipInsElement);
        SnippetManager spySnippetMgr = spy(new SnippetManager(dbHepler,false));
        spySnippetMgr.setmSnippetTreeListAdapter(mockAdapter);
        spySnippetMgr.clickOnItem(3);
        verify(spySnippetMgr).pasteSnippet(snipInsElement);
    }
    @Test
    public void checkPasteSnippet(){
        DBHelper dbHepler = mock(DBHelper.class);
        SnippetManager spySnippetMgr = spy(new SnippetManager(dbHepler,false));
        ISnippetManager.OnSnippetInsertionListener mockListener
                = mock(ISnippetManager.OnSnippetInsertionListener.class);
        SnippetInsertedElement snInsElement
                = new SnippetInsertedElement("",null,0,new Snippet("","content"));
        spySnippetMgr.setSnippetInsertionListener(mockListener);
        spySnippetMgr.pasteSnippet(snInsElement);
        verify(mockListener).fireEvent("content");
    }
    @Test
    public void checkUpdateSnippetInsertedElement(){
        DBHelper dbHepler = mock(DBHelper.class);
        SnippetManager spySnippetMgr = spy(new SnippetManager(dbHepler,false));
        doNothing().when(spySnippetMgr).loadSnippets();
        SnippetInsertedElement snInsElement
                = new SnippetInsertedElement("",null,0,new Snippet("","content"));
        spySnippetMgr.updateSnippetInsertedElement(snInsElement);
        verify(dbHepler).updateSnippetElement(snInsElement);
    }
    @Test
    public void checkAddSnippetInsertedElement(){
        DBHelper dbHepler = mock(DBHelper.class);
        SnippetManager spySnippetMgr = spy(new SnippetManager(dbHepler,false));
        spySnippetMgr.addSnippetInsertedElement("name","tag","content");
        verify(dbHepler).addSnippetElement("name","tag",null,"content",false);
    }
}
