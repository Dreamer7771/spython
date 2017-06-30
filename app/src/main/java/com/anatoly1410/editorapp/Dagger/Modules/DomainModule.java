package com.anatoly1410.editorapp.Dagger.Modules;


import android.content.Context;

import com.anatoly1410.editorapp.Dagger.Scopes.DomainScope;
import com.anatoly1410.editorapp.Data.ExtraSnippetsManager;
import com.anatoly1410.editorapp.Domain.AutocompletionManager;
import com.anatoly1410.editorapp.Domain.BlockedTextExtractor;
import com.anatoly1410.editorapp.Domain.CommandHistoryManager;
import com.anatoly1410.editorapp.Domain.HelpManager;
import com.anatoly1410.editorapp.Domain.HighlightSyntaxManager;
import com.anatoly1410.editorapp.Domain.Interfaces.IDBHelper;
import com.anatoly1410.editorapp.Domain.Interfaces.IExtraSnippetsManager;
import com.anatoly1410.editorapp.Domain.Interfaces.IFileManager;
import com.anatoly1410.editorapp.Domain.Interfaces.IHelpLoadManager;
import com.anatoly1410.editorapp.Domain.Interfaces.ISettingsManager;
import com.anatoly1410.editorapp.Domain.Interfaces.IXmlLangSyntaxParser;
import com.anatoly1410.editorapp.Domain.AutocompletionItemsKeeper;
import com.anatoly1410.editorapp.Domain.MultiBuffer;
import com.anatoly1410.editorapp.Domain.QPythonScriptRunner;
import com.anatoly1410.editorapp.Domain.SnippetManager;
import com.anatoly1410.editorapp.Domain.TabManager;
import com.anatoly1410.editorapp.Presentation.Interfaces.IAutocompletionManager;
import com.anatoly1410.editorapp.Presentation.Interfaces.IBlockedTextExtractor;
import com.anatoly1410.editorapp.Presentation.Interfaces.ICommandHistoryManager;
import com.anatoly1410.editorapp.Presentation.Interfaces.IHelpManager;
import com.anatoly1410.editorapp.Presentation.Interfaces.IHighlightSyntaxManager;
import com.anatoly1410.editorapp.Presentation.Interfaces.IAutocompletionItemsKeeper;
import com.anatoly1410.editorapp.Presentation.Interfaces.IMultiBuffer;
import com.anatoly1410.editorapp.Presentation.Interfaces.IQPythonScriptRunner;
import com.anatoly1410.editorapp.Presentation.Interfaces.ISnippetManager;
import com.anatoly1410.editorapp.Presentation.Interfaces.ITabManager;

import java.text.ParseException;

import dagger.Module;
import dagger.Provides;

/**
 * Created by 1 on 10.05.2017.
 */
@Module
public class DomainModule {

    @Provides
    @DomainScope
    public IMultiBuffer provideMultiBuffer(IDBHelper dBHelper)
    {
        return new MultiBuffer(dBHelper);
    }

    @Provides
    @DomainScope
    public IAutocompletionManager provideAutocompletionManager()
    {
        return new AutocompletionManager();
    }

    @Provides
    @DomainScope
    public ICommandHistoryManager provideCommandHistoryManager(ITabManager tabManager,
                                                               ISettingsManager settingsManager)
    {
        return new CommandHistoryManager(tabManager, settingsManager);
    }

    @Provides
    @DomainScope
    public IHelpManager provideHelpManager(IHelpLoadManager helpLoadManager)
    {
        return new HelpManager(helpLoadManager);
    }

    @Provides
    @DomainScope
    public IAutocompletionItemsKeeper provideKeyWordsKeeper(IXmlLangSyntaxParser xmlLangSyntaxParser,
                                                            ISnippetManager snippetManager,
                                                            IExtraSnippetsManager extraSnippetsManager)
    {
        return new AutocompletionItemsKeeper(xmlLangSyntaxParser, snippetManager,extraSnippetsManager);
    }

    @Provides
    @DomainScope
    public IQPythonScriptRunner provideQPythonScriptRunner(Context context)
    {
        return new QPythonScriptRunner(context);
    }

    @Provides
    @DomainScope
    public ISnippetManager provideSnippetManager(IDBHelper dBHelper)
    {
        return new SnippetManager(dBHelper);
    }

    @Provides
    @DomainScope
    public IHighlightSyntaxManager provideHighlightSyntaxManager(IXmlLangSyntaxParser xmlLangSyntaxParser)
    {
        return new HighlightSyntaxManager(xmlLangSyntaxParser);
    }

    @Provides
    @DomainScope
    public IBlockedTextExtractor provideBlockedTextExtractor(ISettingsManager settingsManager)
    {
        return new BlockedTextExtractor(settingsManager);
    }
    @Provides
    @DomainScope
    public ITabManager provideTabManager(Context context, IFileManager fileManager)
    {
        return new TabManager(context, fileManager);
    }
    @Provides
    @DomainScope
    public IExtraSnippetsManager provideExtraSnippetsManager()
    {
        return new ExtraSnippetsManager();
    }

}
