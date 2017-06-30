package com.anatoly1410.editorapp.DataTests.DBHelper;

import com.anatoly1410.editorapp.BuildConfig;
import com.anatoly1410.editorapp.Data.ExtraSnippetsManager;
import com.anatoly1410.editorapp.Domain.Snippet;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.InputStream;
import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by 1 on 06.06.2017.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ExtraSnippetsManager_tests{
    @Test
    public void checkLoadExtraSnippets_oneSnippet(){
        ClassLoader classLoader = getClass().getClassLoader();
        ExtraSnippetsManager spyExtraSnippetsMgr = spy(new ExtraSnippetsManager(false));
        InputStream in = classLoader.getResourceAsStream("xml/snippets_test1.xml");
        when(spyExtraSnippetsMgr.getInputStream(anyString())).thenReturn(in);
        ArrayList<Snippet> snippets = spyExtraSnippetsMgr.loadExtraSnippets();
        assertEquals(snippets.size(),1);
        Snippet snippet = snippets.get(0);
        assertEquals(snippet.tag,"phase");
        assertEquals(snippet.content,"phase()");
        assertEquals(snippet.cursorOffset,6);
        assertEquals(snippet.type,0);
    }
    @Test
    public void checkLoadExtraSnippets_twoSnippets(){
        ClassLoader classLoader = getClass().getClassLoader();
        ExtraSnippetsManager spyExtraSnippetsMgr = spy(new ExtraSnippetsManager(false));
        InputStream in = classLoader.getResourceAsStream("xml/snippets_test2.xml");
        when(spyExtraSnippetsMgr.getInputStream(anyString())).thenReturn(in);
        ArrayList<Snippet> snippets = spyExtraSnippetsMgr.loadExtraSnippets();
        assertEquals(snippets.size(),2);
        Snippet snippet1 = snippets.get(0);
        Snippet snippet2 = snippets.get(1);
        assertEquals(snippet1.tag,"phase");
        assertEquals(snippet1.content,"phase()");
        assertEquals(snippet1.cursorOffset,6);
        assertEquals(snippet1.type,0);
        assertEquals(snippet2.tag,"math");
        assertEquals(snippet2.content,"math");
        assertEquals(snippet2.cursorOffset,-1);
        assertEquals(snippet2.type,1);
    }
    @Test
    public void checkLoadExtraSnippets_withError(){
        ClassLoader classLoader = getClass().getClassLoader();
        ExtraSnippetsManager spyExtraSnippetsMgr = spy(new ExtraSnippetsManager(false));
        InputStream in = classLoader.getResourceAsStream("xml/snippets_test3.xml");
        when(spyExtraSnippetsMgr.getInputStream(anyString())).thenReturn(in);
        ArrayList<Snippet> snippets = spyExtraSnippetsMgr.loadExtraSnippets();
        assertEquals(snippets,null);
    }
}
