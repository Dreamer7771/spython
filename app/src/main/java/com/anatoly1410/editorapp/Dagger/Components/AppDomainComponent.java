package com.anatoly1410.editorapp.Dagger.Components;

import com.anatoly1410.editorapp.Dagger.Modules.DataModule;
import com.anatoly1410.editorapp.Dagger.Modules.DomainModule;
import com.anatoly1410.editorapp.Dagger.Scopes.DomainScope;
import com.anatoly1410.editorapp.Presentation.HelpActivity;
import com.anatoly1410.editorapp.Presentation.MainActivity;
import com.anatoly1410.editorapp.Presentation.OpenOrSaveFileActivity;
import com.anatoly1410.editorapp.Presentation.SettingsActivity;
import com.anatoly1410.editorapp.Presentation.SnippetActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by 1 on 10.05.2017.
 */
@DomainScope
@Component(dependencies = {AppDataComponent.class}, modules ={DomainModule.class})
public interface AppDomainComponent {
    void inject(MainActivity mainActivity);
    void inject(OpenOrSaveFileActivity openOrSaveFileActivity);
    void inject(SnippetActivity snippetActivity);
    void inject(HelpActivity helpActivity);
    void inject(SettingsActivity settingsActivity);
}
