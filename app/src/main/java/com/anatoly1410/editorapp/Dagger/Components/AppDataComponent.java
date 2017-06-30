package com.anatoly1410.editorapp.Dagger.Components;

import android.content.Context;

import com.anatoly1410.editorapp.Dagger.Modules.DataModule;
import com.anatoly1410.editorapp.Data.FileManager;
import com.anatoly1410.editorapp.Data.HelpLoadManager;
import com.anatoly1410.editorapp.Data.SettingsManager;
import com.anatoly1410.editorapp.Data.VisualStylesManager;
import com.anatoly1410.editorapp.Data.XmlLangSyntaxParser;
import com.anatoly1410.editorapp.Domain.Interfaces.IDBHelper;
import com.anatoly1410.editorapp.Domain.Interfaces.IFileManager;
import com.anatoly1410.editorapp.Domain.Interfaces.IHelpLoadManager;
import com.anatoly1410.editorapp.Domain.Interfaces.ISettingsManager;
import com.anatoly1410.editorapp.Domain.Interfaces.IVisualStylesManager;
import com.anatoly1410.editorapp.Domain.Interfaces.IXmlLangSyntaxParser;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by 1 on 10.05.2017.
 */
@Singleton
@Component(modules = {DataModule.class})
public interface AppDataComponent {
    IDBHelper dBHelper();
    IFileManager fileManager();
    IHelpLoadManager helpLoadManager();
    ISettingsManager settingsManager();
    IVisualStylesManager visualStylesManager();
    IXmlLangSyntaxParser xmlLangSyntaxParser();
    Context context();
}
