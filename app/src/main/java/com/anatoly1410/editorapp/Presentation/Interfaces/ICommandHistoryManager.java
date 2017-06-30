package com.anatoly1410.editorapp.Presentation.Interfaces;

import com.anatoly1410.editorapp.Domain.Interfaces.ICommand;

/**
 * Created by 1 on 16.05.2017.
 */

public interface ICommandHistoryManager {
    boolean isCommandHistoryWritingEnabled();
    void incCommandHistoryWritingEnabled(int subscribersCount);
    void decCommandHistoryWritingEnabled();
    void addCommand(ICommand command);
    void undoCommand();
    void redoCommand();
    boolean unitWithLast(ICommand command);
}
