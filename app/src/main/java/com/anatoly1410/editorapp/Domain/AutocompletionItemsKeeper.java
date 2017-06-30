package com.anatoly1410.editorapp.Domain;

import com.anatoly1410.editorapp.Domain.Interfaces.IExtraSnippetsManager;
import com.anatoly1410.editorapp.Domain.Interfaces.IXmlLangSyntaxParser;
import com.anatoly1410.editorapp.Presentation.Interfaces.IAutocompletionItemsKeeper;
import com.anatoly1410.editorapp.Presentation.Interfaces.ISnippetManager;

import java.util.ArrayList;

/**
 * Created by 1 on 26.12.2016.
 */
public class AutocompletionItemsKeeper implements IAutocompletionItemsKeeper {
    public SuggestTree tree;
    public final int maxWordsDisplayed = 70;

    private ISnippetManager mSnippetManager;
    private IExtraSnippetsManager mExtraSnippetsManager;
    private IXmlLangSyntaxParser mXmlLangParser;

    public SuggestTree getSuggestTree(){
        return tree;
    }

    public AutocompletionItemsKeeper(IXmlLangSyntaxParser xmlLangParser, ISnippetManager snippetManager,
                                     IExtraSnippetsManager extraSnippetsManager){
        mSnippetManager = snippetManager;
        mExtraSnippetsManager = extraSnippetsManager;
        mXmlLangParser = xmlLangParser;
        init();

    }
    public AutocompletionItemsKeeper(IXmlLangSyntaxParser xmlLangParser, ISnippetManager snippetManager,
                                     IExtraSnippetsManager extraSnippetsManager, boolean initDuringCostruction){
        mSnippetManager = snippetManager;
        mExtraSnippetsManager = extraSnippetsManager;
        mXmlLangParser = xmlLangParser;
        if(initDuringCostruction){
            init();
        }
    }

    public void init(){
        Snippet[] embeddedSnippets = mXmlLangParser.getEmbeddedSnippets();

        tree = new SuggestTree(maxWordsDisplayed);
        for(int i=0;i<embeddedSnippets.length;++i)
        {
            tree.put(embeddedSnippets[i],1);
        }
        loadFromSnippetManager();
        loadExtraSnippets();
    }


    public void loadFromSnippetManager(){
        SuggestTree suggestTree = getSuggestTree();
        ArrayList<SnippetTreeElement> snippets = mSnippetManager.getSnippets();
        if(snippets == null){
            return;
        }
        for(int i=0;i<snippets.size();++i)
        {
            SnippetTreeElement element = snippets.get(i);

            if(element.getClass() == SnippetFolder.class){
                continue;
            }
            Snippet snippet = ((SnippetInsertedElement)element).getSnippet();
            if(snippet.tag.length() > 0){
                suggestTree.put(snippet,1);
            }
        }
    }


    public void loadExtraSnippets(){
        SuggestTree suggestTree = getSuggestTree();
        ArrayList<Snippet> snippets = mExtraSnippetsManager.getExtraSnippets();
        if(snippets == null){
            return;
        }
        for(int i=0;i<snippets.size();++i)
        {
            Snippet element = snippets.get(i);

            suggestTree.put(element,1);
        }
    }


    public Snippet[] getWordsForPrefix(String prefix)
    {
        if(prefix.equals(""))
        {
            return new Snippet[0];
        }
        SuggestTree.Node node = tree.getAutocompleteSuggestions(prefix);
        if(node == null)
        {
            return new Snippet[0];
        }
        Snippet[] res = new Snippet[node.listLength()];
        for(int i=0;i<node.listLength();++i){
            SuggestTree.Entry entry = node.getSuggestion(i);
            res[i] = entry.getSnippet();
        }
        return res;
    }

}
