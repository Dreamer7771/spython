package com.anatoly1410.editorapp.DataTests.DBHelper;

import android.content.SharedPreferences;

import com.anatoly1410.editorapp.BuildConfig;
import com.anatoly1410.editorapp.Data.SettingsManager;
import com.anatoly1410.editorapp.Domain.Interfaces.ISettingsManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.*;

/**
 * Created by 1 on 08.06.2017.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SettingsManager_tests {
    @Test
    public void checkSetSettings() {
        SettingsManager spySettingsMgr = spy(new SettingsManager());
        ISettingsManager.OnSettingsChangedEventListener mockListener
                = mock(ISettingsManager.OnSettingsChangedEventListener.class);
        spySettingsMgr.setSettingsChangedEventListener(mockListener);

        SharedPreferences mockSharedPref = mock(SharedPreferences.class);
        spySettingsMgr.setSettings(mockSharedPref);
        verify(mockListener).fireEvent();
    }
    @Test
    public void checkSetLastOpenDirectory() {
        SettingsManager spySettingsMgr = spy(new SettingsManager());
        SharedPreferences.Editor mockEditor = mock( SharedPreferences.Editor.class);
        doReturn(mockEditor).when(spySettingsMgr).getSettingsEditor();

        spySettingsMgr.setLastOpenDirectory("last_open_dir_path");

        verify(mockEditor).putString(ISettingsManager.APP_PREFERENCES_LAST_OPEN_DIR,"last_open_dir_path");
        verify(mockEditor).apply();
    }
    @Test
    public void checkSetBooleanSetting() {
        SettingsManager spySettingsMgr = spy(new SettingsManager());
        ISettingsManager.OnSettingsChangedEventListener mockListener
                = mock(ISettingsManager.OnSettingsChangedEventListener.class);
        spySettingsMgr.setSettingsChangedEventListener(mockListener);
        SharedPreferences.Editor mockEditor = mock( SharedPreferences.Editor.class);
        doReturn(mockEditor).when(spySettingsMgr).getSettingsEditor();

        spySettingsMgr.setBooleanSetting("bool_setting_name",true);

        verify(mockEditor).putBoolean("bool_setting_name",true);
        verify(mockEditor).apply();
        verify(mockListener).fireEvent();
    }
    @Test
    public void checkSetIntSetting() {
        SettingsManager spySettingsMgr = spy(new SettingsManager());
        ISettingsManager.OnSettingsChangedEventListener mockListener
                = mock(ISettingsManager.OnSettingsChangedEventListener.class);
        spySettingsMgr.setSettingsChangedEventListener(mockListener);
        SharedPreferences.Editor mockEditor = mock( SharedPreferences.Editor.class);
        doReturn(mockEditor).when(spySettingsMgr).getSettingsEditor();

        spySettingsMgr.setIntSetting("some_number",42);

        verify(mockEditor).putInt("some_number",42);
        verify(mockEditor).apply();
        verify(mockListener).fireEvent();
    }
}
