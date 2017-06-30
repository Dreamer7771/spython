package com.anatoly1410.editorapp.IntegrationTests;

/**
 * Created by 1 on 13.06.2017.
 */

import android.content.SharedPreferences;

import com.anatoly1410.editorapp.BuildConfig;
import com.anatoly1410.editorapp.Data.SettingsManager;
import com.anatoly1410.editorapp.Domain.BlockedTextExtractor;
import com.anatoly1410.editorapp.Domain.BlockedTextSpinnerAdapter;
import com.anatoly1410.editorapp.Domain.Interfaces.ISettingsManager;
import com.anatoly1410.editorapp.Domain.SyntaxBlock;
import com.anatoly1410.editorapp.Presentation.Interfaces.IBlockedTextExtractor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class BlockedTextExtractor_SettingsManager_tests {
    @Test
    public void checkShowOnlyFunctions_AlphabeticOrder(){
        SharedPreferences mockSettings = mock(SharedPreferences.class);
        when(mockSettings.getInt(ISettingsManager.APP_PREFERENCES_SORT_ORDER,0)).thenReturn(0);
        when(mockSettings.contains(ISettingsManager.APP_PREFERENCES_SHOW_FUNCTIONS)).thenReturn(true);
        when(mockSettings.contains(ISettingsManager.APP_PREFERENCES_SHOW_CLASSES)).thenReturn(true);
        when(mockSettings.contains(ISettingsManager.APP_PREFERENCES_SORT_ORDER)).thenReturn(true);
        when(mockSettings.getBoolean(eq(ISettingsManager.APP_PREFERENCES_SHOW_FUNCTIONS),
                any(Boolean.class))).thenReturn(true);
        when(mockSettings.getBoolean(eq(ISettingsManager.APP_PREFERENCES_SHOW_CLASSES),
                any(Boolean.class))).thenReturn(false);

        BlockedTextSpinnerAdapter mockAdapter = mock(BlockedTextSpinnerAdapter.class);
        SettingsManager spySettingsMgr = spy(new SettingsManager());
        spySettingsMgr.setSettings(mockSettings);
        BlockedTextExtractor spyBlockedTextExtractor = spy(new BlockedTextExtractor(spySettingsMgr));
        spyBlockedTextExtractor.initPatternsList();
        spyBlockedTextExtractor.setBlockedTextSpinnerAdapter(mockAdapter);
        assert(spyBlockedTextExtractor.areFunctionsShowed());
        assert(!spyBlockedTextExtractor.areClassesShowed());
        assert(spyBlockedTextExtractor.sortBlocksLexicographically());
        String text = "def f2():\n pass\n\ndef f1():\n pass\n\nclass A:\n a = 3\n\nclass B:\n b = 4";
        spyBlockedTextExtractor.extractBlockedTextFromStringAsync(text);
        ArrayList<SyntaxBlock> blocks = spyBlockedTextExtractor.getDisplayedBlocks();
        assertEquals(blocks.size(),2);
        assertEquals(blocks.get(0).header,"f1()");
        assertEquals(blocks.get(0).type, BlockedTextExtractor.FUNC_BLOCK_TYPE);
        assertEquals(blocks.get(0).startPos, 17);
        assertEquals(blocks.get(1).header,"f2()");
        assertEquals(blocks.get(1).type, BlockedTextExtractor.FUNC_BLOCK_TYPE);
        assertEquals(blocks.get(1).startPos, 0);
    }

    @Test
    public void checkShowOnlyClasses_AlphabeticOrder(){
        SharedPreferences mockSettings = mock(SharedPreferences.class);
        when(mockSettings.getInt(ISettingsManager.APP_PREFERENCES_SORT_ORDER,0)).thenReturn(0);
        when(mockSettings.contains(ISettingsManager.APP_PREFERENCES_SHOW_FUNCTIONS)).thenReturn(true);
        when(mockSettings.contains(ISettingsManager.APP_PREFERENCES_SHOW_CLASSES)).thenReturn(true);
        when(mockSettings.contains(ISettingsManager.APP_PREFERENCES_SORT_ORDER)).thenReturn(true);
        when(mockSettings.getBoolean(eq(ISettingsManager.APP_PREFERENCES_SHOW_FUNCTIONS),
                any(Boolean.class))).thenReturn(false);
        when(mockSettings.getBoolean(eq(ISettingsManager.APP_PREFERENCES_SHOW_CLASSES),
                any(Boolean.class))).thenReturn(true);

        BlockedTextSpinnerAdapter mockAdapter = mock(BlockedTextSpinnerAdapter.class);
        SettingsManager spySettingsMgr = spy(new SettingsManager());
        spySettingsMgr.setSettings(mockSettings);
        BlockedTextExtractor spyBlockedTextExtractor = spy(new BlockedTextExtractor(spySettingsMgr));
        spyBlockedTextExtractor.initPatternsList();
        spyBlockedTextExtractor.setBlockedTextSpinnerAdapter(mockAdapter);
        assert(!spyBlockedTextExtractor.areFunctionsShowed());
        assert(spyBlockedTextExtractor.areClassesShowed());
        assert(spyBlockedTextExtractor.sortBlocksLexicographically());
        String text = "def f2():\n pass\n\ndef f1():\n pass\n\nclass A:\n a = 3\n\nclass B:\n b = 4";
        spyBlockedTextExtractor.extractBlockedTextFromStringAsync(text);
        ArrayList<SyntaxBlock> blocks = spyBlockedTextExtractor.getDisplayedBlocks();
        assertEquals(blocks.size(),2);
        assertEquals(blocks.get(0).header,"A");
        assertEquals(blocks.get(0).type, BlockedTextExtractor.CLASS_BLOCK_TYPE);
        assertEquals(blocks.get(0).startPos, 34);
        assertEquals(blocks.get(1).header,"B");
        assertEquals(blocks.get(1).type, BlockedTextExtractor.CLASS_BLOCK_TYPE);
        assertEquals(blocks.get(1).startPos, 51);
    }
    @Test
    public void checkShowAll_AlphabeticOrder(){
        SharedPreferences mockSettings = mock(SharedPreferences.class);
        when(mockSettings.getInt(ISettingsManager.APP_PREFERENCES_SORT_ORDER,0)).thenReturn(0);
        when(mockSettings.contains(ISettingsManager.APP_PREFERENCES_SHOW_FUNCTIONS)).thenReturn(true);
        when(mockSettings.contains(ISettingsManager.APP_PREFERENCES_SHOW_CLASSES)).thenReturn(true);
        when(mockSettings.contains(ISettingsManager.APP_PREFERENCES_SORT_ORDER)).thenReturn(true);
        when(mockSettings.getBoolean(eq(ISettingsManager.APP_PREFERENCES_SHOW_FUNCTIONS),
                any(Boolean.class))).thenReturn(true);
        when(mockSettings.getBoolean(eq(ISettingsManager.APP_PREFERENCES_SHOW_CLASSES),
                any(Boolean.class))).thenReturn(true);

        BlockedTextSpinnerAdapter mockAdapter = mock(BlockedTextSpinnerAdapter.class);
        SettingsManager spySettingsMgr = spy(new SettingsManager());
        spySettingsMgr.setSettings(mockSettings);
        BlockedTextExtractor spyBlockedTextExtractor = spy(new BlockedTextExtractor(spySettingsMgr));
        spyBlockedTextExtractor.initPatternsList();
        spyBlockedTextExtractor.setBlockedTextSpinnerAdapter(mockAdapter);
        assert(spyBlockedTextExtractor.areFunctionsShowed());
        assert(spyBlockedTextExtractor.areClassesShowed());
        assert(spyBlockedTextExtractor.sortBlocksLexicographically());
        String text = "def f2():\n pass\n\ndef f1():\n pass\n\nclass A:\n a = 3\n\nclass B:\n b = 4";
        spyBlockedTextExtractor.extractBlockedTextFromStringAsync(text);
        ArrayList<SyntaxBlock> blocks = spyBlockedTextExtractor.getDisplayedBlocks();
        assertEquals(blocks.size(),4);
        assertEquals(blocks.get(0).header,"A");
        assertEquals(blocks.get(0).type, BlockedTextExtractor.CLASS_BLOCK_TYPE);
        assertEquals(blocks.get(0).startPos, 34);
        assertEquals(blocks.get(1).header,"B");
        assertEquals(blocks.get(1).type, BlockedTextExtractor.CLASS_BLOCK_TYPE);
        assertEquals(blocks.get(1).startPos, 51);
        assertEquals(blocks.get(2).header,"f1()");
        assertEquals(blocks.get(2).type, BlockedTextExtractor.FUNC_BLOCK_TYPE);
        assertEquals(blocks.get(2).startPos, 17);
        assertEquals(blocks.get(3).header,"f2()");
        assertEquals(blocks.get(3).type, BlockedTextExtractor.FUNC_BLOCK_TYPE);
        assertEquals(blocks.get(3).startPos, 0);
    }
    @Test
    public void checkShowAll_ByCodeOrder(){
        SharedPreferences mockSettings = mock(SharedPreferences.class);
        when(mockSettings.getInt(ISettingsManager.APP_PREFERENCES_SORT_ORDER,0)).thenReturn(1);
        when(mockSettings.contains(ISettingsManager.APP_PREFERENCES_SHOW_FUNCTIONS)).thenReturn(true);
        when(mockSettings.contains(ISettingsManager.APP_PREFERENCES_SHOW_CLASSES)).thenReturn(true);
        when(mockSettings.contains(ISettingsManager.APP_PREFERENCES_SORT_ORDER)).thenReturn(true);
        when(mockSettings.getBoolean(eq(ISettingsManager.APP_PREFERENCES_SHOW_FUNCTIONS),
                any(Boolean.class))).thenReturn(true);
        when(mockSettings.getBoolean(eq(ISettingsManager.APP_PREFERENCES_SHOW_CLASSES),
                any(Boolean.class))).thenReturn(true);

        BlockedTextSpinnerAdapter mockAdapter = mock(BlockedTextSpinnerAdapter.class);
        SettingsManager spySettingsMgr = spy(new SettingsManager());
        spySettingsMgr.setSettings(mockSettings);
        BlockedTextExtractor spyBlockedTextExtractor = spy(new BlockedTextExtractor(spySettingsMgr));
        spyBlockedTextExtractor.initPatternsList();
        spyBlockedTextExtractor.setBlockedTextSpinnerAdapter(mockAdapter);
        assert(spyBlockedTextExtractor.areFunctionsShowed());
        assert(spyBlockedTextExtractor.areClassesShowed());
        assert(!spyBlockedTextExtractor.sortBlocksLexicographically());
        String text = "def f2():\n pass\n\ndef f1():\n pass\n\nclass A:\n a = 3\n\nclass B:\n b = 4";
        spyBlockedTextExtractor.extractBlockedTextFromStringAsync(text);
        ArrayList<SyntaxBlock> blocks = spyBlockedTextExtractor.getDisplayedBlocks();
        assertEquals(blocks.size(),4);
        assertEquals(blocks.get(0).header,"f2()");
        assertEquals(blocks.get(0).type, BlockedTextExtractor.FUNC_BLOCK_TYPE);
        assertEquals(blocks.get(0).startPos, 0);
        assertEquals(blocks.get(1).header,"f1()");
        assertEquals(blocks.get(1).type, BlockedTextExtractor.FUNC_BLOCK_TYPE);
        assertEquals(blocks.get(1).startPos, 17);
        assertEquals(blocks.get(2).header,"A");
        assertEquals(blocks.get(2).type, BlockedTextExtractor.CLASS_BLOCK_TYPE);
        assertEquals(blocks.get(2).startPos, 34);
        assertEquals(blocks.get(3).header,"B");
        assertEquals(blocks.get(3).type, BlockedTextExtractor.CLASS_BLOCK_TYPE);
        assertEquals(blocks.get(3).startPos, 51);
    }
}
