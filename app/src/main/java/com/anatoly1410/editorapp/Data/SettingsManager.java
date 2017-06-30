package com.anatoly1410.editorapp.Data;

import android.content.SharedPreferences;

import com.anatoly1410.editorapp.Domain.Interfaces.ISettingsManager;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by 1 on 13.03.2017.
 */

public class SettingsManager implements ISettingsManager {

    private SharedPreferences mSettings;

    OnSettingsChangedEventListener mSettingsChangedEventListener;

    public void setSettingsChangedEventListener(OnSettingsChangedEventListener listener) {
        mSettingsChangedEventListener = listener;
    }


    public SettingsManager(){

    }

    //for testing
    public SharedPreferences.Editor getSettingsEditor(){
        if(mSettings == null){
            return null;
        }

        return mSettings.edit();
    }

    public void setSettings(SharedPreferences settings) {
        mSettings = settings;
        if(mSettingsChangedEventListener != null){
            mSettingsChangedEventListener.fireEvent();
        }
    }

    public void setLastOpenDirectory(String path){
        SharedPreferences.Editor editor = getSettingsEditor();
        editor.putString(APP_PREFERENCES_LAST_OPEN_DIR, path);
        editor.apply();
    }

    public String getLastOpenDirectory(){
        String path = mSettings.getString(APP_PREFERENCES_LAST_OPEN_DIR,"");
        return path;
    }

    public void setBooleanSetting(String settingName, boolean value){
        SharedPreferences.Editor editor = getSettingsEditor();
        editor.putBoolean(settingName, value);
        editor.apply();
        if(mSettingsChangedEventListener != null){
            mSettingsChangedEventListener.fireEvent();
        }
    }

    public void setIntSetting(String settingName, int value){
        SharedPreferences.Editor editor = getSettingsEditor();
        editor.putInt(settingName, value);
        editor.apply();
        if(mSettingsChangedEventListener != null){
            mSettingsChangedEventListener.fireEvent();
        }
    }

    public boolean getBooleanSetting(String settingName){
        boolean value;
        if(mSettings.contains(settingName)){
            value =  mSettings.getBoolean(settingName,false);
        }else{
            value =  getDefaultBooleanSetting(settingName);
        }
        return value;
    }


    public int getIntSetting(String settingName){
        int value;
        if(mSettings.contains(settingName)){
            value =  mSettings.getInt(settingName,0);
        }else{
            value =  getDefaultIntSetting(settingName);
        }
        return value;
    }


    private boolean getDefaultBooleanSetting(String settingName){
        boolean value = false;
        switch(settingName){
            case APP_PREFERENCES_HIGHLIGHT_BY_DEFAULT:
                value = true;
                break;
            case APP_PREFERENCES_EXTRACT_BLOCKS_BY_DEFAULT:
                value = true;
                break;
            case APP_PREFERENCES_AUTOCOMP_BY_DEFAULT:
                value = true;
                break;
        }
        return value;
    }
    private int getDefaultIntSetting(String settingName){
        int value = 0;
        switch(settingName){
            case APP_PREFERENCES_COMMANDS_NUM_BY_DEFAULT:
                value = 0;
                break;
        }
        return value;
    }
}
