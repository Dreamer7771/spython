package com.anatoly1410.editorapp.DomainTests;

import com.anatoly1410.editorapp.BuildConfig;
import com.anatoly1410.editorapp.Domain.CommandHistory;
import com.anatoly1410.editorapp.Domain.Interfaces.ICommand;
import com.anatoly1410.editorapp.Domain.TextChangeCommand;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.w3c.dom.Text;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by 1 on 09.06.2017.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class CommandHistory_tests {
    @Test
    public void checkAddCommand(){
        CommandHistory spyCommandHistory = spy(new CommandHistory());
        ArrayList<ICommand> commands = mock(ArrayList.class);
        when(commands.size()).thenReturn(2);

        doReturn(commands).when(spyCommandHistory).getCommandsList();

        spyCommandHistory.addCommand(new TextChangeCommand(10,"abc","abcdef"));
        verify(spyCommandHistory).incCurrentStatePosition();
        ArgumentCaptor<ICommand> argumentCaptor = ArgumentCaptor.forClass(ICommand.class);
        verify(commands).add(argumentCaptor.capture());
        TextChangeCommand command = (TextChangeCommand)argumentCaptor.getValue();
        assertEquals(command.getPosition(),10);
        assertEquals(command.getTextBefore(),"abc");
        assertEquals(command.getTextAfter(),"abcdef");
    }
    @Test
    public void checkUndoCommand(){
        CommandHistory spyCommandHistory = spy(new CommandHistory());
        doReturn(1).when(spyCommandHistory).getCurrentStatePosition();
        TextChangeCommand mockCommand = mock(TextChangeCommand.class);
        ArrayList<ICommand> mockCommandsList = mock(ArrayList.class);
        when(mockCommandsList.get(1)).thenReturn(mockCommand);
        doReturn(mockCommandsList).when(spyCommandHistory).getCommandsList();

        spyCommandHistory.undoCommand();

        verify(mockCommand).Unexecute();
        verify(spyCommandHistory).decCurrentStatePosition();
    }
    @Test
    public void checkRedoCommand(){
        CommandHistory spyCommandHistory = spy(new CommandHistory());
        doReturn(1).when(spyCommandHistory).getCurrentStatePosition();
        TextChangeCommand mockCommand = mock(TextChangeCommand.class);
        ArrayList<ICommand> mockCommandsList = mock(ArrayList.class);
        when(mockCommandsList.get(1)).thenReturn(mockCommand);
        when(mockCommandsList.size()).thenReturn(2);
        doReturn(mockCommandsList).when(spyCommandHistory).getCommandsList();

        spyCommandHistory.redoCommand();

        verify(mockCommand).Execute();
        verify(spyCommandHistory).incCurrentStatePosition();
    }

}
