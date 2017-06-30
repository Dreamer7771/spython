package com.anatoly1410.editorapp.DomainTests;

import com.anatoly1410.editorapp.BuildConfig;
import com.anatoly1410.editorapp.Data.ExtraSnippetsManager;
import com.anatoly1410.editorapp.Data.XmlLangSyntaxParser;
import com.anatoly1410.editorapp.Domain.AutocompletionItemsKeeper;
import com.anatoly1410.editorapp.Domain.Snippet;
import com.anatoly1410.editorapp.Domain.SnippetInsertedElement;
import com.anatoly1410.editorapp.Domain.SnippetManager;
import com.anatoly1410.editorapp.Domain.SnippetTreeElement;
import com.anatoly1410.editorapp.Domain.SuggestTree;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by 1 on 08.06.2017.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AutocompletionItemsKeeper_tests {
        private boolean equalsSnippets(Snippet s1,Snippet s2) {
            return s1.content.equals(s2.content)
                    && s1.tag.equals(s2.tag)
                    && s1.type == s2.type
                    && s1.cursorOffset == s2.cursorOffset;
        }
    @Test
    public void checkLoadFromSnippetManager() {
        XmlLangSyntaxParser mockSyntaxParser = mock(XmlLangSyntaxParser.class);
        SnippetManager mockSnippetMgr = mock(SnippetManager.class);
        ArrayList<SnippetTreeElement> snippetElements = new ArrayList<>();
        snippetElements.add(new SnippetInsertedElement("snippet 1",null,1,new Snippet("tag_sn","content sn",0)));
        snippetElements.add(new SnippetInsertedElement("snippet 2",null,1,new Snippet("tag_sn2","content sn2",3)));
        when(mockSnippetMgr.getSnippets()).thenReturn(snippetElements);
        ExtraSnippetsManager mockExtraSnippetsMgr = mock(ExtraSnippetsManager.class);
        AutocompletionItemsKeeper spyAutocompletionItemsKeeper
                = spy(new AutocompletionItemsKeeper(mockSyntaxParser, mockSnippetMgr,mockExtraSnippetsMgr,false));
        SuggestTree mockTree = mock(SuggestTree.class);
        doReturn(mockTree).when(spyAutocompletionItemsKeeper).getSuggestTree();
        doNothing().when(spyAutocompletionItemsKeeper).init();

        spyAutocompletionItemsKeeper.loadFromSnippetManager();

        ArgumentCaptor<Snippet> sn = ArgumentCaptor.forClass(Snippet.class);
        verify(mockTree,times(2)).put(sn.capture(),anyInt());
        assert(equalsSnippets(new Snippet("tag_sn","content sn",0),sn.getAllValues().get(0)));
        assert(equalsSnippets(new Snippet("tag_sn2","content sn2",3),sn.getAllValues().get(1)));
    }
    @Test
    public void checkLoadExtraSnippets() {
        XmlLangSyntaxParser mockSyntaxParser = mock(XmlLangSyntaxParser.class);
        SnippetManager mockSnippetMgr = mock(SnippetManager.class);
        ExtraSnippetsManager mockExtraSnippetsMgr = mock(ExtraSnippetsManager.class);
        ArrayList<Snippet> snippets = new ArrayList<>();
        snippets.add(new Snippet("tag_sn","content sn",0));
        snippets.add(new Snippet("tag_sn2","content sn2",3));
        when(mockExtraSnippetsMgr.getExtraSnippets()).thenReturn(snippets);

        AutocompletionItemsKeeper spyAutocompletionItemsKeeper
                = spy(new AutocompletionItemsKeeper(mockSyntaxParser, mockSnippetMgr,mockExtraSnippetsMgr,false));
        SuggestTree mockTree = mock(SuggestTree.class);
        doReturn(mockTree).when(spyAutocompletionItemsKeeper).getSuggestTree();
        doNothing().when(spyAutocompletionItemsKeeper).init();

        spyAutocompletionItemsKeeper.loadExtraSnippets();

        ArgumentCaptor<Snippet> sn = ArgumentCaptor.forClass(Snippet.class);
        verify(mockTree,times(2)).put(sn.capture(),anyInt());
        assert(equalsSnippets(new Snippet("tag_sn","content sn",0),sn.getAllValues().get(0)));
        assert(equalsSnippets(new Snippet("tag_sn2","content sn2",3),sn.getAllValues().get(1)));
    }

}
