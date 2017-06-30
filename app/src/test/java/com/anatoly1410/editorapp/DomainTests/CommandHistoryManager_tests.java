package com.anatoly1410.editorapp.DomainTests;

/**
 * Created by 1 on 09.06.2017.
 */

import com.anatoly1410.editorapp.BuildConfig;
import com.anatoly1410.editorapp.Data.SettingsManager;
import com.anatoly1410.editorapp.Domain.CommandHistory;
import com.anatoly1410.editorapp.Domain.CommandHistoryManager;
import com.anatoly1410.editorapp.Domain.Interfaces.ICommand;
import com.anatoly1410.editorapp.Domain.TabManager;
import com.anatoly1410.editorapp.Domain.TextChangeCommand;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class CommandHistoryManager_tests {
    @Test
    public void checkAddCommand(){
        TabManager mockTabMgr = mock(TabManager.class);
        SettingsManager mockSettingsMgr = mock(SettingsManager.class);
        CommandHistory mockHistory = mock(CommandHistory.class);
        CommandHistoryManager spyCommandHistMgr
                = spy(new CommandHistoryManager(mockTabMgr,mockSettingsMgr));
        doReturn(mockHistory).when(spyCommandHistMgr).getCommandHistoryForCurrentTab();
        spyCommandHistMgr.addCommand(new TextChangeCommand(1,"abc","abcs"));
        ArgumentCaptor<TextChangeCommand> argumentCaptor = ArgumentCaptor.forClass(TextChangeCommand.class);
        verify(mockHistory).addCommand(argumentCaptor.capture());
        assertEquals(argumentCaptor.getValue().getPosition(),1);
        assertEquals(argumentCaptor.getValue().getTextBefore(),"abc");
        assertEquals(argumentCaptor.getValue().getTextAfter(),"abcs");
    }
    @Test
    public void checkUndoCommand(){
        TabManager mockTabMgr = mock(TabManager.class);
        SettingsManager mockSettingsMgr = mock(SettingsManager.class);
        CommandHistory mockHistory = mock(CommandHistory.class);
        CommandHistoryManager spyCommandHistMgr
                = spy(new CommandHistoryManager(mockTabMgr,mockSettingsMgr));
        doReturn(mockHistory).when(spyCommandHistMgr).getCommandHistoryForCurrentTab();
        spyCommandHistMgr.undoCommand();
        verify(mockHistory).undoCommand();
    }
    @Test
    public void checkRedoCommand(){
        TabManager mockTabMgr = mock(TabManager.class);
        SettingsManager mockSettingsMgr = mock(SettingsManager.class);
        CommandHistory mockHistory = mock(CommandHistory.class);
        CommandHistoryManager spyCommandHistMgr
                = spy(new CommandHistoryManager(mockTabMgr,mockSettingsMgr));
        doReturn(mockHistory).when(spyCommandHistMgr).getCommandHistoryForCurrentTab();
        spyCommandHistMgr.redoCommand();
        verify(mockHistory).redoCommand();
    }
    @Test
    public void checkUnitWithLast() {
        TabManager mockTabMgr = mock(TabManager.class);
        SettingsManager mockSettingsMgr = mock(SettingsManager.class);
        CommandHistory mockHistory = mock(CommandHistory.class);
        when(mockHistory.getLastCommand()).thenReturn(new TextChangeCommand(1,"aa ","aa b"));
        CommandHistoryManager spyCommandHistMgr
                = spy(new CommandHistoryManager(mockTabMgr,mockSettingsMgr));
        doReturn(mockHistory).when(spyCommandHistMgr).getCommandHistoryForCurrentTab();

        boolean res = spyCommandHistMgr.unitWithLast(new TextChangeCommand(5,"abc","abcd"));
        assert(res);
    }
}
