package com.anatoly1410.editorapp.Domain;

import android.app.Application;
import android.content.pm.PackageManager;

import com.anatoly1410.editorapp.Dagger.Components.AppDataComponent;
import com.anatoly1410.editorapp.Dagger.Components.AppDomainComponent;
import com.anatoly1410.editorapp.Dagger.Components.DaggerAppDataComponent;
import com.anatoly1410.editorapp.Dagger.Components.DaggerAppDomainComponent;
import com.anatoly1410.editorapp.Dagger.Modules.DataModule;
import com.anatoly1410.editorapp.Dagger.Modules.DomainModule;
import com.anatoly1410.editorapp.Data.FileManager;
import com.anatoly1410.editorapp.Data.HelpLoadManager;
import com.anatoly1410.editorapp.Data.SettingsManager;
import com.anatoly1410.editorapp.R;
import com.anatoly1410.editorapp.Data.VisualStylesManager;

import java.io.File;

/**
 * Created by 1 on 30.12.2016.
 */
/*Main application class*/
public class MyApplication extends Application {

    private static AppDataComponent dataComponent;
    private static AppDomainComponent domainComponent;
    public static AppDomainComponent getDomainComponent(){
        return domainComponent;
    }
    public static AppDataComponent getDataComponent(){
        return dataComponent;
    }
    public static boolean firstMainActivityInit = true;
    /*On creation:
    * - initiates VisualStylesManager*/
    @Override
    public void onCreate() {
        super.onCreate();
        dataComponent = buildDataComponent();
        domainComponent = buildDomainComponent(dataComponent);

        createAppDirectories();
    }
    private void createAppDirectories()
    {
        PackageManager m = getPackageManager();
        String packageName = getPackageName();
        File wallpaperDirectory = new File("/sdcard/"+packageName+"/"+getResources().getString(R.string.projectsDirName));
        wallpaperDirectory.mkdirs();
    }

    protected AppDataComponent buildDataComponent(){
        return DaggerAppDataComponent.builder()
                .dataModule(new DataModule(this))
                .build();
    }
    protected AppDomainComponent buildDomainComponent(AppDataComponent appDataComponent){
        return DaggerAppDomainComponent.builder()
                .appDataComponent(appDataComponent)
                .domainModule(new DomainModule())
                .build();
    }

}
