package com.anatoly1410.editorapp.Dagger.Modules;

import android.content.Context;

import com.anatoly1410.editorapp.Data.DBHelper;
import com.anatoly1410.editorapp.Data.FileManager;
import com.anatoly1410.editorapp.Data.HelpLoadManager;
import com.anatoly1410.editorapp.Data.SettingsManager;
import com.anatoly1410.editorapp.Data.VisualStylesManager;
import com.anatoly1410.editorapp.Data.XmlLangSyntaxParser;
import com.anatoly1410.editorapp.Domain.HelpManager;
import com.anatoly1410.editorapp.Domain.Interfaces.IDBHelper;
import com.anatoly1410.editorapp.Domain.Interfaces.IFileManager;
import com.anatoly1410.editorapp.Domain.Interfaces.IHelpLoadManager;
import com.anatoly1410.editorapp.Domain.Interfaces.ISettingsManager;
import com.anatoly1410.editorapp.Domain.Interfaces.IVisualStylesManager;
import com.anatoly1410.editorapp.Domain.Interfaces.IXmlLangSyntaxParser;
import com.anatoly1410.editorapp.Domain.SnippetManager;

import org.antlr.v4.runtime.misc.NotNull;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

/**
 * Created by 1 on 10.05.2017.
 */
@Module
public class DataModule {
    private Context context;

    public DataModule(Context context){
        this.context = context;
    }

    @Provides
    @Singleton
    public Context provideContext(){
        return context;
    }

    @Provides
    @Singleton
    public IDBHelper provideDBHelper(Context context){
        return new DBHelper(context);
    }

    @Provides
    @Singleton
    public IFileManager provideFileManager(Context context){
        return new FileManager(context);
    }

    @Provides
    @Singleton
    public IHelpLoadManager provideHelpLoadManager(IFileManager fileManager){
        return new HelpLoadManager(fileManager);
    }

    @Provides
    @Singleton
    public ISettingsManager provideSettingsManager(){
        return new SettingsManager();
    }

    @Provides
    @Singleton
    public IVisualStylesManager provideVisualStylesManager(IFileManager fileManager, Context context){
        return new VisualStylesManager(fileManager, context);
    }

    @Provides
    @Singleton
    public IXmlLangSyntaxParser provideXmlLangSyntaxParser(Context context){
        return new XmlLangSyntaxParser(context);
    }
}
